package ada.app.util;

import ada.app.model.Box;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Component
public class PlotCoordinates extends JFrame {

    public PlotCoordinates() throws HeadlessException {
        super("Data clustering");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 800);
    }

    /**
     * Plots the boxes using different colors for each logical group.
     *
     * @param input - a list of groups
     */
    public void getPlotCoordinates(List<List<Box>> input) throws HeadlessException {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int count = 0;
                for (List<Box> group : input) {
                    count++;
                    g2d.setColor(generateColor(count, input.size()));
                    for (Box box : group) {
                        double width = box.getBottomRight().getX() - box.getTopLeft().getX();
                        double height = box.getBottomRight().getY() - box.getTopLeft().getY();
                        g2d.fillRect((int) box.getTopLeft().getX(), (int) box.getTopLeft().getY(), (int) width, (int) height);
                    }
                }
            }
        };
        getContentPane().add(panel);
        setVisible(true);
    }

    /**
     * Generate a color with varying hue based on iteration, full saturation and brightness.
     */
    private Color generateColor(int index, int total) {
        float hue = (float) index / total;
        float saturation = 1.0f;
        float brightness = 1.0f;
        return Color.getHSBColor(hue, saturation, brightness);
    }
}
