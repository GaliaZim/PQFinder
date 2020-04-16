package NodeMappingAlgorithms;

import structures.GeneGroup;
import structures.Mapping;
import structures.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class LeafMappingAlgorithm extends NodeMappingAlgorithm {

    public LeafMappingAlgorithm(ArrayList<GeneGroup> string, Node node, int treeDeletionLimit, int stringDeletionLimit,
                                BiFunction<GeneGroup,GeneGroup,Double> substitutionFunction) {
        super(string, node, treeDeletionLimit, stringDeletionLimit, substitutionFunction);
    }

    /**
     * Adds a mapping between every string index and the leaf node according to the substitution function.
     * The mapping with the dummy endPoint 0, is given a score of 0.
     */
    @Override
    public void runAlgorithm() {
        Double score;
        int endPoint = 0;
        addMappingByEndPoint(endPoint, 0.0);
        for(GeneGroup stringGeneGroup : string) {
            endPoint++;
            final GeneGroup nodeLabel = node.getLabel();
            score = substitutionFunction.apply(nodeLabel, stringGeneGroup);
                if(!score.equals(substitutionFunction.apply(stringGeneGroup, nodeLabel)))
                    throw new RuntimeException(String.format("Substitution matrix is not symmetric for %s and %s",
                            nodeLabel.getCog(), stringGeneGroup.getCog()));
            addMappingByEndPoint(endPoint, score);
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
