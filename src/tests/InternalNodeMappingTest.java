package tests;

import NodeMappingAlgorithms.MappingAlgorithmBuilder;
import NodeMappingAlgorithms.NodeMappingAlgorithm;
import helpers.PrepareInput;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import structures.GeneGroup;
import structures.Mapping;
import structures.Node;
import visualization.VisualizeMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

class InternalNodeMappingTest {
    private static BiFunction<GeneGroup, GeneGroup, Double> substitutionFunction;
    private static GeneGroupsProvider geneGroupsProvider;
    private final static String treeHeight3JsonPath = ".\\src\\tests\\treeJSONS\\mixedTypeTreeHeight3";
    private final static String treeHeight4JsonPath = ".\\src\\tests\\treeJSONS\\mixedTypeTreeHeight4";
    private final static String qNodeTreeHeight1JsonPath = ".\\src\\tests\\treeJSONS\\qNodeOneTier";
    private final static String pNodeTreeHeight1JsonPath = ".\\src\\tests\\treeJSONS\\pNodeOneTier";

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

    private static Stream<Arguments> internalNodeMappingTest() {
        List<String> jsonPaths = Arrays.asList(treeHeight3JsonPath, treeHeight4JsonPath,
                pNodeTreeHeight1JsonPath, qNodeTreeHeight1JsonPath);
        List<Node> roots = new ArrayList<>();
        for (String jsonPath : jsonPaths) {
            try {
                roots.add(PrepareInput.buildTree(jsonPath));
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }

        Stream<Arguments> argumentsStream = Stream.of(
                Arguments.of("5741132534623", 0, 0),
                Arguments.of("57413722534623", 2, 3),
                Arguments.of("3216514", 2, 1),
                Arguments.of("", 2, 1),
                Arguments.of("124231452", 0, 1),
                Arguments.of("321654314", 2, 0),
                Arguments.of("12342314", 0, 0),
                Arguments.of("124231452", 2, 3),
                Arguments.of("321", 2, 1),
                Arguments.of("132", 0, 0)
        );

        return argumentsStream.flatMap(arguments -> {
            Object[] args = arguments.get();
            return roots.stream().map(root ->
                    Arguments.arguments(root, args[0], args[1], args[2]));
        });
    }

    @ParameterizedTest
    @MethodSource("internalNodeMappingTest")
    void internalNodeMappingTest(Node root, String str, int treeDeletionLimit, int stringDeletionLimit)
            throws ExceptionInInitializerError {
        ArrayList<GeneGroup> string = geneGroupsProvider.convertToGeneGroups(str);
        NodeMappingAlgorithm algorithm =
                MappingAlgorithmBuilder.build(string, root, treeDeletionLimit, stringDeletionLimit,
                        substitutionFunction);
        algorithm.runAlgorithm();
        HashMap<Integer, List<Mapping>> resultMappingsByEndPoints = algorithm.getResultMappingsByEndPoints();
//        VisualizeMapping.draw(algorithm.getNode(), algorithm.getString(), algorithm.getBestStringIndexToLeafMapping());
        MappingAssertions.assertGenericMappingMapProperties(resultMappingsByEndPoints);
    }
}
