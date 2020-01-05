package NodeMappingAlgorithms;

import org.junit.jupiter.api.Test;
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
    InternalNodeMappingAlgorithm(String string, Node node, int treeDeletionLimit,
                                 int stringDeletionLimit,
                                 BiFunction<String, Character, Double> substitutionFunction) {
        super(string, node, treeDeletionLimit, stringDeletionLimit, substitutionFunction);
        this.mappingsStartingAtSameIndexByEndPoints = new HashMap<>(); //TODO: capacity
        this.numberOfChildren = node.getNumberOfChildren();
        this.mappingsByChildren = new ArrayList<>(numberOfChildren + 1);
    }

    HashMap<Integer, List<Mapping>> createMappingsByEndPoints(int smallestEndPoint, int largestEndPoint) {
        HashMap<Integer, List<Mapping>> mappingsByEndPoint =
                new HashMap<>();
        IntStream.rangeClosed(smallestEndPoint, largestEndPoint)
                .forEach(endPoint -> mappingsByEndPoint.put(endPoint, new ArrayList<>()));
        return mappingsByEndPoint;
    }

    public void runAlgorithm() {
        callAndCollectChildrenMappings();
        calculateSpans();
        int maxLength = node.getSpan() + stringDeletionLimit;
        int minLength = Math.max(node.getSpan() - treeDeletionLimit, 1);
        int stringLastIndex = string.length();
        int smallestEndPoint = Math.max(minLength, 1);
        //TODO: make sure the restriction doesn't damage the next tiers in the tree
        resultMappingsByEndPoints = createMappingsByEndPoints(smallestEndPoint, stringLastIndex);
        int largestStartIndex = Math.max(stringLastIndex - minLength + 1, 1);
        for (int startIndex = 1; startIndex <= largestStartIndex; startIndex++) {
            filterChildrenMappings(startIndex);
            map(startIndex, Math.min(startIndex - 1 + maxLength, stringLastIndex), minLength);
            mergeMappings();
        }
    }

    private void mergeMappings() {
        List<Mapping> mappingsToAddTo;
        for (Map.Entry<Integer, List<Mapping>> entry : mappingsStartingAtSameIndexByEndPoints.entrySet()) {
            mappingsToAddTo = resultMappingsByEndPoints.get(entry.getKey());
            if(mappingsToAddTo == null) //If this happens the algorithm is wrong
                throw new NoSuchElementException("Missing an end point in the result mappings of node: " + node);
            mappingsToAddTo.addAll(entry.getValue());
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
        for (HashMap<Integer,List<Mapping>> childMappingsByEndPoint : this.mappingsByChildren) {
            if(childMappingsByEndPoint != null) {
                childMappingsByEndPoint.keySet().removeIf(endPoint -> endPoint < fromIndex);
                for(Map.Entry<Integer, List<Mapping>> mappingsAtEndPoint : childMappingsByEndPoint.entrySet()) {
                    mappingsAtEndPoint.getValue().removeIf(mapping -> mapping.getStartIndex() < fromIndex);
                }
            }
        }
    }

    /** returns the one-to-one mapping between the leaves and string according to the best
     * mapping of the tree rooted in this.node */
    public HashMap<Integer,Node> getBestStringIndexToLeafMapping() {
        HashMap<Integer, Node> mappingToReturn = null;
        Optional<Mapping> optionalMaxMapping = resultMappingsByEndPoints.values().stream()
                .flatMap(Collection::stream).max(Mapping::compareTo);
        if(optionalMaxMapping.isPresent()) {
            Mapping maxMapping = optionalMaxMapping.get();
            System.out.println(maxMapping);
            mappingToReturn = maxMapping.getLeafMappings();
        }
        return mappingToReturn;
    }

    protected abstract void calculateSpans();
    protected abstract void map(int stringStartIndex, int stringEndIndex, int minLength);
}
