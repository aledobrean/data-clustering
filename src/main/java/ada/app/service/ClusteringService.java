package ada.app.service;

import ada.app.model.Box;
import ada.app.util.CoordinatesParser;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.apache.spark.sql.functions.expr;

@Service
public class ClusteringService {
    private static final String SOLUTION_NAME = "clustering_solution";

    private final Logger logger = LoggerFactory.getLogger(ClusteringService.class);
    private final CoordinatesParser coordinatesParser;

    @Value("${solution.file-path}")
    private String filePath;
    @Value("${solution.distance-threshold}")
    private double distanceThreshold;
    @Value("${solution.clusters}")
    private int numberOfClusters;

    @Autowired
    public ClusteringService(CoordinatesParser coordinatesParser) {
        this.coordinatesParser = coordinatesParser;
    }

    public List<List<Box>> solve() {
        List<Box> inputBoxes = coordinatesParser.parseJson(filePath);
        logger.info("solution={}, event=process_input, number_of_boxes={}, distanceThreshold={}", SOLUTION_NAME,
                inputBoxes.size(), distanceThreshold);

        getClusters(numberOfClusters);

        return List.of();
    }

    /**
     * Used Apache Spark to create clusters of boxes.
     * It takes @numberOfClusters random coordinates from a json file as cluster centers
     * then it forms clusters based on the closest coordinates to these center
     * then it calculates the mean distance and create a new center point aka the center of the cluster
     * then it repeats these steps until the deviation is small enough.
     */
    private void getClusters(int numberOfClusters) {
        SparkSession spark = SparkSession.builder()
                .appName("Box Clustering")
                .master("local[*]")
                .getOrCreate();

        // Read JSON file into a DataFrame
        Dataset<Row> boxData = spark.read()
                .format("json")
                .load("src/main/resources/static/input.json");

        // Extract upper-left and bottom-right coordinates
        Dataset<Row> boxCoordinates = boxData.selectExpr(
                "topLeft.x as tlx", "topLeft.y as tly",
                "bottomRight.x as brx", "bottomRight.y as bry"
        );

        // Calculate box width and height
        Dataset<Row> featureVectors = boxCoordinates.withColumn("width", expr("brx - tlx"))
                .withColumn("height", expr("bry - tly"))
                .select("width", "height");

        // Convert box dimensions to feature vectors
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(new String[]{"width", "height"})
                .setOutputCol("features");
        Dataset<Row> trainingData = assembler.transform(featureVectors);

        // Train K-means clustering model
        KMeans kmeans = new KMeans()
                .setK(numberOfClusters) // Number of clusters
                .setSeed(1234L);
        KMeansModel model = kmeans.fit(trainingData);

        // Make predictions
        Dataset<Row> predictions = model.transform(trainingData);

        // Output the cluster assignments
        predictions.select("width", "height", "prediction").show();

        // Stop the SparkSession
        spark.stop();
    }
}
