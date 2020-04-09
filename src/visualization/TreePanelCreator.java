package visualization;

import structures.Node;
import structures.NodeType;
import visualization.drawables.*;
import visualization.panels.DrawablePanel;

import java.awt.*;
import java.util.*;
import java.util.List;

public class TreePanelCreator {

    private Node treeRoot;
    private DrawablePanel dPanel;
    private int lowestLeafLocation;

    TreePanelCreator(Node treeRoot, Font font) {
        this.treeRoot = treeRoot;
        this.dPanel = new DrawablePanel(font);
        this.lowestLeafLocation = 0;
    }

    public int getLeafTierWidth() {
        return treeRoot.getSpan() *
                (Constants.LETTER_WIDTH * Constants.LETTERS_IN_LABEL + Constants.NODE_HORIZONTAL_SPACE)
                - Constants.NODE_HORIZONTAL_SPACE;
    }

    public int getHeight() {
        System.out.print(lowestLeafLocation);
        return  lowestLeafLocation + 2;
    }

    public DrawablePanel createTreeJPanel(int x, int y) {
        HashMap<Node, Point> leafConnections = new HashMap<>();
        SubTreeBoundaries subTreeBoundaries = paintNode(treeRoot, x, y, leafConnections);
        dPanel.addDrawable(subTreeBoundaries.getRoot());
        dPanel.setLeafConnections(leafConnections);
        dPanel.setLeafWidth(getLeafTierWidth());
        dPanel.setTreeHeight(getHeight());
        return dPanel;
    }

    private SubTreeBoundaries paintNode(Node node, int xPosition, int yPosition, HashMap<Node, Point> leafConnections) {
        if (node.getType() == NodeType.LEAF) {
            return createLeafDrawable(node, xPosition, yPosition, leafConnections);
        }

        int childrenYPosition = yPosition + Constants.LAYER_SPACE;
        List<SubTreeBoundaries> childPaintResults = new ArrayList<>();
        int childXPosition = xPosition;
        int subTreeWidth = -Constants.NODE_HORIZONTAL_SPACE;
        int subTreeHeight = 0;
        for(Node child : node.getChildren()) {
            SubTreeBoundaries childPaintResult = paintNode(child, childXPosition, childrenYPosition, leafConnections);
            childPaintResults.add(childPaintResult);
            childXPosition = childPaintResult.getEndLeafX() + Constants.NODE_HORIZONTAL_SPACE;
            subTreeWidth += childPaintResult.getBorder().width + Constants.NODE_HORIZONTAL_SPACE;
            subTreeHeight = Math.max(subTreeHeight, childPaintResult.getBorder().height);
        }

        ConnectedDrawable thisNodeDrawable
                = createInternalNodeDrawable(node, xPosition, yPosition, subTreeWidth);

        addChildrenAndConnectingLinesToPanel(node, childPaintResults, thisNodeDrawable);

        return new SubTreeBoundaries(thisNodeDrawable, xPosition, yPosition,
                subTreeWidth, subTreeHeight + Constants.LAYER_SPACE + thisNodeDrawable.getBorders().height);
    }

    private ConnectedDrawable createInternalNodeDrawable(Node node, int xPosition, int yPosition, int subTreeWidth) {
        ConnectedDrawable nodeDrawable;
        int thisXMiddle = xPosition + subTreeWidth / 2;
        if(node.getType() == NodeType.QNode) {
            nodeDrawable = new QNodeDrawable(thisXMiddle, yPosition, node.getNumberOfChildren());
        } else {
            nodeDrawable = new PNodeDrawable(thisXMiddle, yPosition);
        }
        return nodeDrawable;
    }

    private void addChildrenAndConnectingLinesToPanel(Node node, List<SubTreeBoundaries> childPaintResults,
                                                      ConnectedDrawable nodeDrawable) {
        List<Point> bottomConnectionPoints = nodeDrawable.getBottomConnectionPoints(node.getNumberOfChildren());
        for (int i = 0; i < node.getNumberOfChildren(); i++) {
            ConnectedDrawable childDrawable = childPaintResults.get(i).getRoot();
            dPanel.addDrawable(childDrawable);
            Point parentBottomPoint = bottomConnectionPoints.get(i);
            Point childConnectionPoint = childDrawable.getTopConnectionPoint();
            dPanel.addDrawable(new Line(parentBottomPoint.x, parentBottomPoint.y,
                    childConnectionPoint.x, childConnectionPoint.y));
        }
    }

    private SubTreeBoundaries createLeafDrawable(Node node, int xPosition, int yPosition, HashMap<Node, Point> leafConnections) {
        yPosition += Constants.LEAF_Y_ADDITION;
        this.lowestLeafLocation = Math.max(this.lowestLeafLocation,
                yPosition - Constants.LEAF_Y_ADDITION + Constants.LETTER_HEIGHT);
        LeafDrawable drawable = new LeafDrawable(xPosition, yPosition, node.getLabel());
        leafConnections.put(node, drawable.getBottomConnectionPoints(1).get(0));
        return new SubTreeBoundaries(drawable);
    }
}
