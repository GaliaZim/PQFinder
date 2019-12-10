package helpers;

import java.util.*;

public class ChildrenSubsetEncoding {
    public static Set<Integer> indexToChildrenSet(int index) {
        Set<Integer> children = new HashSet<>(); //TODO: capacity
        int childIndex = 1;
        while (index > 0) {
            if(index % 2 == 1)
                children.add(childIndex);
            index /= 2;
            childIndex++;
        }
        return children;
    }

    public static int childrenSubsetToIndex(Set<Integer> children) {
        int index = 0;
        for (Integer childIndex : children) {
            if(childIndex < 1)
                throw new IllegalArgumentException("Illegal child index " + childIndex);
            index += Math.pow(2, childIndex - 1);
        }
        return index;
    }

    public static int getSetIndexWithoutChild(int setIndex, Integer childIndex) {
        Set<Integer> set = indexToChildrenSet(setIndex);
        if(set.remove(childIndex))
            return childrenSubsetToIndex(set);
        else
            return setIndex;
    }

    /**
     *
     * @param originSetIndex
     * @param newSetIndex
     * @return the index of the child that is in one set and not in the other,
     * and null if there is no such child
     * @throws IllegalArgumentException if there is a difference of more than one child
     */
    public static Integer getMissingChild(int originSetIndex, int newSetIndex) {
        int diff = Math.abs(originSetIndex - newSetIndex);
        Set<Integer> diffSet = indexToChildrenSet(diff);
        if(diffSet.size() > 1)
            throw new IllegalArgumentException("More than one child difference");
        return diffSet.stream().findFirst().orElse(null);
    }
}
