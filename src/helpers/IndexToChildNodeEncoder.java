package helpers;

import structures.Node;

import java.util.List;

public class IndexToChildNodeEncoder {
    private List<Node> childrenList;
    private int numberOfChildNodes;

    /**
     * @param childrenList A list of sibling nodes from left to right
     */
    public IndexToChildNodeEncoder(List<Node> childrenList) {
        this.childrenList = childrenList;
        this.numberOfChildNodes = childrenList.size();
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
     * @param reverseOrder is the index from right to left
     * @return returns the {@code index}th node from left to right if {@code reverseOrder==true},
     * otherwise from right to left.
     */
    public Node indexToChildNode(int index, boolean reverseOrder) {
        if(reverseOrder)
            index = numberOfChildNodes - index;
        else
            index -= 1;
        return childrenList.get(index);
    }
}
