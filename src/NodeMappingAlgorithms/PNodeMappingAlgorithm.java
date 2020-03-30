package NodeMappingAlgorithms;

import helpers.ChildrenSubsetEncoding;
import structures.Backtrack;
import structures.GeneGroup;
import structures.Mapping;
import structures.Node;

import java.util.*;
import java.util.function.BiFunction;

public class PNodeMappingAlgorithm extends InternalNodeMappingAlgorithm {
    private Backtrack[][][] dPTable;
    /**
     * The number of subsets of children of {@code node}
     */
    private int numberOfSubsets;
    /**
     * Holds the summation of spans of every subset of children of {@code node} by the set's index as given by
     * {@code ChildrenSubsetEncoding}
     */
    private int[] subsetSpans;

    public PNodeMappingAlgorithm(ArrayList<GeneGroup> string, Node node, int treeDeletionLimit,
                                 int stringDeletionLimit,
                                 BiFunction<GeneGroup, GeneGroup, Double> substitutionFunction) {
        super(string, node, treeDeletionLimit, stringDeletionLimit, substitutionFunction);
        this.numberOfSubsets = (int) Math.pow(2,numberOfChildren);
        this.subsetSpans = new int[numberOfSubsets];
    }

    @Override
    protected void map(int stringStartIndex, int stringEndIndex, int minLength) {
        initDPTable();
        Backtrack max;
        int endPoint;
        for (int setIndex = 0; setIndex < numberOfSubsets; setIndex++) {
            for (int kS = 0; kS <= stringDeletionLimit; kS++) {
                for (int kT = 0; kT <= treeDeletionLimit; kT++) {
                    endPoint = getEndPoint(stringStartIndex, kT, kS, setIndex);
                    if(endPoint <= stringEndIndex) {
                        max = this.dPTable[kT][kS][setIndex];
                        if(max == null)
                            //if this DP table entry was not set as part of the initialization
                            max = new Backtrack(Double.NEGATIVE_INFINITY);
                        if (setIndex > 0) //Not an empty set
                            //choose a child to mapping
                            max = findMaxMappingByEndPoint(setIndex, kT, kS, endPoint);
                        if (kS > 0 //it's an option to delete from the string
                                // and deleting from the string gives a higher score
                                && dPTable[kT][kS - 1][setIndex].compareTo(max) >= 0)
                            max = new Backtrack(dPTable[kT][kS - 1][setIndex].getScore(),
                                    kT, kS - 1, setIndex);
                        this.dPTable[kT][kS][setIndex] = max;
                    }
                }
            }
        }
        buildResult(stringStartIndex, stringEndIndex, minLength);
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
        Mapping nodeMapping;
        Backtrack backtrack;
        int endPoint, length;
        mappingsStartingAtSameIndexByEndPoints =
                createEmptyMappingListsByEndPoints(stringStartIndex - 1 + minLength, stringEndIndex);
        int allChildrenSetIndex = numberOfSubsets - 1;
        for (int kT = 0; kT <= treeDeletionLimit; kT++) {
            for (int kS = 0; kS <= stringDeletionLimit; kS++) {
                backtrack = dPTable[kT][kS][allChildrenSetIndex];
                endPoint = getEndPoint(stringStartIndex, kT, kS, allChildrenSetIndex);
                length = getLength(kT, kS, allChildrenSetIndex);
                if (length > 0 & endPoint <= stringEndIndex) {
                    nodeMapping = new Mapping(node, stringStartIndex, kS, kT, backtrack.getScore());
                    if (!nodeMapping.getScore().equals(Double.NEGATIVE_INFINITY))
                        addChildrenMappings(nodeMapping);
                    mappingsStartingAtSameIndexByEndPoints.get(nodeMapping.getEndIndex()).add(nodeMapping);
                }
            }
        }
    }

    /**
     * @param nodeMapping A mapping of {@code node} built from
     *                    {@code dpTable[nodeMapping.getTreeDeletions()][nodeMapping.getStringDeletions()]
     *                    [allChildrenSetIndex]}
     * The method backtracks through {@code dpTable} and adds to {@code nodeMapping} the string indices that were
     *                    deleted, the descendants leaves that were deleted and the node's children mappings that were
     *                    chosen to construct {@code nodeMapping}
     */
    private void addChildrenMappings(Mapping nodeMapping) {
        Backtrack backtrack;
        int endPoint;
        int prevKT, prevKS, prevSetIndex;
        int kT = nodeMapping.getTreeDeletions();
        int kS = nodeMapping.getStringDeletions();
        int stringStartIndex = nodeMapping.getStartIndex();
        int setIndex = numberOfSubsets - 1;
        backtrack = dPTable[kT][kS][setIndex];
        do {
            endPoint = getEndPoint(stringStartIndex, kT, kS, setIndex);
            prevKT = backtrack.getPreviousTreeDeletions();
            prevKS = backtrack.getPreviousStringDeletions();
            prevSetIndex = backtrack.getPreviousSubsetIndex();
            if(setIndex == prevSetIndex) {
                nodeMapping.addDeletedStringIndex(endPoint);
            } else {
                int childIndex = ChildrenSubsetEncoding.getMissingChild(setIndex, prevSetIndex);
                Node childNode = node.getChildren().get(childIndex - 1);
                int kTDiff = kT - prevKT;
                if((kTDiff == childNode.getSpan())
                        & (kS == prevKS)) {
                    nodeMapping.addDeletedChild(childNode);
                } else {
                    int kSDiff = kS - prevKS;
                    Mapping childMapping = mappingsByChildren.get(childIndex).get(endPoint).stream()
                            .filter(m ->
                                    (m.getTreeDeletions() == kTDiff) & (m.getStringDeletions() == kSDiff))
                            .findFirst().orElse(null);
                    nodeMapping.addChildMapping(childMapping);
                }
            }
            setIndex = prevSetIndex;
            kT = prevKT;
            kS = prevKS;
            backtrack = dPTable[kT][kS][setIndex];
        } while(backtrack.isNotFirst());
    }

    /**
     * @param setIndex The index of the subset of children from which to choose
     * @param kT The maximum number of deletions from the tree
     * @param kS The maximum number of deletions from the string
     * @param endPoint The end point of the mappings from which to choose
     * @return A {@code Backtrack} for the best child mapping (or deletion) to choose according to the
     * method's parameters.
     * Deletion is allowed only in the base case where the mapped substring's length is 0 and {@code kS=0}. In such
     * a case it does not matter which child is deleted first, and thus the leftmost child is chosen
     */
    private Backtrack findMaxMappingByEndPoint(int setIndex, int kT, int kS, int endPoint) {
        int length = getLength(kT, kS, setIndex);
        Set<Integer> childrenSet = ChildrenSubsetEncoding.indexToChildrenSet(setIndex);
        Backtrack max = new Backtrack(Double.NEGATIVE_INFINITY);
        int setWithoutChildIndex;
        if(length > 0) {
            //find a child mapping that gives the maximal score
            for (Integer childIndex : childrenSet) {
                setWithoutChildIndex = ChildrenSubsetEncoding.getSetIndexWithoutChild(setIndex, childIndex);
                max = Backtrack.max(max,
                        findChildMaxMappingByEndPoint(setWithoutChildIndex,
                                childIndex, kT, kS, endPoint));
            }
        } else if(length == 0 & kS == 0){
            if(!childrenSet.isEmpty()) {
                //delete the leftmost child if possible given kT.
                int childIndex = Collections.min(childrenSet);
                setWithoutChildIndex = ChildrenSubsetEncoding.getSetIndexWithoutChild(setIndex,childIndex);
                int spanDiff = subsetSpans[setIndex] - subsetSpans[setWithoutChildIndex];
                if(spanDiff <= kT)
                    max = Backtrack.max(max,
                            new Backtrack(dPTable[kT-spanDiff][kS][setWithoutChildIndex].getScore(),
                                    kT-spanDiff, kS, setWithoutChildIndex));
            }
        }
        return max;
    }

    /**
     * @param setWithoutChildIndex An index of a subset of children of {@code node} that does not contain
     *                             the {@code childIndex} child
     * @param childIndex An index of a child of {@code node}
     * @param kT The maximum number of deletions from the tree allowed for the chosen mapping
     * @param kS The maximum number of deletions from the string allowed for the chosen mapping
     * @param endPoint The end point of the mappings from which to choose of the chosen mapping
     * @return A backtrack of the chosen mapping of the {@code childIndex} child of {@code node} according to the
     * method's parameters. The chosen mapping yields the highest score. If there is no mapping that meets the
     * conditions laid by the parameters, an empty backtrack with score -infinity is returned.
     */
    private Backtrack findChildMaxMappingByEndPoint(int setWithoutChildIndex,
                                                    Integer childIndex, int kT, int kS,
                                                    int endPoint) {
        Backtrack max = new Backtrack(Double.NEGATIVE_INFINITY);
        List<Mapping> mappings = this.mappingsByChildren.get(childIndex).get(endPoint);
        for (Mapping mapping : mappings) {
            if(mapping.getStringDeletions() <= kS & mapping.getTreeDeletions() <= kT){
                Double score = this.dPTable[kT - mapping.getTreeDeletions()]
                        [kS - mapping.getStringDeletions()]
                        [setWithoutChildIndex].getScore()
                                + mapping.getScore();
                if(score > max.getScore()) {
                    max = new Backtrack(score, kT - mapping.getTreeDeletions(),
                            kS - mapping.getStringDeletions(), setWithoutChildIndex);
                }
            }
        }
        return max;
    }

    /**
     * Initialize {@code dpTable} according to the P-node Mapping Algorithm
     */
    private void initDPTable() {
        this.dPTable = new Backtrack[treeDeletionLimit+1][stringDeletionLimit+1][numberOfSubsets];
        //init DPTable entries of empty subset
        int emptySetIndex = ChildrenSubsetEncoding.childrenSubsetToIndex(Collections.emptySet());
        for (int kS = 0; kS <= stringDeletionLimit; kS++) {
            for (int kT = 1; kT <= treeDeletionLimit; kT++) {
                dPTable[kT][kS][emptySetIndex] = new Backtrack(Double.NEGATIVE_INFINITY);
            }
            dPTable[0][kS][emptySetIndex] = new Backtrack(0.0);
        }
        dPTable[0][0][emptySetIndex] = new Backtrack(0.0);
        //init DPTable entries for getEndPoint = 0
        for (int setIndex = 0; setIndex < numberOfSubsets; setIndex++) {
            for (int kS = 0; kS <= stringDeletionLimit; kS++) {
                for (int kT = 0; kT <= treeDeletionLimit; kT++) {
                    if(getLength(kT,kS,setIndex) == 0)
                        if(kS == 0)
                            dPTable[kT][kS][setIndex] = new Backtrack(0.0);
                        else
                            dPTable[kT][kS][setIndex] = new Backtrack(Double.NEGATIVE_INFINITY);
                }
            }
        }
    }

    /**
     * @param kT Number of deletions from the tree
     * @param kS Number of deletions from the string
     * @param setIndex Index of a subset of children of {@code node} as encoded by
     * {@code ChildrenSubsetEncoding}
     * @return The length of a mapping containing the nodes in the subset with index
     * {@code setIndex}, and {@code kT} and {@code kS} deletions.
     */
    private int getLength(int kT, int kS, int setIndex) {
        return this.subsetSpans[setIndex] + kS - kT;
    }

    /**
     * @param stringStartIndex An index of {@code string}
     * @param kT Number of deletions from the tree
     * @param kS Number of deletions from the string
     * @param setIndex Index of a subset of children of {@code node} as encoded by
     * {@code ChildrenSubsetEncoding}
     * @return The end index (inclusive) of a mapping containing the nodes in the subset with
     * index {@code setIndex}, and {@code kT} and {@code kS} deletions, that starts
     * at index {@code stringStartIndex}
     */
    private int getEndPoint(int stringStartIndex, int kT, int kS, int setIndex) {
        return stringStartIndex - 1 + getLength(kT, kS, setIndex);
    }

    /**
     * Calculates the span of every subset of children of {@code node}
     */
    protected void calculateSpans() {
        for (int subsetIndex = 0; subsetIndex < numberOfSubsets; subsetIndex++) {
            Set<Integer> childrenSet = ChildrenSubsetEncoding.indexToChildrenSet(subsetIndex);
            for (Integer childIndex : childrenSet)
                this.subsetSpans[subsetIndex] +=
                        node.getChildren().get(childIndex-1).getSpan();
        }
    }

    //For debugging
    private void printDPTable() {
        for (int setIndex = numberOfSubsets-1; setIndex >= 0; setIndex--) {
            System.out.println(setIndex + ": ");
            for (int kT = 0; kT < treeDeletionLimit + 1; kT++) {
                for (int kS = 0; kS < stringDeletionLimit + 1; kS++) {
                    Backtrack backtrack = dPTable[kT][kS][setIndex];
                    if(backtrack != null && !backtrack.getScore().equals(Double.NEGATIVE_INFINITY))
                        System.out.println(kT + "," + kS + ": " + backtrack);
                }
            }
            System.out.println();
        }
    }
}
