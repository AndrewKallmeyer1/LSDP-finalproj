package final_project

import org.apache.spark.sql.SparkSession
import scala.util.Random
import scala.collection.mutable

object main {
    def main (args: Array[String]): Unit = {
        if (args.length != 2){
            System.err.println("Usage: main <inputfile> <outputfile> ")
            System.exit(1)
        }

        val inputPath = args(0)
        val outputPath = args(1)

        val spark = SparkSession.builder
            .appName("Maximal Matching")
            .getOrCreate()

        import spark.implicits._

        val edges = spark.read
            .option("header", "false")
            .csv(inputPath)
            .rdd
            .map(row => (row.getString(0).toInt, row.getString(1).toInt))
            .collect
            .toBuffer

        val shuffledEdges = Random.shuffle(edges)

        val usedVertices = mutable.Set[Int]()
        val matching = scala.collection.mutable.ArrayBuffer[(Int, Int)]()

        for ((u, v) <- shuffledEdges) {
            if (!usedVertices.contains(u) && !usedVertices.contains(v)) {
                matching.append((u, v))
                usedVertices += u
                usedVertices += v 
            }
        }

        val matchingRDD = spark.sparkContext.parallelize(matching)
        matchingRDD.map { case (u, v) => s"$u,$v" }
            .saveAsTextFile(outputPath)

        spark.stop()
    }
}