package visualization;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ConnectingLinesPanel extends JPanel {
    private HashMap<Integer,Integer> map;
    private Point[] leafsPositions;
    private Point[] stringCharPositions;
    private boolean set;

    ConnectingLinesPanel(){
        set = false;
    }

    void set(HashMap<Integer, Integer> map, Point[] leafsPositions, Point[] stringCharPositions) {
        this.map = map;
        this.leafsPositions = leafsPositions;
        this.stringCharPositions = stringCharPositions;
        set = true;
    }

    @Override
    public void paintComponent(Graphics g) {
        Point leafLocation, charLocation;
        int x1, y1, x2, y2;
        if(set) {
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                leafLocation = leafsPositions[entry.getKey() - 1];
                x1 = Math.toIntExact(Math.round(leafLocation.getX()));
                y1 = Math.toIntExact(Math.round(leafLocation.getY()));
                charLocation = stringCharPositions[entry.getValue() - 1];
                x2 = Math.toIntExact(Math.round(charLocation.getX()));
                y2 = Math.toIntExact(Math.round(charLocation.getY()));
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }
}
