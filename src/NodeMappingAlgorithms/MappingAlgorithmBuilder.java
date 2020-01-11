package NodeMappingAlgorithms;

import structures.GeneGroup;
import structures.Node;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.function.BiFunction;

public class MappingAlgorithmBuilder {
    /**
     * @param string A string to map
     * @param node A node to map
     * @param treeDeletionLimit The maximum number of deletions from the tree
     * @param stringDeletionLimit The maximum number of deletions from the string
     * @param substitutionFunction A substitution function between the string characters and the labels
     *                             of the leafs of the tree rooted in {@code node}
     * @return A Node mapping algorithm according to the type of {@code node}
     */
    public static NodeMappingAlgorithm build(ArrayList<GeneGroup> string, Node node, int treeDeletionLimit,
                                             int stringDeletionLimit,
                                             BiFunction<GeneGroup, GeneGroup, Double>
                                              substitutionFunction) {
        NodeMappingAlgorithm algorithm;
        try {
            Constructor constructor;
            Class<?> mappingAlgorithmClass = node.getType().getMappingAlgorithm();
            constructor = mappingAlgorithmClass
                    .getConstructor(ArrayList.class, Node.class, int.class, int.class, BiFunction.class);
            Object newInstance = constructor.newInstance(string, node, treeDeletionLimit,
                    stringDeletionLimit, substitutionFunction);
            if(mappingAlgorithmClass.isInstance(newInstance))
                algorithm = ((NodeMappingAlgorithm) newInstance);
            else
                algorithm = getNodeMappingAlgorithm(string, node, treeDeletionLimit,
                        stringDeletionLimit, substitutionFunction);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException e) {
            e.printStackTrace();
            algorithm = getNodeMappingAlgorithm(string, node, treeDeletionLimit,
                    stringDeletionLimit, substitutionFunction);
        }
        return algorithm;
    }

    /**
     * returns the same as {@code build}, but constructor methods calls are hard coded
     */
    private static NodeMappingAlgorithm getNodeMappingAlgorithm(ArrayList<GeneGroup> string, Node node,
                                                                int treeDeletionLimit, int stringDeletionLimit,
                                                                BiFunction<GeneGroup, GeneGroup, Double>
                                                                        substitutionFunction) {
        NodeMappingAlgorithm algorithm = null;
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
