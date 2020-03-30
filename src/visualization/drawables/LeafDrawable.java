package visualization.drawables;

import structures.GeneGroup;
import visualization.Constants;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class LeafDrawable implements ConnectedDrawable {
    private int x;
    private int y;
    private GeneGroup label;
    private int width;
    private int xDiff;

    public LeafDrawable(int x, int y, GeneGroup label) {
        this.x = x;
        this.y = y;
        this.label = label;
        width = label.getCog().length() * Constants.LETTER_WIDTH;
    }

    @Override
    public void draw(Graphics g) {
        g.drawString(label.getCog(), x + xDiff, y);
    }

    @Override
    public void setXDiff(int xDiff) {
        this.xDiff = xDiff;
    }

    @Override
    public Point getTopConnectionPoint() {
        int topPointX = x + width / 2;
        int topPointY = y - Constants.VERTICAL_MARGIN;
        return new Point(topPointX, topPointY);
    }

    @Override
    public List<Point> getBottomConnectionPoints(int size) {
        int bottomPointX = x + width / 3;
        int bottomPointY = y + Constants.LETTER_HEIGHT; // + Constants.VERTICAL_MARGIN;
        Point bottom = new Point(bottomPointX, bottomPointY);
        return Collections.nCopies(size, bottom);
    }

    @Override
    public Rectangle getBorders() {
        return new Rectangle(x, y, width, Constants.LETTER_HEIGHT);
    }
}
