package tests;

import helpers.ChildrenSubsetEncoding;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class EncodingTests {

    @Test
    public void childrenSubsetEncodingTest() {
        childrenSubsetEncodingTest(new HashSet<>(Arrays.asList(2, 3)), 6);
        childrenSubsetEncodingTest(new HashSet<>(Arrays.asList(1, 3)), 5);
        childrenSubsetEncodingTest(new HashSet<>(Arrays.asList(1, 2, 3)), 7);
        childrenSubsetEncodingTest(new HashSet<>(Arrays.asList(2, 4)), 10);
        childrenSubsetEncodingTest(Collections.emptySet(), 0);
    }

    private void childrenSubsetEncodingTest(Set<Integer> childrenSet, int index) {
        int receivedIndex = ChildrenSubsetEncoding
                .childrenSubsetToIndex(childrenSet);
        Assertions.assertEquals(index, receivedIndex);

        Set<Integer> receivedList = ChildrenSubsetEncoding
                .indexToChildrenSet(index);
        Assertions.assertIterableEquals(childrenSet, receivedList,
                "childrenList: "
                        + childrenSet.stream().reduce("",(acc,i)->acc +"," + i.toString(), String::concat)
                        + "received: "
                        + receivedList.stream().reduce("",(acc,i)->acc + i.toString(), String::concat));
    }

    @Test
    public void getMissingChildTest(){
        getMissingChildTest(new HashSet<>(Arrays.asList(2, 3)),
                new HashSet<>(Arrays.asList(2, 3)), null);
        getMissingChildTest(new HashSet<>(Arrays.asList(1, 2, 3)),
                new HashSet<>(Arrays.asList(2, 3)), 1);
        getMissingChildTest(new HashSet<>(Arrays.asList(2, 1)),
                new HashSet<>(Arrays.asList(1)), 2);
        getMissingChildTest(new HashSet<>(Arrays.asList(2, 3)),
                new HashSet<>(Arrays.asList(2)), 3);

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ChildrenSubsetEncoding.getMissingChild(
                        ChildrenSubsetEncoding
                                .childrenSubsetToIndex(new HashSet<>(Arrays.asList(1, 2, 3))),
                        ChildrenSubsetEncoding
                                .childrenSubsetToIndex(new HashSet<>(Arrays.asList(3)))));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ChildrenSubsetEncoding.getMissingChild(
                        ChildrenSubsetEncoding
                                .childrenSubsetToIndex(new HashSet<>(Arrays.asList(1, 2, 3))),
                        ChildrenSubsetEncoding
                                .childrenSubsetToIndex(new HashSet<>(Arrays.asList(3, 4)))));
    }

    private void getMissingChildTest(Set<Integer> set1, Set<Integer> set2, Integer missingChildIndex) {
        Assertions.assertEquals(missingChildIndex, ChildrenSubsetEncoding.getMissingChild(
                ChildrenSubsetEncoding.childrenSubsetToIndex(set1),
                ChildrenSubsetEncoding.childrenSubsetToIndex(set2)));
    }
}
