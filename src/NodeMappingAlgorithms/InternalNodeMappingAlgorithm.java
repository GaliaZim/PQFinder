package NodeMappingAlgorithms;

import structures.Mapping;
import structures.Node;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public abstract class InternalNodeMappingAlgorithm extends NodeMappingAlgorithm{
    //childIndex -> endPoint -> list of mappings
    ArrayList<HashMap<Integer, List<Mapping>>> mappingsByChildren;
    HashMap<Integer, List<Mapping>> mappingsStartingAtSameIndexByEndPoints;
    int numberOfChildren;

    //TODO: Deal with (dT limit > span of node) and (dS limit > substring length). should it be null or -infinity for next level
    public InternalNodeMappingAlgorithm(String string, Node node, int treeDeletionLimit,
                                        int stringDeletionLimit,
                                        BiFunction<String, Character, Double> substitutionFunction) {
        super(string, node, treeDeletionLimit, stringDeletionLimit, substitutionFunction);
        this.mappingsStartingAtSameIndexByEndPoints = new HashMap<>(); //TODO: capacity
        this.numberOfChildren = node.getNumberOfChildren();
        this.mappingsByChildren = new ArrayList<>(numberOfChildren);
    }

    HashMap<Integer, List<Mapping>> createMappingsByEndPoints(int stringStartIndex, int stringEndIndex) {
        HashMap<Integer, List<Mapping>> mappingsByEndPoint = new HashMap<>();
        int noDeletionEndPoint = stringStartIndex - 1 + node.getSpan();
        IntStream.rangeClosed(Math.max(noDeletionEndPoint - treeDeletionLimit, 1),
                Math.min(noDeletionEndPoint + stringDeletionLimit, stringEndIndex))
                .forEach(endPoint -> mappingsByEndPoint.put(endPoint, new LinkedList<>()));
        return mappingsByEndPoint;
    }

    public void runAlgorithm() {
        callAndCollectChildrenMappings();
        calculateSpans();
        int length = string.length();
        resultMappingsByEndPoints = createMappingsByEndPoints(1, length);
        for (int startIndex = 1; startIndex < length; startIndex++) {
            filterChildrenMappings(startIndex);
            map(startIndex, length); //TODO: use more accurate endIndex
            mergeMappings();
        }
    }

    private void mergeMappings() {
        //TODO: Think of reversing - outer loop on mappingsStartingAtSameIndexByEndPoints, might be smaller and no need for null check
        List<Mapping> mappingsToAdd;
        for (Map.Entry<Integer, List<Mapping>> entry : resultMappingsByEndPoints.entrySet()) {
            mappingsToAdd = mappingsStartingAtSameIndexByEndPoints.get(entry.getKey());
            if(mappingsToAdd != null)
                entry.getValue().addAll(mappingsToAdd);
        }
    }

    private void callAndCollectChildrenMappings() {
        NodeMappingAlgorithm childMappingAlgorithm;
        int childIndex = 0;
        mappingsByChildren.add(childIndex,null);
        for (Node child : node.getChildren()) {
            childIndex ++;
            childMappingAlgorithm = MappingAlgorithmBuilder.build(string, child, treeDeletionLimit,
                    stringDeletionLimit, substitutionFunction);
            childMappingAlgorithm.runAlgorithm();
            HashMap<Integer, List<Mapping>> childMappings =
                    childMappingAlgorithm.getResultMappingsByEndPoints();
            mappingsByChildren.add(childIndex, childMappings);
        }
    }

    private void filterChildrenMappings(int fromIndex) {
        //TODO: make sure this doesn't ruin the backtracking through the tree tiers
        //mappings: childIndex -> endpoint -> list of mappings
        for (HashMap<Integer,List<Mapping>> childMappingsByEndPoint : this.mappingsByChildren) {
            if(childMappingsByEndPoint != null) {
                childMappingsByEndPoint.keySet().removeIf(endPoint -> endPoint < fromIndex);
                for(Map.Entry<Integer, List<Mapping>> mappingsAtEndPoint : childMappingsByEndPoint.entrySet()) {
                    mappingsAtEndPoint.getValue().removeIf(mapping -> mapping.getStartIndex() < fromIndex);
                }
            }
        }
    }

    protected abstract void map(int stringStartIndex, int stringEndIndex);
    protected abstract void calculateSpans();
}
