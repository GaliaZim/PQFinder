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
    private static BiFunction<GeneGroup, GeneGroup, Double> substitutionFunction;
    private static GeneGroupsProvider geneGroupsProvider;

    @BeforeAll
    static void classSetUp() {
        substitutionFunction = (leafGeneGroup, stringGeneGroup) -> {
            if (stringGeneGroup.getCog().equals(leafGeneGroup.getCog()))
                return 1.0;
            else
                return Double.NEGATIVE_INFINITY;
        };
        geneGroupsProvider = GeneGroupsProvider.getInstance();
    }

    private static Stream<Arguments> LeafMappingTestProvider() {
        ArrayList<GeneGroup> s = new ArrayList<>();
        List<Node> emptyList = Collections.emptyList();
        return Stream.of(
                Arguments.of("123423145682",
                        new Node(1, NodeType.LEAF, "cog2+", emptyList, false), 0, 0),
                Arguments.of("123423145682",
                        new Node(1, NodeType.LEAF, "cog2+", emptyList, false), 2, 3),
                Arguments.of("1",
                        new Node(1, NodeType.LEAF, "cog5+", emptyList, false), 2, 1),
                Arguments.of("",
                        new Node(1, NodeType.LEAF, "cog5+", emptyList, false), 2, 1),
                Arguments.of("156253451525",
                        new Node(1, NodeType.LEAF, "cog5+", emptyList, false), 0, 1),
                Arguments.of("156253451525",
                        new Node(1, NodeType.LEAF, "cog5+", emptyList, false), 2, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("LeafMappingTestProvider")
    void leafMappingTest(String str, Node leaf, int treeDeletionLimit, int stringDeletionLimit){
        ArrayList<GeneGroup> string = geneGroupsProvider.convertToGeneGroups(str);
        LeafMappingAlgorithm algorithm = new LeafMappingAlgorithm(string, leaf,
                treeDeletionLimit, stringDeletionLimit, substitutionFunction);
        algorithm.runAlgorithm();
        HashMap<Integer, List<Mapping>> resultMappingsByEndPoints = algorithm.getResultMappingsByEndPoints();
        MappingAssertions.assertLeafMappings(resultMappingsByEndPoints, string, leaf.getLabel(), substitutionFunction);
        System.out.println(str);
        algorithm.printResultMappings();
    }
}
