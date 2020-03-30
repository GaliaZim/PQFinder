package visualization.panels;

import structures.Node;
import visualization.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MappingLinesPanel extends JPanel {
    private HashMap<Integer, Node> map;
    private HashMap<Node, Point> leafsPositions;
    private Point[] stringCharPositions;
    private boolean set;

    public MappingLinesPanel(){
        set = false;
    }

    public void set(HashMap<Integer, Node> map, HashMap<Node, Point> leafsPositions, Point[] stringCharPositions) {
        this.map = map;
        this.leafsPositions = leafsPositions;
        this.stringCharPositions = stringCharPositions;
        set = true;
    }

    @Override
    public void paintComponent(Graphics g) {
        Point leafLocation, charLocation;
        ((Graphics2D)g).setStroke(new BasicStroke(Constants.MAPPING_LINE_WIDTH));
        int x1, y1, x2, y2;
        if(set) {
            for (Map.Entry<Integer, Node> entry : map.entrySet()) {
                leafLocation = leafsPositions.get(entry.getValue());
                x1 = leafLocation.x;
                y1 = leafLocation.y;
                charLocation = stringCharPositions[entry.getKey()];
                x2 = charLocation.x;
                y2 = charLocation.y;
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }
}
