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


    public static Node buildTree(String path) throws IOException, ParseException {
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
        geneSeqJson.forEach(geneJson -> string.add(new GeneGroup((String) geneJson)));
        return string;
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
                Double score = Double.valueOf(split[colCogIndex + 1]);
                if(score == 0.0)
                    score = Double.NEGATIVE_INFINITY;
                scoreMatrix.get(lineCogIndex).add(colCogIndex, score);
            }
            lineCogIndex++;
        }

        BiFunction<GeneGroup, GeneGroup, Double> substitutionFunc =
                (g1, g2) -> {
                    int i1 = cogToIndex.get(g1.getCog());
                    int i2 = cogToIndex.get(g2.getCog());
                    return scoreMatrix.get(i1).get(i2);
                };
        return substitutionFunc;
    }

    private static Map<String, Integer> getCogIndicesFromFirstLine(String line) {
        Map<String, Integer> map = new HashMap<>();
        String[] split = line.split("\t");
        for (int i = 0; i < split.length - 1; i++) {
            map.put(split[i + 1], i);
        }
        return map;
    }
}
