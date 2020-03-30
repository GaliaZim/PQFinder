package helpers;

import structures.ChildrenOrder;
import structures.Node;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;

public class IndexToChildNodeEncoder {
    private List<Node> childrenList;
    private EnumMap<ChildrenOrder, Function<Integer,Integer>> indexToChildNodeMethods;

    /**
     * @param childrenList A list of sibling nodes from left to right
     */
    public IndexToChildNodeEncoder(List<Node> childrenList) {
        this.childrenList = childrenList;
        int numberOfChildNodes = childrenList.size();
        this.indexToChildNodeMethods = new EnumMap<>(ChildrenOrder.class);
        indexToChildNodeMethods.put(ChildrenOrder.LTR, index ->  index -1);
        indexToChildNodeMethods.put(ChildrenOrder.RTL, index ->  numberOfChildNodes - index);
    }

    /**
     * @param child A node of the list of nodes given in the constructor
     * @return The index of the node looking from left to right on the children.
     * For the first child from the left the value 1 will be returned.
     * If there is no such child in the list, 0 will be returned.
     */
    public int childNodeToLTRIndex(Node child) {
        return childrenList.indexOf(child) + 1;
    }

    /**
     * @param index the child index between 1 and the number of children
     * @param childrenOrder an enum indicating the order of the children
     * @return returns the {@code index}th node from left to right if {@code childrenOrder==LTR},
     * otherwise from right to left.
     */
    public Node indexToChildNode(int index, ChildrenOrder childrenOrder) {
        index = this.indexToChildNodeMethods.get(childrenOrder).apply(index);
        return childrenList.get(index);
    }
}

