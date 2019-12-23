package helpers;

import structures.Node;

import java.util.List;

public class IndexToChildNodeEncoder {
    private List<Node> childrenList;
    private int numberOfChildNodes;

    public IndexToChildNodeEncoder(List<Node> childrenList) {
        this.childrenList = childrenList;
        this.numberOfChildNodes = childrenList.size();
    }

    public int childNodeToLTRIndex(Node child) {
        return childrenList.indexOf(child) + 1;
    }

    public Node indexToChildNode(int index, boolean reverseOrder) {
        if(reverseOrder)
            index = numberOfChildNodes - index;
        else
            index -= 1;
        return childrenList.get(index);
    }
}
