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

    NodeMappingAlgorithm(String string, Node node, int treeDeletionLimit, int stringDeletionLimit,
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

    //For debugging
    public void printResultMappings() {
        resultMappingsByEndPoints.forEach((endPoint, ls) -> {
            System.out.println(endPoint + ": ");
            ls.forEach(mapping -> {
                System.out.println(mapping);
                if (!mapping.getScore().equals(Double.NEGATIVE_INFINITY)) {
                    mapping.getChildrenMappings().forEach(System.out::println);
                    System.out.println("Deleted:");
                    System.out.println("Descendants:");
                    mapping.getDeletedDescendant().forEach(child ->
                            System.out.println(child.getCog()));
                    System.out.println("String Indices:");
                    mapping.getDeletedStringIndices().forEach(System.out::println);
                }
                System.out.println("----");
            });
            System.out.println("*********");
        });
    }
}
