package structures;

import java.util.*;
import java.util.stream.Collectors;


public class Node {
    /**
     * The node index in the tree
     */
    private int index;
    private Integer span;
    private List<Node> children;
    private int numberOfChildren;
    private NodeType type;
    /**
     * The label of the leaf node. If it is not a leaf the label is null
     */
    private String cog;
    /**
     * The leafs of the tree rooted in {@code node}
     */
    private List<Node> leafs;
    private Integer height;

    public Node(int index, Integer span, List<Node> children, NodeType type, String cog, Integer height,
                List<Node> leafs) {
        this.index = index;
        this.span = span;
        this.children = children;
        this.numberOfChildren = children.size();
        this.type = type;
        this.cog = cog;
        this.height = height;
        this.leafs = leafs;
    }

    public Node(int index, NodeType type, String cog, List<Node> children, boolean setUndefined) {
        this(index, null, children, type, cog, null, null);
        if(setUndefined)
            setUndefinedFields();
    }

    public String getCog() {return cog;}

    public int getIndex() {
        return index;
    }

    public int getSpan() {
        if(span == null)
            setSpan();
        return span;
    }

    public List<Node> getChildren() {
        return children;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public NodeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Node{" +
                "index=" + index +
                ", type=" + type +
                ", cog='" + cog + '\'' +
                '}';
    }

    /**
     * @return The children of {@code node} from right to left
     */
    public List<Node> getChildrenReversed() {
        List<Node> reversedChildren = new LinkedList<>(children);
        Collections.reverse(reversedChildren);
        return reversedChildren;
    }

    public List<Node> getLeafs() {
        if(leafs == null) {
            setLeafs();
        }
        return leafs;
    }

    /**
     * Recursively builds the leaf list of {@code node}
     */
    private void setLeafs() {
        if(type == NodeType.LEAF)
            leafs = Collections.singletonList(this);
        else
            leafs = this.children.stream().flatMap(child -> child.getLeafs().stream())
                    .collect(Collectors.toList());
    }

    public int getHeight() {
        if(height == null) {
            setHeight();
        }
        return height;
    }

    /**
     * Recursively calculates the height of {@code node} and sets it
     */
    private void setHeight() {
        if(type == NodeType.LEAF)
            height = 0;
        else
            height = 1 + getChildren().stream().map(Node::getHeight)
                        .max(Integer::compareTo).orElseThrow(() ->
                                new IllegalArgumentException("An internal node has no children"));
    }

    private void setSpan() {
        span = getLeafs().size();
    }

    /**
     * Sets the values of {@code this.leafs}, {@code this.height} and {@code this.span}
     */
    private void setUndefinedFields() {
        setLeafs();
        setHeight();
        setSpan();
    }
}
