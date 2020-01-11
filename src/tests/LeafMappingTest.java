package tests;

import NodeMappingAlgorithms.LeafMappingAlgorithm;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import structures.GeneGroup;
import structures.Mapping;
import structures.Node;
import structures.NodeType;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class LeafMappingTest {
    private static BiFunction<GeneGroup, Character, Double> substitutionFunction;

    @BeforeAll
    static void classSetUp() {
        substitutionFunction = (geneGroup, chr) -> {
            if (chr.equals(geneGroup.getCog().charAt(3)))
                return 1.0;
            else
                return Double.NEGATIVE_INFINITY;
        };
    }

    private static Stream<Arguments> LeafMappingTestProvider() {
        List<Node> emptyList = Collections.emptyList();
        return Stream.of(
                Arguments.of("123423145682", new Node(1, NodeType.LEAF, "cog2+", emptyList, false), 0, 0),
                Arguments.of("123423145682", new Node(1, NodeType.LEAF, "cog2+", emptyList, false), 2, 3),
                Arguments.of("1", new Node(1, NodeType.LEAF, "cog5+", emptyList, false), 2, 1),
                Arguments.of("", new Node(1, NodeType.LEAF, "cog5+", emptyList, false), 2, 1),
                Arguments.of("156253451525", new Node(1, NodeType.LEAF, "cog5+", emptyList, false), 0, 1),
                Arguments.of("156253451525", new Node(1, NodeType.LEAF, "cog5+", emptyList, false), 2, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("LeafMappingTestProvider")
    void leafMappingTest(String string, Node leaf, int treeDeletionLimit, int stringDeletionLimit){
        LeafMappingAlgorithm algorithm = new LeafMappingAlgorithm(string, leaf,
                treeDeletionLimit, stringDeletionLimit, substitutionFunction);
        algorithm.runAlgorithm();
        HashMap<Integer, List<Mapping>> resultMappingsByEndPoints = algorithm.getResultMappingsByEndPoints();
        MappingAssertions.assertLeafMappings(resultMappingsByEndPoints, string, leaf.getLabel(), substitutionFunction);
        algorithm.printResultMappings();
    }
}
