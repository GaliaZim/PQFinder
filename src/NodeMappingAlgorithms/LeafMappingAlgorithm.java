package NodeMappingAlgorithms;

import structures.Mapping;
import structures.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class LeafMappingAlgorithm extends NodeMappingAlgorithm {

    public LeafMappingAlgorithm(String string, Node node, int treeDeletionLimit, int stringDeletionLimit, BiFunction<String,Character,Double> substitutionFunction) {
        super(string, node, treeDeletionLimit, stringDeletionLimit, substitutionFunction);
    }

    /**
     * Adds a mapping between every string index and the leaf node according to the substitution function.
     * The mapping with the dummy endPoint 0, is given a score of 0.
     */
    @Override
    public void runAlgorithm() {
        Double score;
        addMappingByEndPoint(0, 0.0);
        for (int i = 1; i <= string.length(); i++) {
            score = substitutionFunction.apply(node.getCog(),string.charAt(i-1));
            addMappingByEndPoint(i, score);
        }
    }

    /**
     * @param endPoint an index of the string
     * @param score the score of the mapping
     * The method adds a mapping between string[{@code endPoint}] and {@code node} with no deletions
     *              to {@code resultMappingsByEndPoints}
     */
    private void addMappingByEndPoint(int endPoint, Double score) {
        Mapping mapping = new Mapping(node, endPoint, endPoint, 0, 0, score);
        List<Mapping> mappingList = new ArrayList<>();
        mappingList.add(mapping);
        resultMappingsByEndPoints.put(endPoint, mappingList);
    }
}
