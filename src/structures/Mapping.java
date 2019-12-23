package structures;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Mapping implements Comparable<Mapping>{
    private Node node;
    private int startIndex;
    private int endIndex;
    private int stringDeletions;
    private int treeDeletions;
    private Double score;
    private List<Mapping> childrenMappings;
    private List<Node> deletedDescendant;
    private List<Integer> deletedStringIndices;

    public Mapping(Node node, int startIndex, int endIndex, int stringDeletions, int treeDeletions, Double score) {
        this.node = node;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.stringDeletions = stringDeletions;
        this.treeDeletions = treeDeletions;
        this.score = score;
        this.childrenMappings = new LinkedList<>();
        this.deletedDescendant = new LinkedList<>();
        this.deletedStringIndices = new LinkedList<>();
    }

    public Mapping(Node node, int startIndex, int stringDeletions, int treeDeletions, Double score) {
        this(node, startIndex, 0, stringDeletions, treeDeletions, score);
    }

    public Mapping(Node node, int startIndex, int endIndex, int stringDeletions,
                   int treeDeletions) {
        this(node, startIndex, endIndex, stringDeletions, treeDeletions, Double.NEGATIVE_INFINITY);
    }

    public int getEndIndex() {
        if(endIndex == 0)
            endIndex = startIndex - 1 + node.getSpan() + stringDeletions - treeDeletions;
        return endIndex;
    }

    public void addChildMapping(Mapping childMapping) {
        if(childMapping == null)
            throw new IllegalArgumentException("Cannot add NULL as a child mapping of " + node.toString());
        this.childrenMappings.add(childMapping);
        this.deletedStringIndices.addAll(childMapping.deletedStringIndices);
        this.deletedDescendant.addAll(childMapping.deletedDescendant);
    }

    public void addDeletedChild(Node child) {
        this.deletedDescendant.addAll(child.getLeafs());
    }

    public void addDeletedStringIndex(int index) {
        this.deletedStringIndices.add(index);
    }

    public Node getNode() {
        return node;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getStringDeletions() {
        return stringDeletions;
    }

    public int getTreeDeletions() {
        return treeDeletions;
    }

    public Double getScore() {
        return score;
    }

    public List<Mapping> getChildrenMappings() {
        return childrenMappings;
    }

    public List<Node> getDeletedDescendant() {
        return deletedDescendant;
    }

    public List<Integer> getDeletedStringIndices() {
        return deletedStringIndices;
    }

    public String toString() {
        return String.format("{%s(%d) -> [%d,%d], dT=%d, dS=%d, score=%.2f}",
                node.getCog(), node.getIndex(), startIndex, getEndIndex(),
                treeDeletions, stringDeletions, score);
    }
}
