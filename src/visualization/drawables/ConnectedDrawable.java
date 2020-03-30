package visualization.drawables;

import java.awt.*;
import java.util.List;

public interface ConnectedDrawable extends Drawable {
    Point getTopConnectionPoint();
    List<Point> getBottomConnectionPoints(int size);
    Rectangle getBorders();
}
