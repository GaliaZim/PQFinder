package visualization.drawables;

import visualization.Constants;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class PNodeDrawable implements ConnectedDrawable {
    private int x;
    private int y;
    private int xDiff;

    public PNodeDrawable(int xMiddle, int y) {
        this.x = xMiddle - Constants.P_NODE_WIDTH / 2;
        this.y = y;
    }

    @Override
    public void draw(Graphics g) {
        System.out.println(g.getClipBounds().toString());
        g.drawOval(x + xDiff, y, Constants.P_NODE_WIDTH, Constants.P_NODE_HEIGHT);
    }

    @Override
    public Point getTopConnectionPoint() {
        int topPointX = x + Constants.P_NODE_WIDTH / 2;
        return new Point(topPointX, y);
    }

    @Override
    public List<Point> getBottomConnectionPoints(int size) {
        int bottomPointX = x + Constants.P_NODE_WIDTH / 2;
        int bottomPointY = y + Constants.P_NODE_HEIGHT;
        Point bottom = new Point(bottomPointX, bottomPointY);
        return Collections.nCopies(size, bottom);
    }

    @Override
    public Rectangle getBorders() {
        return new Rectangle(x, y, Constants.P_NODE_WIDTH, Constants.P_NODE_HEIGHT);
    }

    @Override
    public void setXDiff(int xDiff) {
        this.xDiff += xDiff;
    }
}
