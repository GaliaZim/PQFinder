package NodeMappingAlgorithms;

import structures.Mapping;
import structures.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public abstract class InternalNodeMappingAlgorithm extends NodeMappingAlgorithm{
    //childIndex -> endPoint -> list of mappings
    ArrayList<HashMap<Integer, List<Mapping>>> mappingsByChildren;
    private int stringStartIndex;
    private int stringEndIndex;

    public InternalNodeMappingAlgorithm(String string, int stringAbsoluteIndex, Node node, int treeDeletionLimit,
                                        int stringDeletionLimit,
                                        ArrayList<HashMap<Integer, List<Mapping>>> mappingsByChildren,
                                        int stringStartIndex, int stringEndIndex) {
        super(string, stringAbsoluteIndex, node, treeDeletionLimit, stringDeletionLimit);
        this.mappingsByChildren = mappingsByChildren;
        this.stringStartIndex = stringStartIndex;
        this.stringEndIndex = stringEndIndex;
    }

    public ArrayList<HashMap<Integer, List<Mapping>>> getMappingsByChildren() {
        return mappingsByChildren;
    }

    public int getStringStartIndex() {
        return stringStartIndex;
    }

    public int getStringEndIndex() {
        return stringEndIndex;
    }

    void initMappingsByEndPoints() {
        int noDeletionEndPoint = stringAbsoluteIndex - 1 + node.getSpan();
        IntStream.rangeClosed(noDeletionEndPoint - treeDeletionLimit,
                noDeletionEndPoint + stringDeletionLimit)
                .forEach(endPoint -> resultMappingsByEndPoints.put(endPoint, new LinkedList<>()));
    }
}
