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
    int stringStartIndex;
    int stringEndIndex;

    //TODO: Deal with dT limit > span of node and dS limit > substring length
    public InternalNodeMappingAlgorithm(String string, Node node, int treeDeletionLimit,
                                        int stringDeletionLimit,
                                        ArrayList<HashMap<Integer, List<Mapping>>> mappingsByChildren,
                                        int stringStartIndex, int stringEndIndex) {
        super(string, node, treeDeletionLimit, stringDeletionLimit);
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
        int noDeletionEndPoint = stringStartIndex - 1 + node.getSpan();
        IntStream.rangeClosed(Math.max(noDeletionEndPoint - treeDeletionLimit, 1),
                Math.min(noDeletionEndPoint + stringDeletionLimit, this.stringEndIndex))
                .forEach(endPoint -> resultMappingsByEndPoints.put(endPoint, new LinkedList<>()));
    }
}
