package NodeMappingAlgorithms;

import helpers.ChildrenSubsetEncoding;
import structures.Backtrack;
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

    public PNodeMappingAlgorithm(String string, Node node, int treeDeletionLimit,
                                 int stringDeletionLimit,
                                 BiFunction<String, Character, Double> substitutionFunction) {
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
                            max = new Backtrack(Double.NEGATIVE_INFINITY);
                        if (setIndex > 0) //Not an empty set
                            max = findMaxMappingByEndPoint(setIndex, kT, kS, endPoint);
                        if (kS > 0 && dPTable[kT][kS - 1][setIndex].compareTo(max) >= 0)
                            max = new Backtrack(dPTable[kT][kS - 1][setIndex].getScore(),
                                    kT, kS - 1, setIndex);
                        this.dPTable[kT][kS][setIndex] = max;
                    }
                }
            }
        }
        buildResult(stringStartIndex, stringEndIndex, minLength);
    }

    private void buildResult(int stringStartIndex, int stringEndIndex, int minLength) {
        Mapping nodeMapping;
        Backtrack backtrack;
        int endPoint, length;
        mappingsStartingAtSameIndexByEndPoints =
                createMappingsByEndPoints(stringStartIndex - 1 + minLength, stringEndIndex);
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

    private void addChildrenMappings(Mapping nodeMapping, int kT, int kS, int stringStartIndex) {
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
            prevSetIndex = backtrack.getPreviousChildIndex();
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
        } while(!backtrack.isFirst());
    }

    private Backtrack findMaxMappingByEndPoint(int setIndex, int kT, int kS, int endPoint) {
        int length = getLength(kT, kS, setIndex);
        Set<Integer> childrenSet = ChildrenSubsetEncoding.indexToChildrenSet(setIndex);
        Backtrack max = new Backtrack(Double.NEGATIVE_INFINITY);
        int setWithoutChildIndex;
        if(length > 0) {
            for (Integer childIndex : childrenSet) {
                setWithoutChildIndex = ChildrenSubsetEncoding.getSetIndexWithoutChild(setIndex, childIndex);
                max = Backtrack.max(max,
                        findChildMaxMappingByEndPoint(setWithoutChildIndex,
                                childIndex, kT, kS, endPoint));
            }
        } else if(length == 0 & kS == 0){
            if(!childrenSet.isEmpty()) {
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

    private int getLength(int kT, int kS, int setIndex) {
        return this.subsetSpans[setIndex] + kS - kT;
    }

    private int getEndPoint(int stringStartIndex, int kT, int kS, int setIndex) {
        return stringStartIndex - 1 + getLength(kT, kS, setIndex);
    }

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
