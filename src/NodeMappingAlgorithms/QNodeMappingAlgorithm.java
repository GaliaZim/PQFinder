package NodeMappingAlgorithms;

import helpers.IndexToChildNodeEncoder;
import structures.*;

import java.util.*;
import java.util.function.BiFunction;

public class QNodeMappingAlgorithm extends InternalNodeMappingAlgorithm{
    private Backtrack[][][] dPTableLTR;
    private Backtrack[][][] dPTableRTL;
    private int[] spansLTR;
    private int[] spansRTL;
    private IndexToChildNodeEncoder encoder;


    public QNodeMappingAlgorithm(String string, Node node, int treeDeletionLimit,
                                 int stringDeletionLimit,
                                 BiFunction<String, Character, Double> substitutionFunction) {
        super(string, node, treeDeletionLimit, stringDeletionLimit, substitutionFunction);
        spansLTR = new int[numberOfChildren+1];
        spansRTL = new int[numberOfChildren+1];
        encoder = new IndexToChildNodeEncoder(node.getChildren());
    }

    /**
     * Calculates the summation of spans of the first {@code i} children of
     * {@code node} from left to right and from right to left, for
     * {@code 1 <= i <= numberOfChildren}
     */
    @Override
    protected void calculateSpans() {
        calculateCumulativeSpans(spansLTR, node.getChildren());
        calculateCumulativeSpans(spansRTL, node.getChildrenReversed());
    }

    /**
     * @param dPTable The dpTable from which to backtrack (according to the direction of the mapping of
     *                the children)
     * @param spans the spans array to use according to the direction of the mapping of the children
     * @param stringStartIndex The first index of {@code string} from which mappings can begin
     * @param stringEndIndex The last index of {@code string} in which mappings can end
     * @param rTL {@code true}, if the direction of the mapping of the children is from right to left
     * Calculates the best mapping between {@code node} and a substring of {@code string} starting at
     * {@code strngStartIndex} for every deletion combination, where the direction of the mapping of
     *                        the children is either from left to right or from right to left according
     *                        to {@code rTL}.
     */
    private void oneDirectionMapping(Backtrack[][][] dPTable, int[] spans, int stringStartIndex,
                                     int stringEndIndex, boolean rTL) {
        int endPoint, length;
        Backtrack max;
        Node childNode;
        for (int childIndex = 1; childIndex <= numberOfChildren; childIndex++) {
            for (int kS = 0; kS <= stringDeletionLimit; kS++) {
                for (int kT = 0; kT <= treeDeletionLimit; kT++) {
                    length = getLength(spans[childIndex], kT, kS);
                    endPoint = getEndPoint(stringStartIndex, spans[childIndex], kT, kS);
                    //If there are too many deletions considering the substring's length, leave the entry null
                    if(endPoint <= stringEndIndex & length >= 0) {
                        max = new Backtrack(Double.NEGATIVE_INFINITY);
                        if(!(length == 0 & kS != 0)) { //if length==0 & ks!=0 --> illegal entry gets -infinity
                            childNode = encoder.indexToChildNode(childIndex, rTL);
                            //choose from child's mappings
                            if (length > 0) {
                                max = findMaxMappingByEndPoint(dPTable, childIndex, kT, kS,
                                        getChildMappingsAtEndPoint(childNode, endPoint));
                            }
                            Backtrack prev;
                            //deletion from the string
                            if (kS > 0) {
                                prev = dPTable[childIndex][kT][kS - 1];
                                if (prev.compareTo(max) > 0)
                                    max = new Backtrack(prev.getScore(), kT,
                                            kS - 1, childIndex);
                            }
                            //deletion from the tree
                            int childNodeSpan = childNode.getSpan();
                            if (kT >= childNodeSpan) {
                                prev = dPTable[childIndex - 1][kT - childNodeSpan][kS];
                                if (prev.compareTo(max) > 0)
                                    max = new Backtrack(prev.getScore(),
                                        kT - childNodeSpan, kS, childIndex - 1);
                            }
                        }
                        dPTable[childIndex][kT][kS] = max;
                    }
                }
            }
        }
    }

    /**
     * @param stringStartIndex An index of {@code string}
     * @param span The total span of a set of nodes
     * @param kT Number of deletions from the tree
     * @param kS Number of deletions from the string
     * @return The end index (inclusive) of a mapping starting at index {@code stringStartIndex}
     * of a set of nodes with a total span {@code span} with {@code kT} and
     * {@code kS} deletions.
     */
    private int getEndPoint(int stringStartIndex, int span, int kT, int kS) {
        return stringStartIndex - 1 + getLength(span, kT, kS);
    }

    /**
     * @param span The total span of a set of nodes
     * @param kT Number of deletions from the tree
     * @param kS Number of deletions from the string
     * @return The length of a mapping of a set of nodes with a total span {@code span}
     * with {@code kT} and {@code kS} deletions.
     */
    private int getLength(int span, int kT, int kS) {
        return span - kT + kS;
    }

    //For debugging
    private void printDPTable(Backtrack[][][] dPTable) {
        for (int childIndex = numberOfChildren; childIndex >= 0; childIndex--) {
            System.out.println(childIndex + ": ");
            for (int kT = 0; kT < treeDeletionLimit + 1; kT++) {
                for (int kS = 0; kS < stringDeletionLimit + 1; kS++) {
                    Backtrack backtrack = dPTable[childIndex][kT][kS];
                    if(!backtrack.getScore().equals(Double.NEGATIVE_INFINITY))
                        System.out.println(kT + "," + kS + ": " + backtrack);
                }
            }
            System.out.println();
        }
    }

    @Override
    protected void map(int stringStartIndex, int stringEndIndex, int minLength) {
        dPTableLTR = createEmptyDPTable();
        oneDirectionMapping(dPTableLTR, spansLTR, stringStartIndex, stringEndIndex, false);

        dPTableRTL = createEmptyDPTable();
        oneDirectionMapping(dPTableRTL, spansRTL, stringStartIndex, stringEndIndex, true);
//        printDPTable(dPTableRTL);

        buildResult(stringStartIndex, stringEndIndex, minLength);
    }

    /**
     * @return An initialized {@code dpTable} according to the Q-node Mapping Algorithm
     */
    private Backtrack[][][] createEmptyDPTable() {
        Backtrack[][][] dPTable = new Backtrack[numberOfChildren + 1][treeDeletionLimit + 1][stringDeletionLimit + 1];
        for (int kS = 0; kS <= stringDeletionLimit; kS++) {
            for (int kT = 1; kT <= treeDeletionLimit; kT++) {
                dPTable[0][kT][kS] = new Backtrack(Double.NEGATIVE_INFINITY);
            }
            dPTable[0][0][kS] = new Backtrack(0.0, 0, kS-1, 0);
        }
        dPTable[0][0][0] = new Backtrack(0.0);
        return dPTable;
    }

    /**
     * @param spans An array of size {@code children.size()} in which the result
     *              will be saved
     * @param children a list of nodes (children of {@code node})
     * puts in {@code spans[i]} the summation of spans of the first {@code i} children in
     *      {@code children}.
     *      The base case for zero children is a span zero ({@code spans[0]=0})
     */
    private void calculateCumulativeSpans(int[] spans, List<Node> children) {
        int i = 0;
        int span = 0;
        spans[i] = span;
        for(Node childNode : children) {
            i++;
            span += childNode.getSpan();
            spans[i] = span;
        }
    }

    /**
     * @param dPTable The dpTable for which the method finds the mapping
     * @param childIndex An index of a child of {@code node} for which the method finds
     *                   the mapping
     * @param kT The maximum number of deletions from the tree
     * @param kS The maximum number of deletions from the string
     * @param childMappingsAtEndPoint The mappings of the child from which to choose. All mappings end at
     *                                the same index
     * @return A {@code Backtrack} for the best mapping of the child among the optional mappings
     * according to the method's parameters.
     */
    private Backtrack findMaxMappingByEndPoint(Backtrack[][][] dPTable, int childIndex, int kT, int kS,
                                               List<Mapping> childMappingsAtEndPoint){
        Backtrack max = new Backtrack(Double.NEGATIVE_INFINITY);
        for(Mapping mapping : childMappingsAtEndPoint) {
            //if the number of deletions is lower than the limit, consider the mapping
            if(mapping.getStringDeletions() <= kS & mapping.getTreeDeletions() <= kT){
                //the score for choosing the mapping and adding it to the mappings of previous children
                Double score =
                        dPTable[childIndex - 1][kT - mapping.getTreeDeletions()]
                                [kS - mapping.getStringDeletions()].getScore()
                                + mapping.getScore();
                if(score > max.getScore()) {
                    max = new Backtrack(score, kT - mapping.getTreeDeletions(),
                            kS - mapping.getStringDeletions(), childIndex - 1);
                }
            }
        }
        return max;
    }

    /**
     * @param stringStartIndex An index of {@code string} from which the mapping started
     * @param stringEndIndex An index of {@code string} in which the mapping ended (as dictated
     *                       by the deletion limit)
     * @param minLength The minimal length of a substring mapped to {@code node} as dictated by
     *                  the deletion limit
     * Backtracks through the DP table {@code dpTable} and builds the best mapping between
     * {@code node} and every substring of {@code string} starting at {@code stringStartIndex} with
     *                  every deletion combination. Every mapping with a score higher than -infinity is
     *                  built with the mappings of the children of {@code node} that yielded it,
     *                  including the deleted children and string indices. Puts the result in
     *                  {@code mappingsStartingAtSameIndexByEndPoints}.
     */
    private void buildResult(int stringStartIndex, int stringEndIndex, int minLength) {
        Backtrack[][][] dPTable;
        int[] spans;
        Mapping nodeMapping;
        Backtrack backtrackLTR;
        Backtrack backtrackRTL;
        Backtrack backtrack;
        boolean isRTL;
        int endPoint, length;
        mappingsStartingAtSameIndexByEndPoints =
                createMappingsByEndPoints(stringStartIndex - 1 + minLength, stringEndIndex);
        //For every deletion combination
        for (int kT = 0; kT <= treeDeletionLimit; kT++) {
            for (int kS = 0; kS <= stringDeletionLimit; kS++) {
                endPoint = getEndPoint(stringStartIndex, spansLTR[numberOfChildren], kT, kS);
                length = getLength(spansLTR[numberOfChildren], kT, kS);
                if (length > 0 & endPoint <= stringEndIndex) {
                    backtrackLTR = dPTableLTR[numberOfChildren][kT][kS];
                    backtrackRTL = dPTableRTL[numberOfChildren][kT][kS];
                    //Choose the mapping with higher score: left to right or right to left
                    if (backtrackLTR.getScore().compareTo(backtrackRTL.getScore()) > 0) {
                        dPTable = dPTableLTR;
                        spans = spansLTR;
                        backtrack = backtrackLTR;
                        isRTL = false;
                    } else {
                        dPTable = dPTableRTL;
                        spans = spansRTL;
                        backtrack = backtrackRTL;
                        isRTL = true;
                    }
                    nodeMapping = new Mapping(node, stringStartIndex, kS, kT, backtrack.getScore());
                    //if the score is not -infinity, calculate the deletions and children mappings
                    //that yield the node's mapping
                    if (!nodeMapping.getScore().equals(Double.NEGATIVE_INFINITY))
                        addChildrenMappings(dPTable, nodeMapping, isRTL, spans);
                    mappingsStartingAtSameIndexByEndPoints.get(nodeMapping.getEndIndex()).add(nodeMapping);
                }
            }
        }
    }

    /**
     * @param dPTable The dpTable from which to backtrack (according to the direction of the mapping of
     *                the children)
     * @param nodeMapping A mapping of {@code node} built from
     *                    {@code dpTable[nodeMapping.getTreeDeletions()][nodeMapping.getStringDeletions()]
     *                    [allChildrenSetIndex]}
     * @param rTL {@code true} if the direction of the mapping of the children is from right to left
     * @param spans the spans array to use according to the direction of the mapping of the children
     * The method backtracks through {@code dpTable} and adds to {@code nodeMapping} the string indices
     *              that were deleted, the descendants leaves that were deleted and the node's
     *              children mappings that were chosen to construct {@code nodeMapping}
     */
    private void addChildrenMappings(Backtrack[][][] dPTable, Mapping nodeMapping, boolean rTL,
                                     int[] spans) {
        //TODO: test change
        Backtrack backtrack;
        int endPoint;
        int prevKT, prevKS, prevChildIndex;
        int kT = nodeMapping.getTreeDeletions();
        int kS = nodeMapping.getStringDeletions();
        int stringStartIndex = nodeMapping.getStartIndex();
        int childIndex = numberOfChildren;
        backtrack = dPTable[childIndex][kT][kS];
        do {
            endPoint = getEndPoint(stringStartIndex, spans[childIndex], kT, kS);
            prevKT = backtrack.getPreviousTreeDeletions();
            prevKS = backtrack.getPreviousStringDeletions();
            prevChildIndex = backtrack.getPreviousChildIndex();
            if(childIndex == prevChildIndex) {
                nodeMapping.addDeletedStringIndex(endPoint);
            } else {
                Node childNode = encoder.indexToChildNode(childIndex, rTL);
                int kTDiff = kT - prevKT;
                if((kTDiff == childNode.getSpan())
                        & (kS == prevKS)) {
                    nodeMapping.addDeletedChild(childNode);
                } else {
                    int kSDiff = kS - prevKS;
                    Mapping childMapping = getChildMappingsAtEndPoint(childNode, endPoint).stream().filter(m ->
                                    (m.getTreeDeletions() == kTDiff) & (m.getStringDeletions() == kSDiff))
                            .findFirst().orElse(null);
                    nodeMapping.addChildMapping(childMapping);
                }
            }
            childIndex = prevChildIndex;
            kT = prevKT;
            kS = prevKS;
            backtrack = dPTable[childIndex][kT][kS];
        } while(backtrack.isNotFirst());
    }

    /**
     * @param childNode A node which is a child of {@code node} and is a member of
     * {@code node.getChildren()}
     * @param endPoint An index of {@code string}
     * @return A list of mappings between {@code childNode} and substrings of {@code string} ending at
     * {@code endPoint}
     */
    private List<Mapping> getChildMappingsAtEndPoint(Node childNode, int endPoint) {
        return mappingsByChildren.get(encoder.childNodeToLTRIndex(childNode))
                .get(endPoint);
    }
}
