package ada.app;

import ada.app.model.Box;
import ada.app.service.BoxService;
import ada.app.util.PlotCoordinates;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

@SpringBootApplication
public class DataClusteringApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DataClusteringApplication.class, args);
        BoxService boxService = context.getBean(BoxService.class);
        PlotCoordinates plotCoordinates = context.getBean(PlotCoordinates.class);

        List<List<Box>> groups = boxService.processInput("src/main/resources/static/input.json", 65);

        groups.forEach(System.out::println);

        plotCoordinates.getPlotCoordinates(groups);
    }
}
