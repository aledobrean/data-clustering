package ada.app.service;

import ada.app.model.Box;
import ada.app.model.Point;
import ada.app.service.exception.BoxServiceException;
import ada.app.util.CoordinatesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BoxService {
    private static final String SOLUTION_NAME = "proximity_solution";
    private final Logger logger = LoggerFactory.getLogger(BoxService.class);
    private final CoordinatesParser coordinatesParser;
    @Value("${solution.file-path}")
    private String filePath;
    @Value("${solution.distance-threshold}")
    private double distanceThreshold;

    @Autowired
    public BoxService(CoordinatesParser coordinatesParser) {
        this.coordinatesParser = coordinatesParser;
    }

    public List<List<Box>> solve() {
        List<Box> inputBoxes = coordinatesParser.parseJson(filePath);
        logger.info("solution={}, event=process_input, number_of_boxes={}, distanceThreshold={}", SOLUTION_NAME, inputBoxes.size(),
                distanceThreshold);
        try {
            return groupBoxes(inputBoxes, distanceThreshold);
        } catch (BoxServiceException e) {
            logger.error("solution={}, event=process_input_failed, reason=invalid_distance_threshold, " + "distanceThreshold={} [{}]",
                    SOLUTION_NAME, distanceThreshold, e.getMessage());
        }
        return List.of();
    }

    /**
     * Iterates over the input boxes, splits into groups in relation to a specified distance.
     *
     * @param inputBoxes        - boxes from input
     * @param distanceThreshold - distance allowed to form a group
     * @return a list of groups
     */
    public List<List<Box>> groupBoxes(List<Box> inputBoxes, double distanceThreshold) throws BoxServiceException {
        List<List<Box>> groups = new ArrayList<>();
        boolean[] visited = new boolean[inputBoxes.size()];
        if (distanceThreshold < 0) {
            logger.info("solution={}, event=process_input_failed, reason=invalid_distance_threshold", SOLUTION_NAME);
            throw new BoxServiceException("Distance threshold negative!");
        }

        for (int i = 0; i < inputBoxes.size(); i++) {
            if (Boolean.FALSE.equals(visited[i])) {
                List<Box> group = new ArrayList<>();
                group.add(inputBoxes.get(i));
                visited[i] = true;

                findClosestBoxes(inputBoxes, i, visited, group, distanceThreshold);

                groups.add(group);
            }
        }
        logger.info("solution={}, event=process_input, number_of_groups={}", SOLUTION_NAME, groups.size());
        return groups;
    }

    /**
     * Recursively finds the closest box, constraint by a distance threshold.
     *
     * @param boxes             - boxes from input
     * @param index             - current box index
     * @param visited           - array of visited boxes
     * @param group             - logical group
     * @param distanceThreshold - distance allowed to form a group
     */
    private void findClosestBoxes(List<Box> boxes, int index, boolean[] visited, List<Box> group, double distanceThreshold) {
        Box currentBox = boxes.get(index);
        Point boxCenter = currentBox.getCenter();

        for (int i = 0; i < boxes.size(); i++) {
            if (Boolean.FALSE.equals(visited[i])) {
                Box newBox = boxes.get(i);
                Point newBoxCenter = newBox.getCenter();
                double distanceBetweenBoxes = boxCenter.distance(newBoxCenter);

                if (distanceBetweenBoxes <= distanceThreshold) {
                    visited[i] = true;
                    group.add(newBox);
                    findClosestBoxes(boxes, i, visited, group, distanceThreshold);
                }
            }
        }
    }
}
