package tests;

import NodeMappingAlgorithms.LeafMappingAlgorithm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import structures.Mapping;
import structures.Node;
import structures.NodeType;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class LeafMappingTest {
    private static BiFunction<String, Character, Double> substitutionFunction;

    @BeforeAll
    static void classSetUp() {
        substitutionFunction = (str, chr) -> {
            if (chr.equals(str.charAt(3)))
                return 1.0;
            else
                return Double.NEGATIVE_INFINITY;
        };
    }

    private static Stream<Arguments> LeafMappingTestProvider() {
        List<Node> emptyList = Collections.emptyList();
        return Stream.of(
                Arguments.of("123423145682", new Node(1, NodeType.LEAF, "cog2", emptyList, false), 0, 0),
                Arguments.of("123423145682", new Node(1, NodeType.LEAF, "cog2", emptyList, false), 2, 3),
                Arguments.of("1", new Node(1, NodeType.LEAF, "cog5", emptyList, false), 2, 1),
                Arguments.of("", new Node(1, NodeType.LEAF, "cog5", emptyList, false), 2, 1),
                Arguments.of("156253451525", new Node(1, NodeType.LEAF, "cog5", emptyList, false), 0, 1),
                Arguments.of("156253451525", new Node(1, NodeType.LEAF, "cog5", emptyList, false), 2, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("LeafMappingTestProvider")
    void leafMappingTest(String string, Node leaf, int treeDeletionLimit, int stringDeletionLimit){
        LeafMappingAlgorithm algorithm = new LeafMappingAlgorithm(string, leaf,
                treeDeletionLimit, stringDeletionLimit, substitutionFunction);
        algorithm.runAlgorithm();
        HashMap<Integer, List<Mapping>> resultMappingsByEndPoints = algorithm.getResultMappingsByEndPoints();
        assertLeafMappings(resultMappingsByEndPoints, string, leaf.getCog());
    }

    private void assertLeafMappings(HashMap<Integer, List<Mapping>> resultMappingsByEndPoints,
                                    String string, String leafCog) {
        double expectedScore;
        int endPoint;
        Assertions.assertEquals(string.length() + 1, resultMappingsByEndPoints.size(),
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
                expectedScore = substitutionFunction.apply(leafCog, string.charAt(mapping.getStartIndex() - 1));
            Assertions.assertEquals(expectedScore, mapping.getScore(),
                    "Score does not match substitution function");
        }
    }
}
