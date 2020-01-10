package NodeMappingAlgorithms;

import structures.Mapping;
import structures.Node;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public abstract class NodeMappingAlgorithm {
    /**
     * The string to map
     */
    String string;
    /**
     * The node to map
     */
    Node node;
    /**
     * The maximum number of deletions from the tree
     */
    int treeDeletionLimit;
    /**
     * The maximum number of deletions from the string
     */
    int stringDeletionLimit;
    /**
     * {@code substitutionFunction(A,B)} returns the score for substituting the leaf symbol A with
     * the character B of {@code string}.
     */
    BiFunction<String,Character,Double> substitutionFunction;
    /**
     * key: an index of {@code string}
     * value: a list of mappings between {@code node} and substrings of {@code string} ending at {@code key}.
     * endPoint --> list of mappings
     */
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

    /**
     * Puts all the mappings between {@code node} and every possible (according to {@code treeDeletionLimit}
     * and {@code stringDeletionLimit}) substring of {@code string} in {@code resultMappingsByEndPoints}
     * by the end point of the substring.
     */
    public abstract void runAlgorithm();

    /**
     * @return All the mappings as calculated by {@code runAlgorithm} hashed by their end points.
     */
    public HashMap<Integer, List<Mapping>> getResultMappingsByEndPoints() {
        return resultMappingsByEndPoints;
    }

    /**
     * @return The string to be mapped
     */
    public String getString() {
        return string;
    }

    /**
     * @return The node to be mapped
     */
    public Node getNode() {
        return node;
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
                            System.out.println(child.getLabel()));
                    System.out.println("String Indices:");
                    mapping.getDeletedStringIndices().forEach(System.out::println);
                }
                System.out.println("----");
            });
            System.out.println("*********");
        });
    }

    /**
     * @return the one-to-one mapping between the leaves descendants of {@code node} and
     * {@code string} according to the best mapping of the tree rooted in {@code node}
     * Best mapping according to the {@code Mapping::compareTo} method.
     */
    public HashMap<Integer,Node> getBestStringIndexToLeafMapping() {
        HashMap<Integer, Node> mappingToReturn = null;
        Optional<Mapping> optionalMaxMapping = resultMappingsByEndPoints.values().stream()
                .flatMap(Collection::stream).max(Mapping::compareTo);
        if(optionalMaxMapping.isPresent()) {
            Mapping maxMapping = optionalMaxMapping.get();
            System.out.println(maxMapping);
            mappingToReturn = maxMapping.getLeafMappings();
        }
        return mappingToReturn;
    }
}
