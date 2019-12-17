package NodeMappingAlgorithms;

import structures.Node;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiFunction;

class MappingAlgorithmBuilder {
    static NodeMappingAlgorithm build(String string, Node node, int treeDeletionLimit,
                                      int stringDeletionLimit,
                                      BiFunction<String, Character, Double>
                                              substitutionFunction) {
        NodeMappingAlgorithm algorithm;
        try {
            Constructor constructor;
            Class<?> mappingAlgorithmClass = node.getType().getMappingAlgorithm();
            constructor = mappingAlgorithmClass
                    .getConstructor(String.class, Node.class, int.class, int.class, BiFunction.class);
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

    private static NodeMappingAlgorithm getNodeMappingAlgorithm(String string, Node node, int treeDeletionLimit, int stringDeletionLimit, BiFunction<String, Character, Double> substitutionFunction) {
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
