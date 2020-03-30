package visualization.drawables;

import visualization.Constants;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QNodeDrawable implements ConnectedDrawable {
    private int x;
    private int y;
    private int width;
    private int xDiff;

    public QNodeDrawable(int xMiddle, int y, int numberOfChildren) {
        this.y = y;
        this.width = numberOfChildren * Constants.Q_NODE_WIDTH_PER_CHILD;
        this.x = xMiddle - width / 2;
    }

    @Override
    public void draw(Graphics g) {
        g.drawRect(x + xDiff, y, width, Constants.Q_NODE_HEIGHT);
    }

    @Override
    public Point getTopConnectionPoint() {
        int topPointX = x + Constants.P_NODE_WIDTH / 2;
        return new Point(topPointX, y);
    }

    @Override
    public List<Point> getBottomConnectionPoints(int size) {
        int space = width / (size - 1);
        int bottomPointY = y + Constants.Q_NODE_HEIGHT;
        int bottomPointX = x;
        List<Point> pointList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            pointList.add(new Point(bottomPointX, bottomPointY));
            bottomPointX += space;
        }
        return pointList;
    }

    @Override
    public Rectangle getBorders() {
        return new Rectangle(x, y, width, Constants.Q_NODE_HEIGHT);
    }

    @Override
    public void setXDiff(int xDiff) {
        this.xDiff += xDiff;
    }
}
