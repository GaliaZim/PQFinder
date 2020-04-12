package automation;

import NodeMappingAlgorithms.MappingAlgorithmBuilder;
import NodeMappingAlgorithms.NodeMappingAlgorithm;
import helpers.PrepareInput;
import org.json.simple.parser.ParseException;
import structures.GeneGroup;
import structures.Mapping;
import structures.Node;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Parsing {
    private static Node pqt = null;
    private static ArrayList<GeneGroup> geneSeq = null;
    private static int treeDeletionLimit = 0;
    private static int stringDeletionLimit = 0;
    private static BiFunction<GeneGroup, GeneGroup, Double> substitutionFunction
            = Parsing::noSubstitutionsFunction;
    private static Consumer<NodeMappingAlgorithm> outputFunction = Parsing::printBestMapping;

    public static void main(String[] args) {
        retrieveInput(args);
        NodeMappingAlgorithm algorithm = MappingAlgorithmBuilder.build(geneSeq, pqt, treeDeletionLimit,
                stringDeletionLimit, substitutionFunction);
        algorithm.runAlgorithm();
        outputFunction.accept(algorithm);
    }

    private static void printMapping(Mapping mapping) {
        System.out.println("Derivation score: " + mapping.getScore());

        int startPoint = mapping.getStartIndex();
        int endPoint = mapping.getEndIndex();
        ArrayList<GeneGroup> subGeneSeq = new ArrayList<>(geneSeq.subList(startPoint - 1, endPoint));
        String substring = subGeneSeq.stream().map(GeneGroup::toString)
                .reduce("", (s1,s2) ->  s1 + ", " + s2).substring(2);
        System.out.println(String.format("The derived substring is S[%d:%d]= %s",
                startPoint, endPoint, substring));

        System.out.println("The one-to-one mapping:");
        mapping.getLeafMappings().entrySet().stream().map(entry ->
                String.format("(%s,%s[%d])", entry.getValue().getLabel().toString(), geneSeq.get(entry.getKey()-1),
                        entry.getKey()))
                .forEach(map -> System.out.print(map + " ; "));
        System.out.println();

        List<Node> deletedDescendant = mapping.getDeletedDescendant();
        int deletedNodesNum = deletedDescendant.size();
        if(deletedNodesNum > 0) {
            System.out.println(deletedNodesNum + " nodes deleted in the derivation:");
            deletedDescendant.forEach(node -> System.out.print(node.getLabel() + ", "));
            System.out.println();
        } else {
            System.out.println("No nodes deleted in the derivation.");
        }

        List<Integer> deletedStringIndices = mapping.getDeletedStringIndices();
        int deletedCharactersNum = deletedStringIndices.size();
        if(deletedCharactersNum > 0) {
            System.out.println(deletedCharactersNum + " characters deleted from the substring:");
            deletedStringIndices.forEach(i -> System.out.println(geneSeq.get(i-1) + "at index " + i));
        } else {
            System.out.println("No characters deleted from the substring.");
        }
        System.out.println();
    }

    private static void retrieveInput(String[] args) {
        Map<String,  Consumer<String>> optionToArgumentRetrievalFunction = new HashMap<>(6);
        optionToArgumentRetrievalFunction.put("-p", Parsing::retrieveTreeFromParenRepresantation);
        optionToArgumentRetrievalFunction.put("-j", Parsing::retrieveTreeFromJson);
        optionToArgumentRetrievalFunction.put("-g", Parsing::retrieveGeneSeqFromFile);
        optionToArgumentRetrievalFunction.put("-m", Parsing::retrieveSubstitutionFunction);
        optionToArgumentRetrievalFunction.put("-dt", Parsing::retrieveTreeDeletionLimit);
        optionToArgumentRetrievalFunction.put("-ds", Parsing::retrieveStringDeletionLimit);
        optionToArgumentRetrievalFunction.put("-o", Parsing::retrieveOutputFunction);

        int argIndex = 0;
        while (argIndex < args.length - 1) {
           String option = args[argIndex];
           Consumer<String> func = optionToArgumentRetrievalFunction.get(option);
           if(func == null)
               argumentErrorThrower(option);
           argIndex++;
           func.accept(args[argIndex]);
           argIndex++;
        }
        if(pqt == null)
            noArgumentErrorThrower("PQ-tree");
        if(geneSeq == null)
            noArgumentErrorThrower("gene sequence");
    }

    private static void noArgumentErrorThrower(String argument) {
        throw new RuntimeException("Did not receive an argument for the " + argument);
    }

    private static void argumentErrorThrower(String option){
        throw new RuntimeException("unknown option: " + option);
    }

    private static void retrieveTreeFromJson(String pathToJsonFile) {
        String errorMsg = "Could not retrieve PQ-tree from JSON file";
        try {
            pqt = PrepareInput.buildTreeFromJSON(pathToJsonFile);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(errorMsg, e);
        }
    }

    private static void retrieveTreeFromParenRepresantation(String parenthesisRepr) {
        try {
            pqt = PrepareInput.buildTreeFromParenRepresentation(parenthesisRepr);
        } catch (Exception e) {
            throw new RuntimeException("Could not retrieve PQ-tree from parenthesis representation");
        }
    }

    private static void retrieveGeneSeqFromFile(String pathToGeneSeqFile) {
        String errorMsg = "Could not retrieve gene sequence from JSON file";
        try {
            geneSeq = PrepareInput.retrieveInputStringFromFile(pathToGeneSeqFile);
        } catch (IOException | ParseException e) {
            throw new RuntimeException(errorMsg, e);
        }
    }

    private static void retrieveTreeDeletionLimit(String treeDeletionLimit) {
        Parsing.treeDeletionLimit = Integer.valueOf(treeDeletionLimit);
    }

    private static void retrieveStringDeletionLimit(String stringDeletionLimit) {
        Parsing.stringDeletionLimit = Integer.valueOf(stringDeletionLimit);
    }

    private static void retrieveSubstitutionFunction(String pathToSubstitutionMatrixFile) {
        String errorMsg = "Could not retrieve substitution matrix from file";
        try {
            substitutionFunction = PrepareInput.extractSubstitutionFunctionFromFile(pathToSubstitutionMatrixFile);
        } catch (IOException e) {
            throw new RuntimeException(errorMsg, e);
        }
    }

    private static void retrieveOutputFunction(String argument) {
        switch (argument) {
            case "all":
                outputFunction = Parsing::printAllMappings;
                break;
            case "best":
                outputFunction = Parsing::printBestMapping;
                break;
            default:
                throw new RuntimeException(argument + "is not a valid outputFunction option. Try 'best' or 'all'.");
        }
    }

    private static void printBestMapping(NodeMappingAlgorithm algorithm) {
        Parsing.printMapping(algorithm.getBestMapping());
    }

    private static void printAllMappings(NodeMappingAlgorithm algorithm) {
        algorithm.getAllPossibleMappings().forEach(Parsing::printMapping);
    }

    private static Double noSubstitutionsFunction(GeneGroup g1, GeneGroup g2) {
        if(g1.getCog().equals(g2.getCog()))
            return 1.0;
        else
            return Double.NEGATIVE_INFINITY;
    }
}