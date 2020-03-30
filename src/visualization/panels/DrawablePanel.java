package visualization.panels;

import structures.Node;
import visualization.drawables.Drawable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DrawablePanel extends JPanel {
    private List<Drawable> drawableList;
    private Font font;
    private int leafWidth;
    private HashMap<Node, Point> leafConnections;
    private int treeHeight;

    public DrawablePanel(Font font) {
        super(new FlowLayout());
        this.drawableList = new ArrayList<>();
        this.font = font;
        this.leafWidth = 0;
        this.treeHeight = 0;
    }

    public void addDrawable(Drawable drawable) {
        this.drawableList.add(drawable);
    }

    public void setLeafWidth(int leafWidth) {
        this.leafWidth = leafWidth;
    }

    public void setTreeHeight(int treeHeight) {
        this.treeHeight = treeHeight;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(font);
        int diff = (getWidth() - leafWidth)/2;
        for(Drawable drawable : drawableList) {
            drawable.setXDiff(diff);
            drawable.draw(g);
            drawable.setXDiff(-diff);
        }
    }

    public void setLeafConnections(HashMap<Node, Point> leafConnections) {
        this.leafConnections = leafConnections;
    }

    public HashMap<Node, Point> getLeafConnections() {
        return setLeafConnections((getWidth() - leafWidth)/2, getHeight() - treeHeight);
    }

    public HashMap<Node, Point> setLeafConnections(int xDiff, int yDiff) {
        HashMap<Node, Point> res = new HashMap<>(leafConnections.size());
        leafConnections.forEach((node, point) -> res.put(node, new Point(point.x + xDiff, point.y))); // - yDiff)));
        leafConnections = res;
        return res;
    }
}
