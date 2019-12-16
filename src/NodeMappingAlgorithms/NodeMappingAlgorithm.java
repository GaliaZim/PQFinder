package NodeMappingAlgorithms;

import structures.Mapping;
import structures.Node;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

public abstract class NodeMappingAlgorithm {
    String string;
    Node node;
    int treeDeletionLimit;
    int stringDeletionLimit;
    BiFunction<String,Character,Double> substitutionFunction;
    //endPoint --> list of mappings
    HashMap<Integer, List<Mapping>> resultMappingsByEndPoints;
    //TODO: score + backtracking


    public NodeMappingAlgorithm(String string, Node node, int treeDeletionLimit, int stringDeletionLimit,
                                BiFunction<String, Character, Double> substitutionFunction) {
        this.string = string;
        this.node = node;
        this.treeDeletionLimit = treeDeletionLimit;
        this.stringDeletionLimit = stringDeletionLimit;
        this.substitutionFunction = substitutionFunction;
        resultMappingsByEndPoints = new HashMap<>(); //TODO: capacity
    }

    public abstract void runAlgorithm();

    public HashMap<Integer, List<Mapping>> getResultMappingsByEndPoints() {
        return resultMappingsByEndPoints;
    }
}
