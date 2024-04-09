
# **Problem:**
- Given a list of text boxes/outlines from an image, implement an algorithm that groups these boxes in logical groups.
- The boxes that are sufficiently close and that make sense to be together will form a group. 
- The coordinates are relative to (0,0), representing the upper left corner.

## **Input:**
a list of boxes
## **Output:**
a list of groups

# **Notes:**
- The boxes contain only the coordinates, without the content, so the grouping can be done only based on coordinates.
- The boxes can be considered to be oriented at zero degrees.
- The order of the boxes is random.

# **Solutions:**

For java 17 the following VM options are needed, because Apache Spark is not fully compatible yet:
```text
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED 
--add-opens=java.base/java.nio=ALL-UNNAMED 
--add-opens=java.base/java.lang.invoke=ALL-UNNAMED 
--add-opens=java.base/java.util=ALL-UNNAMED
```

## **Solution #1 - Proximity Solution:**

- Service name: BoxService
- Iterates over the input boxes, splits into groups relative to a specified distance.
- Uses an algorithm that recursively finds the closest box, constraint by a distance threshold and adds it to a group.

Run command:
```text
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dapp.use-proximity-solution=true"
other arguments that can be overridden: 
-Dsolution.file-path=src/main/resources/static/input.json
-Dsolution.distance-threshold=65
```

## **Solution #2 - Clustering Solution:**
- Service name: ClusteringService
- Used Apache Spark to create clusters of boxes.
  * It takes @numberOfClusters random coordinates from a json file as cluster centers
  * then it forms clusters based on the closest coordinates to these center
  * then it calculates the mean distance and create a new center point aka the center of the cluster
  * then it repeats these steps until the deviation for @maxIterations times

Run command:
```text
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dapp.use-proximity-solution=false"
other arguments that can be overridden: 
-Dsolution.file-path=src/main/resources/static/input.json
-Dsolution.clusters=5
-Dsolution.max-iterations=20
```