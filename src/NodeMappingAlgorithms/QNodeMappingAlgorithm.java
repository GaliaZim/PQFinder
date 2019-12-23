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

    @Override
    protected void calculateSpans() {
        calculateCumulativeSpans(spansLTR, node.getChildren());
        calculateCumulativeSpans(spansRTL, node.getChildrenReversed());
    }

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
                            //deletion from the string
                            if (kS > 0) {
                                if (dPTable[childIndex][kT][kS - 1] == null)
                                    System.out.println("f");
                                if (dPTable[childIndex][kT][kS - 1].compareTo(max) > 0) {
                                    max = new Backtrack(dPTable[childIndex][kT][kS - 1].getScore(),
                                            kT, kS - 1, childIndex);
                                }
                            }
                            //deletion from the tree
                            int childNodeSpan = childNode.getSpan();
                            if (kT >= childNodeSpan
                                    && dPTable[childIndex - 1][kT - childNodeSpan][kS].compareTo(max) > 0) {
                                max = new Backtrack(dPTable[childIndex - 1][kT - childNodeSpan][kS].getScore(),
                                        kT - childNodeSpan, kS, childIndex - 1);
                            }
                        }
                        dPTable[childIndex][kT][kS] = max;
                    }
                }
            }
        }
    }

    private int getEndPoint(int stringStartIndex, int span, int kT, int kS) {
        return stringStartIndex - 1 + getLength(span, kT, kS);
    }

    private int getLength(int span, int kT, int kS) {
        return span - kT + kS;
    }

    private void printDPTable(Backtrack[][][] dPTable) {
        //For debugging
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

    private Backtrack findMaxMappingByEndPoint(Backtrack[][][] dPTable, int childIndex, int kT, int kS,
                                               List<Mapping> childMappingsAtEndPoint){
        Backtrack max = new Backtrack(Double.NEGATIVE_INFINITY);
        for(Mapping mapping : childMappingsAtEndPoint) {
            if(mapping.getStringDeletions() <= kS & mapping.getTreeDeletions() <= kT){
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
        for (int kT = 0; kT <= treeDeletionLimit; kT++) {
            for (int kS = 0; kS <= stringDeletionLimit; kS++) {
                endPoint = getEndPoint(stringStartIndex, spansLTR[numberOfChildren], kT, kS);
                length = getLength(spansLTR[numberOfChildren], kT, kS);
                if (length > 0 & endPoint <= stringEndIndex) {
                    backtrackLTR = dPTableLTR[numberOfChildren][kT][kS];
                    backtrackRTL = dPTableRTL[numberOfChildren][kT][kS];
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
                    if (!nodeMapping.getScore().equals(Double.NEGATIVE_INFINITY))
                        addChildrenMappings(dPTable, nodeMapping, isRTL, kT, kS, spans, stringStartIndex);
                    mappingsStartingAtSameIndexByEndPoints.get(nodeMapping.getEndIndex()).add(nodeMapping);
                }
            }
        }
    }

    private void addChildrenMappings(Backtrack[][][] dPTable, Mapping nodeMapping, boolean rTL,
                                     int kT, int kS, int[] spans, int stringStartIndex) {
        Backtrack backtrack;
        int endPoint;
        int prevKT, prevKS, prevChildIndex;
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
        } while(!backtrack.isFirst());
    }

    private List<Mapping> getChildMappingsAtEndPoint(Node childNode, int endPoint) {
        return mappingsByChildren.get(encoder.childNodeToLTRIndex(childNode))
                .get(endPoint);
    }
}
