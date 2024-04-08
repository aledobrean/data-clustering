package ada.app;

import ada.app.model.Box;
import ada.app.util.PlotCoordinates;
import ada.app.util.SolutionManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

@SpringBootApplication
public class DataClusteringApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DataClusteringApplication.class, args);
        SolutionManager solutionManager = context.getBean(SolutionManager.class);
        PlotCoordinates plotCoordinates = context.getBean(PlotCoordinates.class);

        List<List<Box>> groups = solutionManager.solveProblem();

        groups.forEach(System.out::println);

        plotCoordinates.getPlotCoordinates(groups);
    }
}
