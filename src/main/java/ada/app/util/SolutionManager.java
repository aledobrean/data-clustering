package ada.app.util;

import ada.app.model.Box;
import ada.app.service.BoxService;
import ada.app.service.ClusteringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SolutionManager {

    @Autowired
    private BoxService boxServiceSolution;

    @Autowired
    private ClusteringService clusteringServiceSolution;

    @Value("${app.use-proximity-solution}")
    private boolean useBoxServiceSolution;

    public List<List<Box>> solveProblem() {
        if (useBoxServiceSolution) {
            return boxServiceSolution.solve();
        } else {
            return clusteringServiceSolution.solve();
        }
    }
}