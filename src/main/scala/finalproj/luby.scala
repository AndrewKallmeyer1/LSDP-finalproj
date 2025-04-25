package final_project

import org.apache.spark.sql.SparkSession
import scala.util.Random

object luby {
    def main(args: Array[String]): Unit = {
        
        val spark = SparkSession.builder.appName("Luby Matching").getOrCreate()
        import spark.implicits._

        val inputPath = args(0)
        val outputPath = args(1)

        val edges: org.apache.spark.rdd.RDD[(String, String)] = spark.read.textFile(inputPath)
            .map(_.split(","))
            .filter(_.length == 2)
            .map(arr => (arr(0).trim, arr(1).trim))
            .filter(t => t._1 != t._2)
            .distinct()
            .rdd
            .cache()

        val vertices = edges.flatMap { case (u, v) => Seq(u, v) }.distinct()

        var matched = spark.sparkContext.emptyRDD[(String, String)]
        var remainingEdges = edges 

        var iteration = 0
        
        while (!remainingEdges.isEmpty()) {
            val rnd = new Random()
            val edgeWithPriority = remainingEdges.map(e => (e, rnd.nextDouble())).cache()
            
            val maxPriorityEdges = edgeWithPriority
                .flatMap { case ((u, v), p) => Seq((u, ((u, v), p)), (v, ((u, v), p))) }
                .reduceByKey((a, b) => if (a._2 > b._2) a else b)
                .map(_._2._1)
                .distinct()

            val seen = scala.collection.mutable.Set[String]()
            val validEdges = maxPriorityEdges
                .collect()
                .filter { case (u, v) =>
                    if (seen.contains(u) || seen.contains(v)) false
                    else {
                        seen += u
                        seen += v
                        true
                    }
                }

            val filteredEdgesRDD = spark.sparkContext.parallelize(validEdges)


            val matchedVerticesSet = validEdges.flatMap { case (u, v) => Seq(u, v) }.toSet
            val matchedVerticesBC = spark.sparkContext.broadcast(matchedVerticesSet)

            remainingEdges = remainingEdges
                .filter { case (u, v) =>
                    !matchedVerticesBC.value.contains(u) && !matchedVerticesBC.value.contains(v)
                }

            matched = matched.union(filteredEdgesRDD)

            println(s"Iteration $iteration: added ${filteredEdgesRDD.count()} edges")
            println(s"Remaining edges: ${remainingEdges.count()}")

            iteration += 1
        }


        matched.map { case (u, v) => s"$u,$v" }
            .repartition(1)
            .saveAsTextFile(outputPath)

        spark.stop()
    }
}