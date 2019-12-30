package tests;

import NodeMappingAlgorithms.InternalNodeMappingAlgorithm;
import NodeMappingAlgorithms.MappingAlgorithmBuilder;
import NodeMappingAlgorithms.PNodeMappingAlgorithm;
import NodeMappingAlgorithms.QNodeMappingAlgorithm;
import helpers.PrepareInput;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import structures.Mapping;
import structures.Node;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

class InternalNodeMappingTest {
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

    private static Stream<Arguments> pNodeMappingOneTierTestProvider() {
        String treeJsonPath = ".\\src\\tests\\treeJSONS\\pNodeOneTier";
        Node pNode;
        try {
            pNode = PrepareInput.buildTree(treeJsonPath);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
        return Stream.of(
                Arguments.of(new PNodeMappingAlgorithm("12342314", pNode,
                        0, 0, substitutionFunction)),
                Arguments.of(new PNodeMappingAlgorithm("124231452", pNode,
                        2, 3, substitutionFunction)),
                Arguments.of(new PNodeMappingAlgorithm("132", pNode,
                        2, 1, substitutionFunction)),
                Arguments.of(new PNodeMappingAlgorithm("", pNode,
                        2, 1, substitutionFunction)),
                Arguments.of(new PNodeMappingAlgorithm("124231452", pNode,
                        0, 1, substitutionFunction)),
                Arguments.of(new PNodeMappingAlgorithm("124231452", pNode,
                        2, 0, substitutionFunction))
        );
    }

    private static Stream<Arguments> qNodeMappingOneTierTestProvider() {
        String treeJsonPath = ".\\src\\tests\\treeJSONS\\qNodeOneTier";
        Node qNode;
        try {
            qNode = PrepareInput.buildTree(treeJsonPath);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
        return Stream.of(
                Arguments.of(new QNodeMappingAlgorithm("12342314", qNode,
                        0, 0, substitutionFunction)),
                Arguments.of(new QNodeMappingAlgorithm("124231452", qNode,
                        2, 3, substitutionFunction)),
                Arguments.of(new QNodeMappingAlgorithm("321", qNode,
                        2, 1, substitutionFunction)),
                Arguments.of(new QNodeMappingAlgorithm("", qNode,
                        2, 1, substitutionFunction)),
                Arguments.of(new QNodeMappingAlgorithm("124231452", qNode,
                        0, 1, substitutionFunction)),
                Arguments.of(new QNodeMappingAlgorithm("124231452", qNode,
                        2, 0, substitutionFunction))
        );
    }

    private static Stream<Arguments> severalTiersTreeTestProvider() {
        String treeJsonPath = ".\\src\\tests\\treeJSONS\\mixedTypeTreeHeight4";
        Node root;
        try {
            root = PrepareInput.buildTree(treeJsonPath);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
        return Stream.of(
                Arguments.of(MappingAlgorithmBuilder.build("5741132534623", root,
                        0, 0, substitutionFunction)),
                Arguments.of(MappingAlgorithmBuilder.build("57413722534623", root,
                        2, 3, substitutionFunction)),
                Arguments.of(MappingAlgorithmBuilder.build("3216514", root,
                        2, 1, substitutionFunction)),
                Arguments.of(MappingAlgorithmBuilder.build("", root,
                        2, 1, substitutionFunction)),
                Arguments.of(MappingAlgorithmBuilder.build("124231452", root,
                        0, 1, substitutionFunction)),
                Arguments.of(MappingAlgorithmBuilder.build("321654314", root,
                        2, 0, substitutionFunction))
        );
    }

    @ParameterizedTest
    @MethodSource({"pNodeMappingOneTierTestProvider", "qNodeMappingOneTierTestProvider",
            "severalTiersTreeTestProvider"})
    void internalNodeMappingTest(InternalNodeMappingAlgorithm algorithm) throws ExceptionInInitializerError {
        algorithm.runAlgorithm();
        HashMap<Integer, List<Mapping>> resultMappingsByEndPoints = algorithm.getResultMappingsByEndPoints();
        MappingAssertions.assertGenericMappingMapProperties(resultMappingsByEndPoints);
    }
}
