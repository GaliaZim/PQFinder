package helpers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import structures.GeneGroup;
import structures.Node;
import structures.NodeType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;

public class PrepareInput {
    private final static int SMALLEST_NODE_INDEX = 1;
    private final static String ROOT_JSON_KEY = "root";
    private final static String NODE_TYPE_JSON_KEY = "type";
    private final static String COG_JSON_KEY = "cog";
    private final static String CHILDREN_JSON_KEY = "children";
    private final static String INCORRECT_PAREN_FORMAT_MSG = "PQ-tree parenthesis format incorrect. ";


    public static Node buildTreeFromJSON(String path) throws IOException, ParseException {
        JSONObject obj = getJsonObjectFromFile(path);
        JSONObject root = (JSONObject) obj.get(ROOT_JSON_KEY);
        return decodeNodeFromJsonObject(root, SMALLEST_NODE_INDEX);
    }

    public static JSONObject getJsonObjectFromFile(String pathToJsonFile) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(new FileReader(pathToJsonFile));
    }

    public static Node decodeNodeFromJsonObject(JSONObject nodeObject, int smallestIndexInNodeSubtree) {
        String type = (String)nodeObject.get(NODE_TYPE_JSON_KEY);
        NodeType nodeType = NodeType.valueOf(type);
        GeneGroup label = null;
        List<Node> children = new ArrayList<>();
        int index;
        if(nodeType == NodeType.LEAF) {
            String labelString = (String)nodeObject.get(COG_JSON_KEY);
            label = new GeneGroup(labelString);
            index = smallestIndexInNodeSubtree;
        } else {
            JSONArray childrenArray = (JSONArray) nodeObject.get(CHILDREN_JSON_KEY);
            Node childNode;
            int smallestIndexInChildSubtree = smallestIndexInNodeSubtree;
            for (Object childObject : childrenArray) {
                //recursively get children nodes from JSON
                childNode = decodeNodeFromJsonObject((JSONObject)childObject, smallestIndexInChildSubtree);
                children.add(childNode);
                smallestIndexInChildSubtree = childNode.getIndex() + 1;
            }
            index = smallestIndexInChildSubtree;
        }
        return new Node(index, nodeType, label, children, true);
    }

    public static ArrayList<GeneGroup> convertCogJSONArrayToInputString(JSONArray geneSeqJson) {
        ArrayList<GeneGroup> string = new ArrayList<>();
        for(Object geneJson: geneSeqJson)
            string.add(new GeneGroup((String) geneJson));
        return string;
    }

    public static ArrayList<GeneGroup> retrieveInputStringFromFile(String pathToStringJSONFile)
            throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object jsonArray = parser.parse(new FileReader(pathToStringJSONFile));
        if(jsonArray instanceof JSONArray) {
            return convertCogJSONArrayToInputString((JSONArray) jsonArray);
        } else {
            throw new IllegalArgumentException("Gene sequence JSON file does not contain an array");
        }
    }

    public static BiFunction<GeneGroup, GeneGroup, Double> extractSubstitutionFunctionFromFile(String path)
            throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine();
        Map<String,Integer> cogToIndex = getCogIndicesFromFirstLine(line);

        int lineCogIndex = 0;
        String[] split;
        ArrayList<ArrayList<Double>> scoreMatrix = new ArrayList<>();
        while((line = br.readLine()) != null) {
            split = line.split("\t");
            scoreMatrix.add(lineCogIndex, new ArrayList<>());
            for(int colCogIndex = 0; colCogIndex < split.length - 1; colCogIndex++) {
                final String value = split[colCogIndex + 1];
                Double score;
                if(value.equals("."))
                    score = Double.NEGATIVE_INFINITY;
                else
                    score = Double.valueOf(value);
                scoreMatrix.get(lineCogIndex).add(colCogIndex, score);
            }
            lineCogIndex++;
        }

        return (g1, g2) -> {
            int i1 = cogToIndex.get(g1.getCog());
            int i2 = cogToIndex.get(g2.getCog());
            return scoreMatrix.get(i1).get(i2);
        };
    }

    private static Map<String, Integer> getCogIndicesFromFirstLine(String line) {
        Map<String, Integer> map = new HashMap<>();
        String[] split = line.split("\t");
        for (int i = 0; i < split.length - 1; i++) {
            map.put(split[i + 1], i);
        }
        return map;
    }

    private static int index;
    public static Node buildTreeFromParenRepresentation(String paren) throws IllegalArgumentException{
        index = 0;
        if(!paren.startsWith("[") & !paren.startsWith("("))
            throw new IllegalArgumentException(INCORRECT_PAREN_FORMAT_MSG + "Should begin with '[' or '('.");

        return buildTreeFromParenRepresentation(paren, SMALLEST_NODE_INDEX);
    }

    private static Node buildTreeFromParenRepresentation(String paren, int nodeIndex) {
        Node node = null;
        StringBuilder leafLabel = new StringBuilder();
        for (; index < paren.length(); index++) {
            char ch = paren.charAt(index);
            switch (ch) {
                case '[':
                case '(':
                    if (node == null)
                        node = newNodeFromParenChar(ch);
                    else {
                        if (leafLabel.length() > 0)
                            throw new IllegalArgumentException(INCORRECT_PAREN_FORMAT_MSG +
                                    "Missing a space character after gene at index " + index);
                        node.addChild(buildTreeFromParenRepresentation(paren, nodeIndex));
                        nodeIndex = node.getIndex();
                    }
                    break;
                case ']':
                case ')':
                    if(!closeParenMatchNodeType(node.getType(), ch))
                        throw new IllegalArgumentException(INCORRECT_PAREN_FORMAT_MSG + "Right bracket at index " +
                                index + "does not match left bracket");
                    if (leafLabel.length() > 0)
                        addLeafChild(nodeIndex, node, leafLabel);
                    return node;
                case ' ':
                    if (leafLabel.length() > 0) {
                        addLeafChild(nodeIndex, node, leafLabel);
                        leafLabel = new StringBuilder();
                        nodeIndex++;
                    } else {
                        final char prevChar = paren.charAt(index - 1);
                        if(prevChar != ']' & prevChar != ')')
                            throw new IllegalArgumentException(INCORRECT_PAREN_FORMAT_MSG +
                                "2 consecutive space characters at index " + index);
                    }
                    break;
                default:
                    leafLabel.append(ch);
            }
        }
        throw new IllegalArgumentException(INCORRECT_PAREN_FORMAT_MSG + "Not enough closing brackets.");
    }

    private static boolean closeParenMatchNodeType(NodeType nodeType, char closeParen) {
        return (nodeType.equals(NodeType.PNode) & closeParen == ')') |
                (nodeType.equals(NodeType.QNode) & closeParen == ']');
    }

    private static Node newNodeFromParenChar(char ch) {
        if(ch == '[')
            return new Node(NodeType.QNode);
        else
            return new Node(NodeType.PNode);
    }

    private static void addLeafChild(int nodeIndex, Node node, StringBuilder leafLabel) {
        Node leaf = new Node(nodeIndex, NodeType.LEAF, new GeneGroup(leafLabel.toString()),
                Collections.emptyList(), true);
        node.addChild(leaf);
    }
}
