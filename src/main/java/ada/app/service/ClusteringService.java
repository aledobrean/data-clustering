package ada.app.service;

import ada.app.model.Box;
import ada.app.model.Point;
import ada.app.util.CoordinatesParser;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ClusteringService {
    private static final String SOLUTION_NAME = "clustering_solution";

    private final Logger logger = LoggerFactory.getLogger(ClusteringService.class);
    private final CoordinatesParser coordinatesParser;
    private final JavaSparkContext sparkContext;

    @Value("${solution.file-path}")
    private String filePath;
    @Value("${solution.clusters}")
    private int numberOfClusters;
    @Value("${solution.max-iterations}")
    private int maxIterations;

    @Autowired
    public ClusteringService(CoordinatesParser coordinatesParser, JavaSparkContext sparkContext) {
        this.coordinatesParser = coordinatesParser;
        this.sparkContext = sparkContext;
    }

    public List<List<Box>> solve() {
        List<Box> inputBoxes = coordinatesParser.parseJson(filePath);
        logger.info("solution={}, event=process_input, number_of_boxes={}, number_of_clusters_required={}",
                SOLUTION_NAME, inputBoxes.size(), numberOfClusters);

        return getClusters(inputBoxes, numberOfClusters, maxIterations);
    }

    /**
     * Used Apache Spark to create clusters of boxes.
     * It takes @numberOfClusters random coordinates from a json file as cluster centers
     * then it forms clusters based on the closest coordinates to these center
     * then it calculates the mean distance and create a new center point aka the center of the cluster
     * then it repeats these steps until the deviation for @maxIterations times
     */
    private List<List<Box>> getClusters(List<Box> input, int numberOfClusters, int maxIterations) {
        List<Point> centerCoordinates = input.stream().map(Box::getCenter).toList();

        List<Vector> data = new ArrayList<>();
        for (Point point : centerCoordinates) {
            data.add(Vectors.dense(point.getX(), point.getY()));
        }
        JavaRDD<Vector> points = sparkContext.parallelize(data);

        // Run KMeans clustering
        long seed = new Random().nextLong();
        KMeans kMeans = new KMeans();
        kMeans.setK(numberOfClusters);
        kMeans.setMaxIterations(maxIterations);
        kMeans.setSeed(seed);
        logger.info("solution={}, event=start_clustering, number_of_clusters={}, max_iterations={}, seed={}",
                SOLUTION_NAME, numberOfClusters, maxIterations, seed);

        KMeansModel model = kMeans.run(points.rdd());

        // Group points by cluster label
        List<List<Point>> clusters = new ArrayList<>();
        for (int i = 0; i < numberOfClusters; i++) {
            clusters.add(new ArrayList<>());
        }

        for (Point point : centerCoordinates) {
            int clusterIndex = model.predict(org.apache.spark.mllib.linalg.Vectors.dense(point.getX(), point.getY()));
            clusters.get(clusterIndex).add(point);
        }

        // Convert clusters to boxes
        List<List<Box>> resultingBoxes = mapPointsToBoxes(clusters, input);
        logger.info("solution={}, event=_successful_clustering", SOLUTION_NAME);

        sparkContext.stop();
        return resultingBoxes;
    }

    /**
     * Maps the resulting points to the initial boxes.
     *
     * @return the transformed list of clusters
     */
    private List<List<Box>> mapPointsToBoxes(List<List<Point>> clusters, List<Box> input) {
        List<List<Box>> resultingBoxes = new ArrayList<>();
        for (List<Point> cluster : clusters) {
            List<Box> boxCluster = new ArrayList<>();
            for (Point point : cluster) {
                Optional<Box> box = input.stream().filter(b -> b.getCenter().getX() == point.getX() && b.getCenter().getY() == point.getY()).findFirst();
                boxCluster.add(box.orElseThrow());
            }
            resultingBoxes.add(boxCluster);
        }
        return resultingBoxes;
    }
}
