package NodeMappingAlgorithms;

import structures.Mapping;
import structures.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

public class LeafMappingAlgorithm extends NodeMappingAlgorithm {
    private BiFunction<String,Character,Double> substitutionFunction;

    public LeafMappingAlgorithm(String string, Node node, int treeDeletionLimit, int stringDeletionLimit, BiFunction<String,Character,Double> substitutionFunction) {
        super(string, node, treeDeletionLimit, stringDeletionLimit);
        this.substitutionFunction = substitutionFunction;
    }

    @Override
    public void runAlgorithm() {
        List<Mapping> mappings;
        Mapping mapping;
        Double score;
        for (int i = 0; i <= string.length(); i++) {
            mappings = new LinkedList<>();
            for (int dT = 0; dT <= treeDeletionLimit; dT++) {
                for (int dS = 0; dS <= stringDeletionLimit; dS++) {
                    score = Double.NEGATIVE_INFINITY;
                    if (i == 0 & dS == 0) {
                        mapping = new Mapping(node, i, dS, dT, score);
                        mappings.add(mapping);
                    }
                }
            }
            if (i == 0)
                score = 0.0;
            else
                score = substitutionFunction.apply(node.getCog(),string.charAt(i-1));
            mapping = new Mapping(node, i, i, 0, 0, score);
            mappings.add(mapping);
            resultMappingsByEndPoints.put(i, mappings);
        }
    }
}
