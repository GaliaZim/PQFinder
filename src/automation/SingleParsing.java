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

public class SingleParsing {
    private static Node pqt = null;
    private static ArrayList<GeneGroup> geneSeq = null;
    private static int treeDeletionLimit = 0;
    private static int stringDeletionLimit = 0;
    private static BiFunction<GeneGroup, GeneGroup, Double> substitutionFunction = Parsing::noSubstitutionsFunction;
    private static Consumer<NodeMappingAlgorithm> outputFunction = SingleParsing::printBestMapping;

    public static void main(String[] args) {
        retrieveInput(args);
        NodeMappingAlgorithm algorithm = MappingAlgorithmBuilder.build(geneSeq, pqt, treeDeletionLimit,
                stringDeletionLimit, substitutionFunction);
        algorithm.runAlgorithm();
        outputFunction.accept(algorithm);
    }

    private static void printMapping(Mapping mapping) {
        System.out.println("Derivation score: " + mapping.getScore());
        printDerivedSubstring(mapping);
        printOneToOneMapping(mapping);
        printDeletedStringIndices(mapping.getDeletedStringIndices());
        printDeletedLeafs(mapping.getDeletedDescendant());
        System.out.println();
    }

    private static void printDerivedSubstring(Mapping mapping) {
        int startPoint = mapping.getStartIndex();
        int endPoint = mapping.getEndIndex();
        ArrayList<GeneGroup> subGeneSeq = new ArrayList<>(geneSeq.subList(startPoint - 1, endPoint));
        String substring = subGeneSeq.stream().map(GeneGroup::toString)
                .reduce("", (s1,s2) ->  s1 + "," + s2).substring(1);
        System.out.println(String.format("The derived substring is S[%d:%d] = %s",
                startPoint, endPoint, substring));
    }

    private static void printDeletedLeafs(List<Node> deletedDescendant) {
        int deletedNodesNum = deletedDescendant.size();
        StringBuilder sb = new StringBuilder();
        if(deletedNodesNum > 0) {
            System.out.println(deletedNodesNum + " leaf(s) deleted in the derivation:");
            deletedDescendant.forEach(node -> sb.append(", ").append(node.getLabel()));
            System.out.println(sb.substring(2));
        } else {
            System.out.println("No leafs deleted in the derivation.");
        }
    }

    private static void printDeletedStringIndices(List<Integer> deletedStringIndices) {
        int deletedCharactersNum = deletedStringIndices.size();
        if(deletedCharactersNum > 0) {
            System.out.println(deletedCharactersNum + " gene(s) deleted in the derivation:");
            deletedStringIndices.forEach(i -> System.out.println(geneSeq.get(i-1) + " at index " + i));
        } else {
            System.out.println("No genes deleted in the derivation.");
        }
    }

    private static void printOneToOneMapping(Mapping mapping) {
        System.out.println("The one-to-one mapping:");
        System.out.println(Parsing.getFormattedOneToOneMapping(mapping, geneSeq));
    }

    private static void retrieveInput(String[] args) {
        Map<String,  Consumer<String>> optionToArgumentRetrievalFunction = new HashMap<>(8);
        optionToArgumentRetrievalFunction.put("-p", SingleParsing::retrieveTreeFromParenRepresantation);
        optionToArgumentRetrievalFunction.put("-j", SingleParsing::retrieveTreeFromJson);
        optionToArgumentRetrievalFunction.put("-gf", SingleParsing::retrieveGeneSeqFromFile);
        optionToArgumentRetrievalFunction.put("-g", SingleParsing::retrieveGeneSeqFromArgument);
        optionToArgumentRetrievalFunction.put("-m", SingleParsing::retrieveSubstitutionFunction);
        optionToArgumentRetrievalFunction.put("-dt", SingleParsing::retrieveTreeDeletionLimit);
        optionToArgumentRetrievalFunction.put("-ds", SingleParsing::retrieveStringDeletionLimit);
        optionToArgumentRetrievalFunction.put("-o", SingleParsing::retrieveOutputFunction);

        int argIndex = 0;
        while (argIndex < args.length - 1) {
           String option = args[argIndex];
           Consumer<String> func = optionToArgumentRetrievalFunction.get(option);
           if(func == null)
               Parsing.argumentErrorThrower(option);
           argIndex++;
           func.accept(args[argIndex]);
           argIndex++;
        }
        if(pqt == null)
            Parsing.noArgumentErrorThrower("PQ-tree");
        if(geneSeq == null)
            Parsing.noArgumentErrorThrower("gene sequence");
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

    private static void retrieveGeneSeqFromArgument(String argument) {
        String[] genes = argument.split(" ");
        ArrayList<GeneGroup> string = new ArrayList<>();
        for(String gene: genes)
            string.add(new GeneGroup(gene));
        geneSeq = string;
    }

    private static void retrieveTreeDeletionLimit(String treeDeletionLimit) {
        SingleParsing.treeDeletionLimit = Integer.valueOf(treeDeletionLimit);
    }

    private static void retrieveStringDeletionLimit(String stringDeletionLimit) {
        SingleParsing.stringDeletionLimit = Integer.valueOf(stringDeletionLimit);
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
                outputFunction = SingleParsing::printAllMappings;
                break;
            case "best":
                outputFunction = SingleParsing::printBestMapping;
                break;
            default:
                throw new RuntimeException(argument + "is not a valid outputFunction option. Try 'best' or 'all'.");
        }
    }

    private static void printBestMapping(NodeMappingAlgorithm algorithm) {
        SingleParsing.printMapping(algorithm.getBestMapping());
    }

    private static void printAllMappings(NodeMappingAlgorithm algorithm) {
        algorithm.getAllPossibleMappings().forEach(SingleParsing::printMapping);
    }

}