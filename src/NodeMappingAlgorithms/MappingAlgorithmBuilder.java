package NodeMappingAlgorithms;

import structures.Node;

import java.util.function.BiFunction;

public class MappingAlgorithmBuilder {
    public static NodeMappingAlgorithm build(String string, Node node, int treeDeletionLimit,
                                                      int stringDeletionLimit,
                                                      BiFunction<String,Character,Double>
                                                     substitutionFunction) {
        NodeMappingAlgorithm algorithm = null;
        //TODO: loose the switch
        switch (node.getType()) {
            case LEAF:
                algorithm = new LeafMappingAlgorithm(string, node, treeDeletionLimit,
                        stringDeletionLimit, substitutionFunction);
                break;
            case Q:
                algorithm = new QNodeMappingAlgorithm(string, node, treeDeletionLimit,
                        stringDeletionLimit, substitutionFunction);
                break;
            case P:
                algorithm = new PNodeMappingAlgorithm(string, node, treeDeletionLimit,
                        stringDeletionLimit, substitutionFunction);

        }
        return algorithm;
    }
}
