# Final Project: Large Scale Data Processing

## Team Members 
- Andrew Kallmeyer
- Evan Taylor
- Gabriel Geyer 

## Our Objective: 
Algorithmically find the maximum matching for large CSV graphs. 

---

## [Link to ZIP files](https://drive.google.com/drive/folders/1XMKILPq0ExdU8j-bdMRRuXzbtkc5rwBE?usp=share_link)
- Add expanded "original-files" to data/ folder
- Add expanded "output-files" to outputs/ folder
- Use verifier as usual


## CSV Graph Files and Matching Findings

| CSV Graph File | Original Edge Size | Matching Result in Edge Size |
|:---|:---|:---|
| log_normal_100.csv | 2761 | 47 |
| musae_ENGB_edges.csv | 35324 | 2261 |
| soc-pokec-relationships.csv | 22301964 | 588076 |
| soc-LiveJournal1 | 42851237 | 1716930 |
| twitter_original_edges | 63555749 | 94269 |
| com-orkut.ungraph | 117185083 | 1391207 |

## Computation Estimations and Runtimes

Local Runs (Files 1 and 2):
- 6-core CPU, 16GB RAM

Remote Runs (Files 3-6):
- 2x4 cores (vCPUs)

Runtime Estimations:

| CSV Graph File | Runtime | 
|:---|:---|
| log_normal_100.csv | 9s |
| musae_ENGB_edges.csv | 9s |
| soc-pokec-relationships.csv | 1m 39s |
| soc-LiveJournal1 | 8m 23s |
| twitter_original_edges | 6m 59s |
| com-orkut.ungraph | 36m 25s |


---

## Algorithm Approach

##### Approach: 
This project addresses the challenge of computing maximal matchings in graphs of various sizes, ranging from small to extremely large. To tackle this, we implement two distinct algorithms: a randomized greedy algorithm for smaller graphs and Luby’s parallel algorithm for larger ones. The algorithm choice is determined by the graph’s edge count to ensure computational efficiency across different scenarios.

For graphs with a smaller number of edges (specifically 2,761 and 35,324), we use a randomized greedy algorithm implemented in main.scala. This algorithm begins by reading the edge list from a CSV file and randomly shuffling the edges to mitigate bias that could arise from fixed orderings. It initializes an empty set to track used vertices and an empty list for the matching. It then iterates through the shuffled edges, adding an edge to the matching only if both its vertices are unused, and marking them as used. This process continues until all edges are processed, resulting in a maximal matching. This approach guarantees a maximal matching, which is at least 1/2 the size of a maximum matching. This bound is well-known and follows from standard matching theory (Vazirani, Approximation Algorithms, 2001). The randomized shuffling helps prevent pathological cases arising from adversarial edge orderings, offering robust practical performance on small- to medium-sized graphs.

For larger graphs—those with edge counts of 22,301,964; 42,851,237; 63,555,749; and 117,185,083—we implement Luby’s algorithm in luby.scala, leveraging Apache Spark for distributed computation. This algorithm proceeds iteratively: in each round, it assigns a random priority (a float between 0 and 1) to each edge, then selects the highest-priority edge incident to each vertex. Edges that share vertices are filtered out to maintain a valid matching, and selected edges are added to the result. Their endpoints are then removed from consideration. This process repeats until no edges remain. Luby’s algorithm has a theoretical runtime of O(log(|V|)) iterations with high probability, making it especially suited for large-scale parallel computation. This theoretical bound on round complexity (A Simple Parallel Algorithm for the Maximal Independent Set Problem, 1986) is critical for ensuring scalability in large-scale distributed environments.

##### Advantages: 
The randomized greedy algorithm is well-suited for smaller graphs due to its simplicity, low overhead, and ease of implementation. The random shuffle avoids deterministic orderings that could degrade performance, and although it does not offer a strong approximation guarantee beyond the standard bound (a maximal matching is at least half the size of a maximum matching), it performs reliably in practice for small to medium graphs.

In contrast, Luby’s algorithm is highly effective for large graphs. Its parallel nature enables fast processing by distributing work across many compute nodes via Apache Spark. Its theoretical guarantees, including the O(log(n)) iteration bound, ensure scalability as the graph size grows. The algorithm consistently produces maximal matchings, verified by monitoring the diminishing edge set over iterations.

Looking at our approach as a whole, both algorithms are guaranteed to produce a maximal matching, i.e. produce a 1/2-approximation of the maximum matching size. 

##### Scalability:
The implementation in Apache Spark allows Luby’s algorithm to process large graphs efficiently. Spark RDDs are used to maintain the current edge set and perform transformations across worker nodes in parallel. Each iteration of the algorithm consists of map-reduce steps to compute priorities, identify maximal edges, and prune the graph. This architecture scales horizontally with the number of machines and is well-suited for high-throughput environments.

In contrast, the greedy algorithm is written for single-machine execution, with time complexity O(|E|). While fast and efficient for small graphs, its sequential design limits its applicability to large datasets.

Our strategy for unseen test cases would be to first evaluate the graph’s size. For graphs with fewer than approximately 100,000 edges, we apply the randomized greedy algorithm for its simplicity and speed. For larger graphs, we switch to Luby’s algorithm to exploit parallelism and ensure efficient scaling. This dual-algorithm approach allows us to balance simplicity and scalability, adapting effectively to graphs of any size.

---

## File Naming

- Input Files: data/*.csv
- Output Files: outputs/*_solution.csv

---

## Additional References 

- Versioning: Scala 2.12, Spark 3.5
- Main Class (Files 1, 2): final_project.main
- Main Class (Files 3-6): final_project.luby
- Verifier Class: final_project.matching_verifier
- Package: finalproj_2.12-1.0.jar

Running Local Files: 

1. Add input file to data/ folder 

2. Call to root directory: 

```bash 
cd local-root
```

3. Create package: 

```bash
sbt clean package
```

4. Run the input file to find matches

```bash
spark-submit --master "local[*]" --class "final_project.main" target/scala-2.12/finalproj_2.12-1.0.jar data/XXX.csv outputs/XXX_solution_folder
```

5. Concatenate output files into CSV format

```bash
cat outputs/musae_ENGB_edges_solution/part-* > outputs/XXX_solution.csv
```

6. Verify

```bash
spark-submit --master "local[*]" --class "final_project.matching_verifier" target/scala-2.12/finalproj_2.12-1.0.jar data/XXX.csv outputs/XXX_solution.csv
```

Running Remote Files on GCP: 

Cluster Specs: 
- Name: user-csci3390-finalproj-cluster
- Region: us-central1 (Subregion: Any)
- Image Type and Version: 2.2-debian12
- Primary Network and Subnetwork: default
- Worker Nodes:
    - Primary Disk Type: Balanced Persistent Disk
    - Machine Type: n2-standard-2
    - Number of Worker Nodes: 4
    - Primary Disk Size: 100GB
- Scheduled Deletion: Delete after a cluster idle time (2 hours)

Bucket Specs: 
- Name: user-csci3390-finalproj-bucket

1. Add original data files and jar file to bucket 
2. Job 1:
    1. Cluster: user-csci3390-finalproj-cluster
    2. Job Type: Spark
    3. Main Class or jar: final_projecy.luby
    4. Jar files: gs://user-csci3390-finalproj-bucket/finalproj_2.12-1.0.jar
    5. Files: gs://user-csci3390-finalproj-bucket/*.csv
    6. Arguments: 
        1. gs://user-csci3390-finalproj-bucket/*.csv
        2. gs://user-csci3390-finalproj-bucket/outputs/*_solution
    7. Max restarts per hour: 1
3. Go into local terminal and run: 

```bash
gsutil compose "gs://user-csci3390-finalproj-bucket/outputs/*_solution/part-*" gs://user-csci3390-finalproj-bucket/outputs/*_solution.csv
```
4. Job 2: 
    1. Cluster: user-csci3390-finalproj-cluster
    2. Job Type: Spark
    3. Main Class: final_project.matching_verifier
    4. Jar files: gs://user-csci3390-finalproj-bucket/finalproj_2.12-1.0.jar
    5. Files: 
        1. gs://user-csci3390-finalproj-bucket/*.csv
        2. gs://user-csci3390-finalproj-bucket/outputs/*_solution.csv
    6. Arguments: 
        1. gs://user-csci3390-finalproj-bucket/*.csv
        2. gs://user-csci3390-finalproj-bucket/outputs/*_solution.csv
    7. Max restarts per hour: 1


