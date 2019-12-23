package helpers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import structures.Node;
import structures.NodeType;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PrepareInput {
    private final static int SMALLEST_NODE_INDEX = 1;
    private final static String ROOT_JSON_KEY = "root";
    private final static String NODE_TYPE_JSON_KEY = "type";
    private final static String COG_JSON_KEY = "cog";
    private final static String CHILDREN_JSON_KEY = "children";


    public static Node buildTree(String path) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(new FileReader(path));
        JSONObject root = (JSONObject) obj.get(ROOT_JSON_KEY);
        return getNode(root, SMALLEST_NODE_INDEX);
    }

    private static Node getNode(JSONObject nodeObject, int smallestIndexInNodeSubtree) {
        String type = (String)nodeObject.get(NODE_TYPE_JSON_KEY);
        NodeType nodeType = NodeType.valueOf(type);
        String cog = null;
        List<Node> children = new ArrayList<>();
        int index;
        if(nodeType == NodeType.LEAF) {
            cog = (String)nodeObject.get(COG_JSON_KEY);
            index = smallestIndexInNodeSubtree;
        } else {
            JSONArray childrenArray = (JSONArray) nodeObject.get(CHILDREN_JSON_KEY);
            Node childNode;
            int smallestIndexInChildSubtree = smallestIndexInNodeSubtree;
            for (Object childObject : childrenArray) {
                //recursively get children nodes from JSON
                childNode = getNode((JSONObject)childObject, smallestIndexInChildSubtree);
                children.add(childNode);
                smallestIndexInChildSubtree = childNode.getIndex() + 1;
            }
            index = smallestIndexInChildSubtree;
        }
        Node node = new Node(index, nodeType, cog, children);
        node.setUndefinedFields();
        return node;
    }
}
