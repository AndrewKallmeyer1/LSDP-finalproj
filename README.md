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

Approach: 

Advantages: 

Scalability: 

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
    2a. Cluster: user-csci3390-finalproj-cluster
    2b. Job Type: Spark
    2c. Main Class or jar: final_projecy.luby
    2d. Jar files: gs://user-csci3390-finalproj-bucket/finalproj_2.12-1.0.jar
    2e. Files: gs://user-csci3390-finalproj-bucket/*.csv
    2f. Arguments: 
        i. gs://user-csci3390-finalproj-bucket/*.csv
        ii. gs://user-csci3390-finalproj-bucket/outputs/*_solution
    2g. Max restarts per hour: 1
3. Go into local terminal and run: 

```bash
gsutil compose "gs://user-csci3390-finalproj-bucket/outputs/*_solution/part-*" gs://user-csci3390-finalproj-bucket/outputs/*_solution.csv
```
4. Job 2: 
    4a. Cluster: user-csci3390-finalproj-cluster
    4b. Job Type: Spark
    4c. Main Class: final_project.matching_verifier
    4d. Jar files: gs://user-csci3390-finalproj-bucket/finalproj_2.12-1.0.jar
    4e. Files: 
        i. gs://user-csci3390-finalproj-bucket/*.csv
        ii. gs://user-csci3390-finalproj-bucket/outputs/*_solution.csv
    4f. Arguments: 
        i. gs://user-csci3390-finalproj-bucket/*.csv
        ii. gs://user-csci3390-finalproj-bucket/outputs/*_solution.csv
    4g. Max restarts per hour: 1


