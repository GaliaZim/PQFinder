package NodeMappingAlgorithms;

import structures.Mapping;
import structures.Node;

import java.util.HashMap;
import java.util.List;

public abstract class NodeMappingAlgorithm {
    String string;
    Node node;
    int stringAbsoluteIndex;
    int treeDeletionLimit;
    int stringDeletionLimit;
    //endPoint --> list of mappings
    HashMap<Integer, List<Mapping>> resultMappingsByEndPoints;
    //TODO: score + backtracking


    public NodeMappingAlgorithm(String string, int stringAbsoluteIndex, Node node, int treeDeletionLimit, int stringDeletionLimit) {
        this.string = string;
        this.stringAbsoluteIndex = stringAbsoluteIndex;
        this.node = node;
        this.treeDeletionLimit = treeDeletionLimit;
        this.stringDeletionLimit = stringDeletionLimit;
        resultMappingsByEndPoints = new HashMap<>(); //TODO: capacity
    }

    public abstract void runAlgorithm();

    public HashMap<Integer, List<Mapping>> getResultMappingsByEndPoints() {
        return resultMappingsByEndPoints;
    }
}
