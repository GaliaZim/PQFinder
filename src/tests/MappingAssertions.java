package tests;

import org.junit.jupiter.api.Assertions;
import structures.GeneGroup;
import structures.Mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

class MappingAssertions {

    static void assertGenericMappingMapProperties(HashMap<Integer, List<Mapping>> mappingsByEndPoint) {
        mappingsByEndPoint.forEach((endPoint,mappingList) -> assertMappingList(mappingList, endPoint));
    }

    private static void assertMappingList(List<Mapping> mappings, int endPoint) {
        mappings.forEach(mapping -> {
            assertMappingIndices(mapping, endPoint);
            assertMappingScore(mapping);
            if(!mapping.getScore().equals(Double.NEGATIVE_INFINITY)) {
                assertTreeDeletions(mapping);
                assertStringDeletions(mapping);
            }
        });
    }

    private static void assertMappingIndices(Mapping mapping, int endPoint) {
        Assertions.assertTrue(mapping.getStartIndex() <= mapping.getEndIndex(),
                "Mapping ends before start index: " + mapping);
        Assertions.assertEquals(endPoint, mapping.getEndIndex(),
                "The mapping's end index does not match its index in mappingsByEndPoint");
    }

    private static void assertMappingScore(Mapping mapping) {
        if(mapping.getScore().equals(Double.NEGATIVE_INFINITY)) {
            Assertions.assertEquals(0, mapping.getChildrenMappings().size(),
                    "Children were mapped, but score is -infinity");
        } else {
            Double childrenSumScore = mapping.getChildrenMappings().stream()
                    .reduce(0.0, (acc, m) -> acc + m.getScore(), Double::sum);
            Assertions.assertEquals(mapping.getScore(), childrenSumScore,
                    "Children's sum score doesn't match mapping score: " + mapping);
        }
    }

    private static void assertStringDeletions(Mapping mapping) {
        Assertions.assertEquals(mapping.getStringDeletions(), mapping.getDeletedStringIndices().size(),
                "Number of deletions from the string doesn't match number of indices in the deleted list: "
                        + mapping);
    }

    private static void assertTreeDeletions(Mapping mapping) {
        Assertions.assertEquals(mapping.getTreeDeletions(), mapping.getDeletedDescendant().size(),
                "Number of deletions from the tree doesn't match number of children in the deleted list: "
                        + mapping);
    }

    static void assertLeafMappings(HashMap<Integer, List<Mapping>> resultMappingsByEndPoints,
                                   ArrayList<GeneGroup> string, GeneGroup leafLabel,
                                   BiFunction<GeneGroup, GeneGroup, Double> substitutionFunction) {
        double expectedScore;
        int endPoint;
        Assertions.assertEquals(string.size() + 1, resultMappingsByEndPoints.size(),
                "Number of end points in result mapping does not match string length");
        for(Map.Entry<Integer, List<Mapping>> entry : resultMappingsByEndPoints.entrySet()) {
            endPoint = entry.getKey();
            List<Mapping> mappingList = entry.getValue();
            Assertions.assertEquals(1, mappingList.size(),
                    "Illegal number of mappings at end point " + endPoint);
            Mapping mapping = mappingList.get(0);
            Assertions.assertEquals(endPoint, mapping.getStartIndex(),
                    "The mapping's start index does not match its index in mappingsByEndPoint");
            Assertions.assertEquals(endPoint, mapping.getEndIndex(),
                    "The mapping's end index does not match its index in mappingsByEndPoint");
            Assertions.assertEquals(0, mapping.getTreeDeletions(),
                    "Allowed tree deletions in leaf mapping");
            Assertions.assertEquals(0, mapping.getStringDeletions(),
                    "Allowed string deletions in leaf mapping");
            if(endPoint == 0)
                expectedScore = 0.0;
            else
                expectedScore = substitutionFunction.apply(leafLabel, string.get(mapping.getStartIndex() - 1));
            Assertions.assertEquals(expectedScore, mapping.getScore(),
                    "Score does not match substitution function");
        }
    }
}
