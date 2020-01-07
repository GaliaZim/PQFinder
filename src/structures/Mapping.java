package structures;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Mapping implements Comparable<Mapping>{
    /**
     * The mapped node
     */
    private Node node;
    /**
     * the index of {@code string} from which the mapped substring begins
     */
    private int startIndex;
    /**
     * the index of {@code string} in which the mapped substring ends
     */
    private int endIndex;
    /**
     * The exact number of deletions from the string in the mapping
     */
    private int stringDeletions;
    /**
     * The exact number of deletions from the tree in the mapping
     */
    private int treeDeletions;
    /**
     * The score of the mapping. If the mapping is not possible {@code score = -infinity}
     */
    private Double score;
    /**
     * The mapping of the children of {@code node} chosen under this mapping. The is one mapping for every not deleted
     * child of {@code node}
     */
    private List<Mapping> childrenMappings;
    /**
     * A list of leaf nodes of the subtrees rooted in the children of {@code node} that were deleted under this mapping
     */
    private List<Node> deletedDescendant;
    /**
     * A list of the string indices that were deleted under this mapping
     */
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
        this.endIndex = startIndex - 1 + node.getSpan() + stringDeletions - treeDeletions;
    }

    public Mapping(Node node, int startIndex, int endIndex, int stringDeletions,
                   int treeDeletions) {
        this(node, startIndex, endIndex, stringDeletions, treeDeletions, Double.NEGATIVE_INFINITY);
    }

    public int getEndIndex() {
        return endIndex;
    }

    /**
     * @param childMapping A chosen mapping for one of the children of {@code node}
     */
    public void addChildMapping(Mapping childMapping) {
        if(childMapping == null)
            throw new IllegalArgumentException("Cannot add NULL as a child mapping of " + node.toString());
        if(existsInChildrenMappings(childMapping.getNode()))
            throw new IllegalArgumentException(childMapping.getNode() + " already has a chosen mapping");
        this.childrenMappings.add(childMapping);
        this.deletedStringIndices.addAll(childMapping.deletedStringIndices);
        this.deletedDescendant.addAll(childMapping.deletedDescendant);
    }

    /**
     * @param child A node
     * @return {@code true} if {@code child} already has a mapping in {@code this.childrenMappings}
     */
    private boolean existsInChildrenMappings(Node child) {
        return childrenMappings.stream().anyMatch(mapping -> mapping.node.equals(child));
    }

    /**
     * @param child A child node of {@code node}
     * Adds the leaf nodes in the subtree rooted in {@code child} to the list of deleted descendants under this mapping
     */
    public void addDeletedChild(Node child) {
        this.deletedDescendant.addAll(child.getLeafs());
    }

    /**
     * @param index A string index
     * Adds {@code index} to the list of deleted string indeices under this mapping
     */
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

    /**
     * @return the ono-to-one mapping between string indices and leaf nodes (of the tree rooted in{@code node})
     * that yields this mapping
     */
    public HashMap<Integer, Node> getLeafMappings() {
        HashMap<Integer, Node> mappingsToReturn = new HashMap<>();
        if(node.getType() == NodeType.LEAF)
            mappingsToReturn.put(startIndex, node);
        else
            childrenMappings.forEach(childMapping ->
                    mappingsToReturn.putAll(childMapping.getLeafMappings()));
        return mappingsToReturn;
    }

    /**
     * @param other another mapping
     * @return
     * Compares first according to score, if the scores are equal, the mapping with the smaller number of deletions is
     * the bigger one.
     */
    @Override
    public int compareTo(Mapping other) {
        int diff = this.getScore().compareTo(other.getScore());
        if(diff == 0) {
            //if the score is equal, the one with less deletions is better
            diff = (other.getStringDeletions() + other.getTreeDeletions())
                    - (this.getStringDeletions() + this.getTreeDeletions());
        }
        return diff;
    }
}
