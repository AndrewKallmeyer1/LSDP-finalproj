# Final Project: Large Scale Data Processing

## Team Members 
- Andrew Kallmeyer
- Evan Taylor
- Gabriel Geyer 

## Our Objective: 
Algorithmically find the maximum matching for large CSV graphs. 

---

## CSV Graph Files and Matching Findings

| CSV Graph File | Original Edge Size | Matching Result in Edge Size |
|:---|:---|:---|
| log_normal_100.csv | 2761 | 47 |
| musae_ENGB_edges.csv | 35324 | 2261 |
| soc-pokec-relationships.csv | 22301964 | 588076 |

## Computation Estimations and Runtimes

Local Runs (Files 1 and 2):
- 6-core CPU, 16GB RAM

Remote Runs (Files 3-6):

- File 3 (soc-pokec-relationships.csv): 2x4 cores (vCPUs)

Runtime Estimations:

| CSV Graph File | Runtime | 
|:---|:---|
| log_normal_100.csv | 9s |
| musae_ENGB_edges.csv | 9s |
| soc-pokec-relationships.csv | 1m 39s |



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
- Main Class: final_project.main
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


