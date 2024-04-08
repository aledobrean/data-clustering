package ada.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Concept: these coordinates are like matrix coordinates.
 * Are relative to the upper left corner (0,0).
 */
@Component
public class Box {

    @JsonProperty("topLeft")
    private Point topLeft;
    @JsonProperty("bottomRight")
    private Point bottomRight;

    @Autowired
    public Box(Point topLeft, Point bottomRight) {
        this.topLeft = topLeft;
        this.bottomRight = bottomRight;
    }

    public Box() {
        // required for deserialization
    }

    public Point getTopLeft() {
        return topLeft;
    }

    public Point getBottomRight() {
        return bottomRight;
    }

    /**
     * Center of the box used to calculate proximity.
     */
    public Point getCenter() {
        return new Point((topLeft.getX() + bottomRight.getX()) / 2, (topLeft.getY()) + bottomRight.getY() / 2);
    }

    @Override
    public String toString() {
        return "Box{" + "topLeft=" + topLeft + ", bottomRight=" + bottomRight + ", center=" + getCenter() + '}';
    }
}
