package helpers;

import java.util.*;

public class ChildrenSubsetEncoding {
    /**
     * @param index An index of a subset of sibling nodes ({@code index >= 0})
     * @return A set of sibling nodes indices from left to right. The left most sibling has index 1.
     */
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

    /**
     * @param children A set of sibling indices. The set values are integers larger than 0.
     * @return A subset index matchon the set of sibling indices.
     * @throws IllegalArgumentException If given an illegal child index
     */
    public static int childrenSubsetToIndex(Set<Integer> children) {
        int index = 0;
        for (Integer childIndex : children) {
            if(childIndex < 1)
                throw new IllegalArgumentException("Illegal child index " + childIndex);
            index += Math.pow(2, childIndex - 1);
        }
        return index;
    }

    /**
     * @param setIndex A subset index
     * @param childIndex A node index
     * @return An index of a subset that is the subset represented by {@code setIndex} without the
     * child represented by {@code childIndex}
     */
    public static int getSetIndexWithoutChild(int setIndex, Integer childIndex) {
        Set<Integer> set = indexToChildrenSet(setIndex);
        if(set.remove(childIndex))
            return childrenSubsetToIndex(set);
        else
            return setIndex;
    }

    /**
     *
     * @param originSetIndex A set index
     * @param newSetIndex A set index
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
