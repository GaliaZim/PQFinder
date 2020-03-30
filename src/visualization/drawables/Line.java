package visualization.drawables;

import java.awt.*;

public class Line implements Drawable{
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    private int xDiff;

    public Line(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void draw(Graphics g) {
        g.drawLine(x1 + xDiff, y1, x2 + xDiff, y2);
    }

    @Override
    public void setXDiff(int xDiff) {
        this.xDiff = xDiff;
    }
}
