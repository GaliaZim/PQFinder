package visualization;

import structures.Node;
import structures.NodeType;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TreePanel extends JPanel {
    private static final int RECT_PARAM = 20;
    private static final int LEAF_HEIGHT = 15;
    final static int SYMBOL_WIDTH = 40;
    final static int SYMBOL_HEIGHT = 40;
    private static final int HEIGHT_MULT = 4;
    private Node treeRoot;
    private HashMap<Node, Point> leafLocations;

    TreePanel(Node treeRoot) {
        this.treeRoot = treeRoot;
        this.leafLocations = new HashMap<>();
    }

    @Override
    public void paintComponent(Graphics g) {
//        int height = treeRoot.getHeight();
        paintNode(g, treeRoot, 2, 2);
    }

    int getExpectedHeight() {
        return treeRoot.getHeight() * SYMBOL_HEIGHT * HEIGHT_MULT;
    }

    private SubTreePaintBorders paintNode(Graphics g, Node node, int xPosition, int yPosition) {
        if (node.getType() == NodeType.LEAF) {
            g.drawString(node.getLabel(), xPosition, yPosition);
            leafLocations.put(node, new Point(xPosition, yPosition));
            return new SubTreePaintBorders(xPosition, yPosition, xPosition,
                    yPosition, xPosition + SYMBOL_WIDTH, yPosition);
        }
        SubTreePaintBorders childSubTreeBorders = null;
        int leafY;
        int leftLeafX = xPosition;
        int rightLeafX;
        int childrenYPosition = yPosition + SYMBOL_HEIGHT * HEIGHT_MULT;
        List<Point> childrenLocations = new ArrayList<>();
        for(Node child : node.getChildren()) {
            childSubTreeBorders = paintNode(g, child, xPosition, childrenYPosition);
            xPosition = childSubTreeBorders.getRightLeafX() + SYMBOL_WIDTH * 2;
            childrenLocations.add(childSubTreeBorders.getRootLocation());
        }
        leafY = childSubTreeBorders.getLeftLeafY();
        rightLeafX = childSubTreeBorders.getRightLeafX();
        int thisXPosition = (leftLeafX + rightLeafX) / 2;
        int thisWidth;
        if(node.getType() == NodeType.Q) {
            thisWidth = node.getNumberOfChildren() * RECT_PARAM;
            g.drawRect(thisXPosition - thisWidth / 2, yPosition, thisWidth, SYMBOL_HEIGHT);
        }
        else {
            thisWidth = SYMBOL_WIDTH;
            g.drawOval(thisXPosition - thisWidth / 2, yPosition, SYMBOL_WIDTH, SYMBOL_HEIGHT);
        }
        thisXPosition -= thisWidth / 2;
        int connectingLineX = thisXPosition + thisWidth / 2;
        int connectingLineY = yPosition + SYMBOL_HEIGHT;
        int height = 0;
        if(node.getChildren().get(0).getType() == NodeType.LEAF)
            height = LEAF_HEIGHT;
        for(Point childLocation : childrenLocations) {
            g.drawLine(connectingLineX, connectingLineY,
                    toInt(childLocation.getX()) + SYMBOL_WIDTH / 2,
                    toInt(childLocation.getY())-height);
        }
        return new SubTreePaintBorders(thisXPosition, yPosition, leftLeafX, leafY, rightLeafX, leafY);
    }

    private int toInt(double x) {
        return Math.toIntExact(Math.round(x));
    }

    HashMap<Node, Point> getLeafLocations() {
        return this.leafLocations;
    }
}
