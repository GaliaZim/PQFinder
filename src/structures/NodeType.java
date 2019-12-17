package structures;

import NodeMappingAlgorithms.LeafMappingAlgorithm;
import NodeMappingAlgorithms.PNodeMappingAlgorithm;
import NodeMappingAlgorithms.QNodeMappingAlgorithm;

public enum NodeType {
    LEAF (LeafMappingAlgorithm.class),
    P (PNodeMappingAlgorithm.class),
    Q (QNodeMappingAlgorithm.class);

    private final Class mappingAlgorithm;

    NodeType(Class mappingAlgorithm) {
        this.mappingAlgorithm = mappingAlgorithm;
    }

    public Class<?> getMappingAlgorithm() {
        return mappingAlgorithm;
    }
}
