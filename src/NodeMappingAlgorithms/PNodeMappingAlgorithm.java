package NodeMappingAlgorithms;

import structures.Mapping;
import structures.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PNodeMappingAlgorithm extends InternalNodeMappingAlgorithm {
    //DP Table

    public PNodeMappingAlgorithm(String string, int stringAbsoluteIndex, Node node, int treeDeletionLimit,
                                 int stringDeletionLimit,
                                 ArrayList<HashMap<Integer, List<Mapping>>> mappingsByChildren,
                                 int stringStartIndex, int stringEndIndex) {
        super(string, stringAbsoluteIndex, node, treeDeletionLimit, stringDeletionLimit,
                mappingsByChildren, stringStartIndex, stringEndIndex);
    }

    @Override
    public void runAlgorithm() {

    }
}
