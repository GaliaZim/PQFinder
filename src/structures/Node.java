package structures;

import java.util.*;
import java.util.stream.Collectors;


public class Node {
    private int index;
    private int span;
    private List<Node> children;
    private int numberOfChildren;
    private NodeType type;
    private String cog;
    private List<Node> leafs;
    private Integer height;

    public Node(int index, int span, List<Node> children, NodeType type, String cog) {
        this.index = index;
        this.span = span;
        this.children = children;
        this.numberOfChildren = children.size();
        this.type = type;
        this.cog = cog;
        this.leafs = null;
        this.height = null;
    }

    public Node(NodeType type,  String cog, List<Node> children) {
        this(-1, -1, children, type, cog);
    }

    public Node(int index, int span, NodeType type) {this(index, span, new LinkedList<>(), type);}

    public Node(int index, int span, NodeType type, String cog) {
        this(index, span, new LinkedList<>(), type, cog);
    }

    public Node(int index, int span, List<Node> children, NodeType type) {
        this(index, span, children, type, null);
    }

    public String getCog() {return cog;}

    public int getIndex() {
        return index;
    }

    public int getSpan() {
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

    public void addChild(Node child) {
        this.children.add(child);
        numberOfChildren ++;
    }

    @Override
    public String toString() {
        return "Node{" +
                "index=" + index +
                ", type=" + type +
                ", cog='" + cog + '\'' +
                '}';
    }

    public List<Node> getChildrenReversed() {
        List<Node> reversedChildren = new LinkedList<>(children);
        Collections.reverse(reversedChildren);
        return reversedChildren;
    }
}
