package visualization;

import structures.GeneGroup;
import structures.Node;
import visualization.panels.DrawablePanel;
import visualization.panels.MappingLinesPanel;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class VisualizeMapping {
    public static Point[] getStringLabelsPositions(ArrayList<GeneGroup> string, JPanel stringPanel) {
        Point[] stringCharPositions = new Point[string.size()];
        Point stringPanelPosition = stringPanel.getLocation();
        for (int i = 0; i < string.size(); i++) {
            stringCharPositions[i] = calcLinePosition(stringPanelPosition,
                    stringPanel.getComponent(i).getBounds(), false);
        }
        return stringCharPositions;
    }

    public static JPanel createStringPanel(ArrayList<GeneGroup> string, Font font) {
        JLabel jLabel;
        JPanel stringPanel = new JPanel();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(Constants.NODE_HORIZONTAL_SPACE);
        stringPanel.setLayout(flowLayout);

        int charIndex = 0;
        for (GeneGroup geneGroup : string) {
            jLabel = new JLabel(geneGroup.toString());
            jLabel.setFont(font);
            stringPanel.add(jLabel,charIndex);
            charIndex++;
        }
        return stringPanel;
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

    public static void visualize(Node treeRoot, ArrayList<GeneGroup> string, HashMap<Integer, Node> mapping){
            //String and tree panel
            Font font = new Font("Courier New", Font.ITALIC, 24);
            GridLayout gridLayout = new GridLayout(2, 1);
            gridLayout.setVgap(50);
            JPanel stringAndTreePanel = new JPanel();
            stringAndTreePanel.setLayout(gridLayout);

            //Add tree to panel
            TreePanelCreator creator = new TreePanelCreator(treeRoot, font);
            DrawablePanel treeJPanel = creator.createTreeJPanel(0, 10);
            stringAndTreePanel.add(treeJPanel);

            //Add string to panel
            JPanel stringPanel = createStringPanel(string, font);
            stringAndTreePanel.add(stringPanel);

            // Panel for string, tree and mapping lines
            JPanel mappingPanel = new JPanel();
            mappingPanel.setLayout(new OverlayLayout(mappingPanel));
            mappingPanel.add(stringAndTreePanel);

            // Add mapping lines
            MappingLinesPanel mappingLines = new MappingLinesPanel();
            mappingPanel.add(mappingLines,0);

            //button to draw mapping lines
            JButton button = new JButton("Map");
            button.addActionListener(event->{
                Point[] stringCharPositions = getStringLabelsPositions(string, stringPanel);
                HashMap<Node, Point> leafLocations = treeJPanel.getLeafConnections();
                mappingLines.set(mapping, leafLocations, stringCharPositions);
                mappingPanel.repaint();
                button.setEnabled(false);
            });

            // Main JFrame
            JFrame jFrame = new JFrame();
            jFrame.setLayout(new BorderLayout());
            jFrame.setSize(1800, 1000);
            jFrame.setVisible(true);
            jFrame.setResizable(false);

            // Add all objects to main panel
            jFrame.add(mappingPanel, BorderLayout.CENTER);
            jFrame.add(button, BorderLayout.LINE_END);
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
