package visualization;

import structures.Node;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class VisualizeMapping {

    public static void draw(Node treeRoot, String string, HashMap<Integer, Node> map) {
        //map: key - leaf, value - string index
        HashMap<Integer, Integer> connectionsMap = new HashMap<>();

        JFrame jFrame = new JFrame();
//        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel jLabel;
        Font font = new Font("Courier New", Font.ITALIC, 24);
        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 20, 2);

        jFrame.setLayout(new OverlayLayout(jFrame.getContentPane()));
        JPanel leafsAndStringPanel = new JPanel();
        leafsAndStringPanel.setLayout(new GridLayout(2,1));

        JPanel leafsPanel = new JPanel();
        List<Node> leafs = treeRoot.getLeafs();
        leafsPanel.setLayout(flowLayout);
        int leafIndex = 0;
        for (Node leaf : leafs) {
            jLabel = new JLabel(leaf.getLabel().toString());
            jLabel.setFont(font);
            leafsPanel.add(jLabel,leafIndex);
            leafIndex++;
            int stringIndex = find(leaf, map);
            if(stringIndex > -1)
                connectionsMap.put(leafIndex, stringIndex);
        }
//        leafsAndStringPanel.add(leafsPanel);
        TreePanel treePanel= new TreePanel(treeRoot);
        leafsAndStringPanel.add(treePanel);

        Point[] stringCharPositions = new Point[string.length()];
        JPanel stringPanel = new JPanel();
        stringPanel.setLayout(flowLayout);
        int charIndex = 0;
        for (char c : string.toCharArray()) {
            jLabel = new JLabel(new String(new char[]{c}));
            jLabel.setFont(font);
            stringPanel.add(jLabel,charIndex);
            charIndex++;
        }
        leafsAndStringPanel.add(stringPanel);

        int width = string.length() * 100;
        leafsAndStringPanel.setSize(width, 500);
        jFrame.add(leafsAndStringPanel);
        jFrame.setSize(width, 1000);
        jFrame.setVisible(true);

        Point[] leafsPositions = new Point[leafs.size()];
//        Point leafPanelPosition = leafsPanel.getLocation();
        Point stringPanelPosition = stringPanel.getLocation();
        for (int i = 0; i < string.length(); i++) {
            stringCharPositions[i] = calcLinePosition(stringPanelPosition, stringPanel.getComponent(i).getBounds(), false);
        }

        ConnectingLinesPanel connectingLinesPanel = new ConnectingLinesPanel();
        jFrame.add(connectingLinesPanel, 0);
        JButton button = new JButton("Map");
        button.addActionListener(event->{
            HashMap<Node, Point> leafLocations = treePanel.getLeafLocations();
            int index;
            for(Map.Entry<Node, Point> leafAndLocation : leafLocations.entrySet()) {
                index = leafs.indexOf(leafAndLocation.getKey());
                leafsPositions[index] = calcLinePosition(new Point(1,0), leafAndLocation.getValue(),
                        TreePanel.SYMBOL_WIDTH, TreePanel.SYMBOL_HEIGHT);
            }
            connectingLinesPanel.set(connectionsMap, leafsPositions, stringCharPositions);
            connectingLinesPanel.repaint();
        });
        stringPanel.add(button);
        jFrame.setResizable(false);
        while (jFrame.isActive()){}
    }

    private static int find(Node leaf, HashMap<Integer, Node> map) {
        int res = -1;
        for(Map.Entry<Integer, Node> integerNodeEntry: map.entrySet()) {
            if(integerNodeEntry.getValue().equals(leaf))
                return integerNodeEntry.getKey();
        }
        return res;
    }

    private static Point calcLinePosition(Point offset, Point location, int leafWidth, int leafHegiht) {
        int x = Math.toIntExact(Math.round(offset.getX() + location.getX() + (0.5 * leafWidth))) + 6;
        int y = Math.toIntExact(Math.round(offset.getY() + location.getY()))+2;
        return new Point(x, y);
    }
    private static Point calcLinePosition(Point offset, Rectangle rectangle, boolean bottom) {
        double addToY;
        if(bottom)
            addToY = rectangle.getHeight();
        else
            addToY = 0.0;
        int x = Math.toIntExact(Math.round(offset.getX() + rectangle.getX() + (0.5 * rectangle.getWidth()))) + 6;
        int y = Math.toIntExact(Math.round(offset.getY() + rectangle.getY()+ addToY));
        return new Point(x, y);
    }
}
