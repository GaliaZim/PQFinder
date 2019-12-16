package NodeMappingAlgorithms;

import structures.*;

import java.util.*;

public class QNodeMappingAlgorithm extends InternalNodeMappingAlgorithm{
    private Backtrack[][][] dPTableLTR;
    private Backtrack[][][] dPTableRTL;
    private int[] spansLTR;
    private int[] spansRTL;
    private int numberOfChildren;


    public QNodeMappingAlgorithm(String string, Node node, int treeDeletionLimit,
                                 int stringDeletionLimit,
                                 ArrayList<HashMap<Integer, List<Mapping>>> mappingsByChildren,
                                 int stringStartIndex, int stringEndIndex) {
        super(string, node, treeDeletionLimit, stringDeletionLimit,
                mappingsByChildren, stringStartIndex, stringEndIndex);
        numberOfChildren = node.getNumberOfChildren();
        spansLTR = new int[numberOfChildren+1];
        spansRTL = new int[numberOfChildren+1];
        dPTableLTR = new Backtrack[numberOfChildren + 1][treeDeletionLimit + 1][stringDeletionLimit + 1];
        dPTableRTL = new Backtrack[numberOfChildren + 1][treeDeletionLimit + 1][stringDeletionLimit + 1];
    }

    private void oneDirectionMapping(Backtrack[][][] dPTable, int[] spans, List<Node> children) {
        int endPoint, length;
        Backtrack max;
        for (int childIndex = 1; childIndex <= numberOfChildren; childIndex++) {
            for (int kS = 0; kS <= stringDeletionLimit; kS++) {
                for (int kT = 0; kT <= treeDeletionLimit; kT++) {
                    length = getLength(spans[childIndex], kT, kS);
                    endPoint = getEndPoint(spans[childIndex], kT, kS);
                    //If there are too many deletions considering the substring's length, leave the entry null
                    if(endPoint <= this.stringEndIndex & endPoint >= 0) {
                        max = new Backtrack(Double.NEGATIVE_INFINITY);
                        if(!(length == 0 & kS != 0)) { //if length==0 & ks!=0 --> illegal entry gets -infinity
                            //choose from child's mappings
                            if (length > 0) {
                                max = findMaxMappingByEndPoint(dPTable, childIndex, kT, kS,
                                        getChildMappingsAtEndPoint(children.get(childIndex - 1), endPoint));
                            }
                            //deletion from the string
                            if (kS > 0 && dPTable[childIndex][kT][kS - 1].compareTo(max) > 0) {
                                max = new Backtrack(dPTable[childIndex][kT][kS - 1].getScore(),
                                        kT, kS - 1, childIndex);
                            }
                            //deletion from the tree
                            int childNodeSpan = spans[childIndex] - spans[childIndex - 1];
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

    private int getEndPoint(int span, int kT, int kS) {
        return this.stringStartIndex - 1 + getLength(span, kT, kS);
    }

    private int getLength(int span, int kT, int kS) {
        return span - kT + kS;
    }

    private List<Mapping> getChildMappingsAtEndPoint(Node child, int endPoint) {
        //TODO: do it better
        int realIndex = 1 + node.getChildren().indexOf(child);
        return mappingsByChildren.get(realIndex).get(endPoint);
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
    public void runAlgorithm() {
        init(dPTableLTR, spansLTR, node.getChildren());
        oneDirectionMapping(dPTableLTR,spansLTR, node.getChildren());

        List<Node> childrenReversed = node.getChildrenReversed();
        init(dPTableRTL, spansRTL, childrenReversed);
        oneDirectionMapping(dPTableRTL,spansRTL, childrenReversed);
//        printDPTable(dPTableRTL);

        buildResult(childrenReversed);
    }

    private void init(Backtrack[][][] dPTable, int[] spans, List<Node> children) {
        calculateCumulativeSpans(spans, children);
        //init DP Table
        for (int kS = 0; kS <= stringDeletionLimit; kS++) {
            for (int kT = 1; kT <= treeDeletionLimit; kT++) {
                dPTable[0][kT][kS] = new Backtrack(Double.NEGATIVE_INFINITY);
            }
            dPTable[0][0][kS] = new Backtrack(0.0, 0, kS-1, 0);
        }
        dPTable[0][0][0] = new Backtrack(0.0);
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

    private void buildResult(List<Node> reversedChildren) {
        Backtrack[][][] dPTable;
        List<Node> children;
        int[] spans;
        Mapping nodeMapping;
        Backtrack backtrackLTR;
        Backtrack backtrackRTL;
        Backtrack backtrack;
        int endPoint;
        initMappingsByEndPoints();
        for (int kT = 0; kT <= treeDeletionLimit; kT++) {
            for (int kS = 0; kS <= stringDeletionLimit; kS++) {
                endPoint = getEndPoint(spansLTR[numberOfChildren], kT, kS);
                if (endPoint > 0 & endPoint <= this.stringEndIndex) {
                    backtrackLTR = dPTableLTR[numberOfChildren][kT][kS];
                    backtrackRTL = dPTableRTL[numberOfChildren][kT][kS];
                    if (backtrackLTR.getScore().compareTo(backtrackRTL.getScore()) > 0) {
                        dPTable = dPTableLTR;
                        spans = spansLTR;
                        backtrack = backtrackLTR;
                        children = node.getChildren();
                    } else {
                        dPTable = dPTableRTL;
                        spans = spansRTL;
                        backtrack = backtrackRTL;
                        children = reversedChildren;
                    }
                    nodeMapping = new Mapping(node, stringStartIndex, kS, kT, backtrack.getScore());
                    if (!nodeMapping.getScore().equals(Double.NEGATIVE_INFINITY))
                        addChildrenMappings(dPTable, nodeMapping, children, kT, kS, spans);
                    resultMappingsByEndPoints.get(nodeMapping.getEndIndex()).add(nodeMapping);
                }
            }
        }
    }


    private void addChildrenMappings(Backtrack[][][] dPTable, Mapping nodeMapping, List<Node> children, int kT, int kS, int[] spans) {
        Backtrack backtrack;
        int endPoint;
        int prevKT, prevKS, prevChildIndex;
        int childIndex = numberOfChildren;
        backtrack = dPTable[childIndex][kT][kS];
        do {
            endPoint = getEndPoint(spans[childIndex], kT, kS);
            prevKT = backtrack.getPreviousTreeDeletions();
            prevKS = backtrack.getPreviousStringDeletions();
            prevChildIndex = backtrack.getPreviousChildIndex();
            if(childIndex == prevChildIndex) {
                nodeMapping.addDeletedStringIndex(endPoint);
            } else {
                Node childNode = children.get(childIndex - 1);
                int kTDiff = kT - prevKT;
                if((kTDiff == childNode.getSpan())
                        & (kS == prevKS)) {
                    nodeMapping.addDeletedChild(childNode);
                } else {
                    int kSDiff = kS - prevKS;
                    int realChildIndex = node.getChildren().indexOf(childNode) + 1; //TODO: do it better
                    Mapping childMapping = mappingsByChildren.get(realChildIndex).get(endPoint).stream()
                            .filter(m ->
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

    private int calcStartIndex(int endIndex, int kS, int kT, Node node) {
        return 1 + endIndex - (node.getSpan() + kS - kT);
    }

}
