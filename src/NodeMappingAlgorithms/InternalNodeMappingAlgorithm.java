package NodeMappingAlgorithms;

import org.junit.jupiter.api.Test;
import structures.Mapping;
import structures.Node;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public abstract class InternalNodeMappingAlgorithm extends NodeMappingAlgorithm{
    //childIndex -> endPoint -> list of mappings
    /**
     * {@code mappingsByChildren[childIndex]} -> a hash map where the keys are indices of {@code string}
     * and the values are lists of mappings between the {@code childIndex} child of the node
     * and substrings of {@code string} ending at the key index.
     * {@code mappingsByChildren[0] = null}
     * The mappings of the first child (from the left) are in {@code mappingsByChildren[1]}
     * The mappings of the last child (from the left) are in {@code mappingsByChildren[numberOfChildren]}
     * childIndex -> endpoint -> list of mappings
     */
    ArrayList<HashMap<Integer, List<Mapping>>> mappingsByChildren;
    /**
     * key: an index of {@code string}
     * value: a list of mappings between {@code node} and substrings of {@code string} ending at {@code key}.
     * All the mapped substrings start at the same index.
     * endPoint --> list of mappings
     */
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

    /**
     * @param smallestEndPoint the smallest key of the hash map
     * @param largestEndPoint the largest key of the hash map
     * @return A hash map where the keys are all the integer values between {@code smallestEndPoint} and
     * {@code largestEndPoint} (inclusive) and the values are empty lists of mappings.
     */
    HashMap<Integer, List<Mapping>> createMappingsByEndPoints(int smallestEndPoint, int largestEndPoint) {
        HashMap<Integer, List<Mapping>> mappingsByEndPoint =
                new HashMap<>();
        IntStream.rangeClosed(smallestEndPoint, largestEndPoint)
                .forEach(endPoint -> mappingsByEndPoint.put(endPoint, new ArrayList<>()));
        return mappingsByEndPoint;
    }

    /**
     * Recursively calls a mapping algorithm for the children of {@code node}, then calculates its
     * own mappings as described in the method of the abstract superclass
     */
    public void runAlgorithm() {
        callAndCollectChildrenMappings();
        calculateSpans();
        int maxLength = node.getSpan() + stringDeletionLimit;
        int minLength = Math.max(node.getSpan() - treeDeletionLimit, 1);
        int stringLastIndex = string.length();
        int smallestEndPoint = Math.min(Math.max(minLength, 1), stringLastIndex);
        resultMappingsByEndPoints = createMappingsByEndPoints(smallestEndPoint, stringLastIndex);
        int largestStartIndex = Math.max(stringLastIndex - minLength + 1, 1);
        for (int startIndex = 1; startIndex <= largestStartIndex; startIndex++) {
            filterChildrenMappings(startIndex);
            map(startIndex, Math.min(startIndex - 1 + maxLength, stringLastIndex), minLength);
            mergeMappings();
        }
    }

    /**
     * Adds all the mappings in {@code mappingsStartingAtSameIndexByEndPoints} to
     * {@code resultMappingsByEndPoints} according to their end points.
     */
    private void mergeMappings() {
        List<Mapping> mappingsToAddTo;
        for (Map.Entry<Integer, List<Mapping>> entry : mappingsStartingAtSameIndexByEndPoints.entrySet()) {
            mappingsToAddTo = resultMappingsByEndPoints.get(entry.getKey());
            if(mappingsToAddTo == null) //If this happens the algorithm is wrong
                throw new NoSuchElementException("Missing an end point in the result mappings of node: " + node);
            mappingsToAddTo.addAll(entry.getValue());
        }
    }

    /**
     * Recursively calls a mapping algorithm for every child of {@code node} and then adds the results in
     * {@code mappingsByChildren} according to the child's index
     */
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

    /**
     * @param fromIndex an index of {@code string}
     * Filters out of {@code mappingsByChildren} all the mappings to substrings that include indices
     *                  of {@code string } that are smaller than {@code fromIndex}
     */
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

    /**
     * Calculate the spans of the children of {@code node} in an accumulative way as dictated by the type of
     * {@code node} and its mapping algorithm
     */
    protected abstract void calculateSpans();

    /**
     * @param stringStartIndex An index of {@code string} from which to start the mapping
     * @param stringEndIndex An index of {@code string} in which the mapping should end (as dictated
     *                       by the deletion limit)
     * @param minLength The minimal length of a substring mapped to {@code node} as dictated by the
     *                  deletion limit
     * Calculates all the best mappings between {@code node} and every substring of {@code string} between
     *                  {@code stringStartIndex} and {@code stringEndIndex} according to the type of
     *                  {@code node}
     */
    protected abstract void map(int stringStartIndex, int stringEndIndex, int minLength);
}
