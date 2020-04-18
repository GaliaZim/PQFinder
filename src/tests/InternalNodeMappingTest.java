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
import java.util.function.Function;
import java.util.stream.Stream;

class InternalNodeMappingTest {
    private static BiFunction<GeneGroup, GeneGroup, Double> substitutionFunction;
    private static GeneGroupsProvider geneGroupsProvider;
    private static Function<GeneGroup, Double> deletionCost;
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
        deletionCost = geneGroup -> -0.5;
    }

    private static Stream<Arguments> internalNodeMappingTest() {
        List<String> jsonPaths = Arrays.asList(
                treeHeight3JsonPath, treeHeight4JsonPath,
                pNodeTreeHeight1JsonPath, qNodeTreeHeight1JsonPath);
        List<Node> roots = new ArrayList<>();
        for (String jsonPath : jsonPaths) {
            try {
                roots.add(PrepareInput.buildTreeFromJSON(jsonPath));
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
        MappingAssertions.assertGenericMappingMapProperties(resultMappingsByEndPoints, string, substitutionFunction);
    }

//    @Test
    void visualizeTest() {
        try {
            Node tree = PrepareInput.buildTreeFromJSON(treeHeight4JsonPath);
            ArrayList<GeneGroup> string = geneGroupsProvider.convertToGeneGroups("57413722534623");
            NodeMappingAlgorithm algorithm =
                    MappingAlgorithmBuilder.build(string, tree, 2, 3,
                            substitutionFunction);
            algorithm.runAlgorithm();
            VisualizeMapping.visualize(algorithm.getNode(), algorithm.getString(),
                    algorithm.getBestStringIndexToLeafMapping());
            Thread.sleep(5000);
        } catch (IOException | ParseException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    private static Stream<Arguments> pNodeMappingTest() {
        Node pqt = PrepareInput.buildTreeFromParenRepresentation("(cog1 cog2 (cog3 cog4) cog5)");
        Function<GeneGroup, Double> deletionCostFunction =
                geneGroup -> {
            if("cog6".equals(geneGroup.getCog())  | "cog1".equals(geneGroup.getCog()))
                return -0.2;
            else
                return deletionCost.apply(geneGroup);};
        final Stream<Arguments> argumentsStream = Stream.of(
                Arguments.of("85214355348", 0, 0),
                Arguments.of("85214355348", 1, 0),
                Arguments.of("3642158", 0, 1),
                Arguments.of("3642158", 1, 1),
                Arguments.of("86324568", 1, 0),
                Arguments.of("86324568", 2, 2),
                Arguments.of("", 2, 1),
                Arguments.of("863624568", 1, 1),
                Arguments.of("863624568", 2, 3)
        );
        return argumentsStream.map(arguments -> {
            Object[] args = arguments.get();
            return Arguments.arguments(pqt, args[0], args[1], args[2], deletionCostFunction);
        });
    }

    @ParameterizedTest
    @MethodSource("pNodeMappingTest")
    void pNodeMappingWithDeletionCostTest(Node root, String str, int treeDeletionLimit, int stringDeletionLimit,
                                          Function<GeneGroup, Double> deletionCostFunc)
            throws ExceptionInInitializerError {
        ArrayList<GeneGroup> string = geneGroupsProvider.convertToGeneGroups(str);
        NodeMappingAlgorithm algorithm =
                MappingAlgorithmBuilder.build(string, root, treeDeletionLimit, stringDeletionLimit,
                        substitutionFunction, deletionCostFunc);
        algorithm.runAlgorithm();
        HashMap<Integer, List<Mapping>> resultMappingsByEndPoints = algorithm.getResultMappingsByEndPoints();
        MappingAssertions.assertGenericMappingMapProperties(resultMappingsByEndPoints, string, deletionCostFunc,
                substitutionFunction);
    }

    private static Stream<Arguments> qNodeMappingTest() {
        Node pqt = PrepareInput.buildTreeFromParenRepresentation("[cog1 cog2 [cog3 cog4] cog5]");
        Function<GeneGroup, Double> deletionCostFunction =
                geneGroup -> {
                    if("cog6".equals(geneGroup.getCog()) | "cog1".equals(geneGroup.getCog()))
                        return -0.2;
                    else
                        return deletionCost.apply(geneGroup);};
        final Stream<Arguments> argumentsStream = Stream.of(
                Arguments.of("85342155348", 0, 0),
                Arguments.of("85342155348", 1, 0),
                Arguments.of("53642178", 0, 1),
                Arguments.of("53642178", 1, 1),
                Arguments.of("86234568", 1, 0),
                Arguments.of("86234568", 2, 2),
                Arguments.of("", 2, 1),
                Arguments.of("862463568", 1, 1),
                Arguments.of("862463568", 2, 3)
        );
        return argumentsStream.map(arguments -> {
            Object[] args = arguments.get();
            return Arguments.arguments(pqt, args[0], args[1], args[2], deletionCostFunction);
        });
    }

    @ParameterizedTest
    @MethodSource("qNodeMappingTest")
    void qNodeMappingWithDeletionCostTest(Node root, String str, int treeDeletionLimit, int stringDeletionLimit,
                                          Function<GeneGroup, Double> deletionCostFunc)
            throws ExceptionInInitializerError {
        ArrayList<GeneGroup> string = geneGroupsProvider.convertToGeneGroups(str);
        NodeMappingAlgorithm algorithm =
                MappingAlgorithmBuilder.build(string, root, treeDeletionLimit, stringDeletionLimit,
                        substitutionFunction, deletionCostFunc);
        HashMap<Integer, List<Mapping>> resultMappingsByEndPoints = algorithm.getResultMappingsByEndPoints();
        MappingAssertions.assertGenericMappingMapProperties(resultMappingsByEndPoints, string, deletionCostFunc,
                substitutionFunction);
    }
}
