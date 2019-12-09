package NodeMappingAlgorithms;

import structures.*;

import java.util.*;
import java.util.stream.IntStream;

public class QNodeMappingAlgorithm extends InternalNodeMappingAlgorithm{
    private Backtrack[][][] dPTable;
    private int[] spans;
    private int numberOfChildren;

    public QNodeMappingAlgorithm(String string, int stringAbsoluteIndex, Node node, int treeDeletionLimit,
                                 int stringDeletionLimit,
                                 ArrayList<HashMap<Integer, List<Mapping>>> mappingsByChildren,
                                 int stringStartIndex, int stringEndIndex) {
        super(string, stringAbsoluteIndex, node, treeDeletionLimit, stringDeletionLimit,
                mappingsByChildren, stringStartIndex, stringEndIndex);
        numberOfChildren = node.getNumberOfChildren();
        spans = new int[numberOfChildren+1];
        dPTable = new Backtrack[numberOfChildren + 1][treeDeletionLimit + 1][stringDeletionLimit + 1];
    }

    @Override
    public void runAlgorithm() {
        init();
        int endPoint;
        Backtrack max;
        for (int childIndex = 1; childIndex <= numberOfChildren; childIndex++) {
            for (int kS = 0; kS <= stringDeletionLimit; kS++) {
                for (int kT = 0; kT <= treeDeletionLimit; kT++) {
                    endPoint = spans[childIndex] - kT + kS;
                    if(endPoint < 0 | endPoint > this.getStringEndIndex()) {
                        dPTable[childIndex][kT][kS] = new Backtrack(Double.NEGATIVE_INFINITY);
                        continue;
                    }
                    if(endPoint == 0) {
                        if(kS == 0)
                            dPTable[childIndex][kT][kS] = new Backtrack(0.0);
                        else
                            dPTable[childIndex][kT][kS] = new Backtrack(Double.NEGATIVE_INFINITY);
                        continue;
                    }
                    max = findMaxMappingByEndPoint(childIndex, endPoint, kT, kS);
                    //deletion from the string
                    if(kS > 0 && dPTable[childIndex][kT][kS - 1].compareTo(max) > 0) {
                        max = new Backtrack(dPTable[childIndex][kT][kS - 1].getScore(),
                                kT, kS - 1, childIndex);
                    }
                    //deletion from the tree
                    int childNodeSpan = spans[childIndex] - spans[childIndex - 1];
                    if(kT >= childNodeSpan
                            && dPTable[childIndex-1][kT - childNodeSpan][kS].compareTo(max) > 0) {
                        max = new Backtrack(dPTable[childIndex-1][kT - childNodeSpan][kS].getScore(),
                                kT - childNodeSpan, kS, childIndex-1);
                    }
                    dPTable[childIndex][kT][kS] = max;
                }
            }
        }
        buildResult();
    }

    private void init() {
        calculateCumulativeSpans();
        //init DP Table
        for (int kS = 0; kS <= stringDeletionLimit; kS++) {
            for (int kT = 1; kT <= treeDeletionLimit; kT++) {
                dPTable[0][kT][kS] = new Backtrack(Double.NEGATIVE_INFINITY);
            }
            dPTable[0][0][kS] = new Backtrack(0.0);

        }
    }

    private void calculateCumulativeSpans() {
        int i = 0;
        int span = 0;
        spans[i] = span;
        for(Node childNode : node.getChildren()) {
            i++;
            span += childNode.getSpan();
            spans[i] = span;
        }
    }

    private Backtrack findMaxMappingByEndPoint(int childIndex, int endPoint, int kT, int kS){
        Backtrack max = new Backtrack(Double.NEGATIVE_INFINITY);
        HashMap<Integer, List<Mapping>> mappingsByEndPoint =
                mappingsByChildren.get(childIndex);
        List<Mapping> mappings = mappingsByEndPoint.get(endPoint);
        for(Mapping mapping : mappings) {
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

    private void buildResult() {
        Mapping nodeMapping;
        Mapping childMapping;
        Backtrack backtrack;
        initMappingsByEndPoints();
        for (int kT = 0; kT <= treeDeletionLimit; kT++) {
            for (int kS = 0; kS <= stringDeletionLimit; kS++) {
                backtrack = dPTable[numberOfChildren][kT][kS];
                nodeMapping = new Mapping(node, stringAbsoluteIndex, kS, kT, backtrack.getScore());
                if(!nodeMapping.getScore().equals(Double.NEGATIVE_INFINITY)) {
                    int childIndex = numberOfChildren;
                    int endIndexForChildMapping = nodeMapping.getEndIndex();
                    do {
                        Node child = this.node.getChildren().get(childIndex - 1);
                        int childMappingStart = calcStartIndex(endIndexForChildMapping, kS, kT, child);
                        childMapping = new Mapping(child, childMappingStart, nodeMapping.getEndIndex(),
                                kS-backtrack.getPreviousStringDeletions(),
                                kT-backtrack.getPreviousTreeDeletions());
                        nodeMapping.addChildMapping(childMapping);
                        childIndex = backtrack.getChildIndex();
                        endIndexForChildMapping = childMappingStart;
                        backtrack = dPTable[childIndex][backtrack.getPreviousTreeDeletions()]
                                [backtrack.getPreviousStringDeletions()];
                    } while(!backtrack.isFirst());
                }
                mappingsByEndPoints.get(nodeMapping.getEndIndex()).add(nodeMapping);
            }
        }
    }

    private void initMappingsByEndPoints() {
        int noDeletionEndPoint = stringAbsoluteIndex - 1 + node.getSpan();
        IntStream.rangeClosed(noDeletionEndPoint - treeDeletionLimit,
                noDeletionEndPoint + stringDeletionLimit)
                .forEach(endPoint -> mappingsByEndPoints.put(endPoint, new LinkedList<>()));
    }

    private int calcStartIndex(int endIndex, int kS, int kT, Node node) {
        return 1 + endIndex - (node.getSpan() + kS - kT);
    }

}
