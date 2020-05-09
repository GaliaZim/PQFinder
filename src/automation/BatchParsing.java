package automation;

import NodeMappingAlgorithms.MappingAlgorithmBuilder;
import NodeMappingAlgorithms.NodeMappingAlgorithm;
import helpers.PrepareInput;
import structures.GeneGroup;
import structures.Mapping;
import structures.Node;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BatchParsing {
    private static HashMap<String,Node> pqts = null;
    private static HashMap<String,String> pqtsParenthesisRepresentation = null;
    private static HashMap<String, ArrayList<GeneGroup>> genomesById = null;
    private static HashMap<String, Integer> genomesEndPointById;
    private static BiFunction<GeneGroup, GeneGroup, Double> substitutionFunction = Parsing::noSubstitutionsFunction;
    private static Function<Integer,Double> calcThreshold = integer -> 0.0;
    private static Function<Integer,Integer> calcTreeDeletionLimit = BatchParsing::defaultDeletionLimit;
    private static Function<Integer,Integer> calcStringDeletionLimit = BatchParsing::defaultDeletionLimit;
    private static BiFunction<NodeMappingAlgorithm, Double, List<Mapping>>
            outputMappingsCollector = BatchParsing::getBestMapping;
    private static String outputFolderPath = null;
    private static BiFunction<List<Mapping>,String,List<Mapping>> outputMappingsFilter = (l,s) -> l;

    public static void main(String[] args) {
        retrieveBatchInput(args);
        createOutputFolder();
        HashMap<String, List<Mapping>> pqtDerivationsByGenomeId;
        for(Map.Entry<String,Node> pqtEntry: pqts.entrySet()) {
            Node pqt = pqtEntry.getValue();
            final String pqtId = pqtEntry.getKey();
            pqtDerivationsByGenomeId = OnePqtToAllGenomesParsing(pqt);
            printPqtParsingResultsToFile(pqtDerivationsByGenomeId, pqtId, pqtsParenthesisRepresentation.get(pqtId));
        }
    }

    private static void createOutputFolder() {
        SimpleDateFormat dateSuffixFormat = new SimpleDateFormat("dd-MM-yyyy_HH.mm.ss");
        Date date = new Date();
        String outputFolderName = String.format("BatchJobResults_%s", dateSuffixFormat.format(date));
        File outputFolder = new File(outputFolderPath, outputFolderName);
        if(!outputFolder.mkdir())
            throw new RuntimeException(String.format("Could not create output folder: %s.", outputFolder.getPath()));
        outputFolderPath = outputFolder.getPath();
    }

    private static void printPqtParsingResultsToFile(HashMap<String, List<Mapping>> pqtDerivationsByGenomeId,
                                                     String pqtId, String pqtParen){
        int numberOfDerivedGenomes = pqtDerivationsByGenomeId.size();
        String outputFileName = String.format("g_%d_pqt_%s.txt", numberOfDerivedGenomes, pqtId);
        File outputFile = new File(outputFolderPath, outputFileName);
        try {
            if(!outputFile.createNewFile()) {
                    throw new RuntimeException("ERROR! Someone is writing to the output directory of this program. Stopping");
            }
            AppendToFile(pqtParen, outputFile);
            writeGenomesAndTheirMappings(pqtDerivationsByGenomeId, outputFile,
                    getSortedGenomeIdsAccordingToDerivations(pqtDerivationsByGenomeId));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void AppendToFile(String string, File outputFile) throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter(outputFile, true));
        printWriter.println(string);
        printWriter.close();
    }

    private static List<String> getSortedGenomeIdsAccordingToDerivations(HashMap<String, List<Mapping>>
                                                                                 derivationsByGenomeId) {
        List<String> ids = new ArrayList<>(derivationsByGenomeId.keySet());
        ids.sort(Comparator.comparingInt((String id) -> derivationsByGenomeId.get(id).size())
                .thenComparing(id -> derivationsByGenomeId.get(id).get(0)));
        return ids;
    }

    private static void writeGenomesAndTheirMappings(HashMap<String, List<Mapping>> genomeIdToMappings, File outputFile,
                                                     List<String> sortedMappedGenomes) throws IOException {
        PrintWriter printWriter = new PrintWriter(new FileWriter(outputFile, true));
        for(String genomeId: sortedMappedGenomes) {
            List<Mapping> possibleMappings = genomeIdToMappings.get(genomeId);
            printWriter.format(">%s\tfound:%d\n", genomeId, possibleMappings.size());
            for(Mapping mapping: possibleMappings) {
                printWriter.print(getGenomeMappingLine(genomeId, mapping));
            }
            printWriter.println();
            printWriter.flush();
        }
        printWriter.close();
    }

    private static String getGenomeMappingLine(String genomeId, Mapping mapping) {
        final String formattedOneToOneMapping;
        final int genomeEndPoint = genomesEndPointById.get(genomeId);
        final ArrayList<GeneGroup> genome = genomesById.get(genomeId);
        int endIndex = mapping.getEndIndex();
        if(endIndex > genomeEndPoint) {
            endIndex -= genomeEndPoint;
            formattedOneToOneMapping = Parsing.getFormattedOneToOneMapping(mapping, genome,
                    m -> m.getOneToOneMappingByLeafs(genomeEndPoint));
        } else {
            formattedOneToOneMapping = Parsing.getFormattedOneToOneMapping(mapping, genome,
                    Mapping::getOneToOneMappingByLeafs);
        }
        int startIndex = mapping.getStartIndex();
        final String indices = String.format("[%d:%d]", startIndex, endIndex);
        return String.format("%s\t%f\tdelS:%d\tdelT:%d\t%s\n", indices, mapping.getScore(),
                mapping.getStringDeletions(), mapping.getTreeDeletions(), formattedOneToOneMapping);
    }

    private static HashMap<String, List<Mapping>> OnePqtToAllGenomesParsing(Node pqt) {
        int len, treeDeletionLimit, stringDeletionLimit;
        double threshold;
        HashMap<String, List<Mapping>> pqtDerivationsByGenomeId;
        len = pqt.getSpan();
        treeDeletionLimit = calcTreeDeletionLimit.apply(len);
        stringDeletionLimit = calcStringDeletionLimit.apply(len);
        threshold = calcThreshold.apply(len);
        pqtDerivationsByGenomeId = new HashMap<>();
        for(Map.Entry<String, ArrayList<GeneGroup>> genomeEntry: genomesById.entrySet()) {
            OnePqtToGenomeParsing(pqt, treeDeletionLimit, stringDeletionLimit, threshold, pqtDerivationsByGenomeId,
                    genomeEntry.getKey(), genomeEntry.getValue());
        }
        return pqtDerivationsByGenomeId;
    }

    private static void OnePqtToGenomeParsing(Node pqt, int treeDeletionLimit, int stringDeletionLimit, double threshold,
                                              HashMap<String, List<Mapping>> pqtDerivationsByGenomeId,
                                              String genomeId, ArrayList<GeneGroup> genome) {
        NodeMappingAlgorithm algorithm;
        algorithm = MappingAlgorithmBuilder.build(genome, pqt, treeDeletionLimit, stringDeletionLimit,
                substitutionFunction);
        algorithm.runAlgorithm();
        List<Mapping> mappingList = outputMappingsFilter.apply(
                outputMappingsCollector.apply(algorithm, threshold), genomeId);
        if(!mappingList.isEmpty())
            pqtDerivationsByGenomeId.put(genomeId, mappingList);
    }

    private static List<Mapping> getAllMappings(NodeMappingAlgorithm algorithm, double threshold) {
        return algorithm.getAllPossibleMappings(threshold);
    }

    private static List<Mapping> getDistinctMappings(NodeMappingAlgorithm algorithm, Double threshold) {
        return algorithm.getBestPossibleMappingsForDistinctIndices(threshold);
    }

    private static List<Mapping> getBestMapping(NodeMappingAlgorithm algorithm, double threshold) {
        final Mapping bestMapping = algorithm.getBestMapping();
        if(bestMapping != null && bestMapping.getScore() >= threshold)
            return Collections.singletonList(bestMapping);
        else
            return Collections.emptyList();
    }

    private static List<Mapping> filterCyclicMappings(List<Mapping> mappingList, String genomeId) {
        final int genomeEndPoint = genomesEndPointById.get(genomeId);
        return mappingList.stream().filter(mapping -> {
            int startIndex = mapping.getStartIndex();
            int endIndex = mapping.getEndIndex();
            return startIndex < genomeEndPoint && endIndex-genomeEndPoint != startIndex;
        })
                .collect(Collectors.toList());
    }

    private static void retrieveBatchInput(String[] args) {
        Map<String, Consumer<String>> optionToArgumentRetrievalFunction = new HashMap<>(12);
        optionToArgumentRetrievalFunction.put("-p", BatchParsing::retrievePqts);
        optionToArgumentRetrievalFunction.put("-g", BatchParsing::retrieveGenomes);
        optionToArgumentRetrievalFunction.put("-gc", BatchParsing::retrieveCyclicGenomes);
        optionToArgumentRetrievalFunction.put("-m", BatchParsing::retrieveSubstitutionFunction);
        optionToArgumentRetrievalFunction.put("-dt", BatchParsing::retrieveTreeDeletionLimitFunction);
        optionToArgumentRetrievalFunction.put("-ds", BatchParsing::retrieveStringDeletionLimitFunction);
        optionToArgumentRetrievalFunction.put("-t", BatchParsing::retrieveThresholdFunction);
        optionToArgumentRetrievalFunction.put("-dtf", BatchParsing::retrieveTreeDeletionFactor);
        optionToArgumentRetrievalFunction.put("-dsf", BatchParsing::retrieveStringDeletionFactor);
        optionToArgumentRetrievalFunction.put("-tf", BatchParsing::retrieveThresholdFactor);
        optionToArgumentRetrievalFunction.put("-o", BatchParsing::retrieveOutputMappingCollector);
        optionToArgumentRetrievalFunction.put("-dest", BatchParsing::retrieveOutputFolder);

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
        if(pqts == null)
            Parsing.noArgumentErrorThrower("PQ-trees");
        if(genomesById == null)
            Parsing.noArgumentErrorThrower("genomes");
        if(outputFolderPath == null)
            Parsing.noArgumentErrorThrower("output folder");
    }

    private static void retrieveOutputFolder(String outputFolderPath) {
        BatchParsing.outputFolderPath = outputFolderPath;
    }

    private static void retrieveOutputMappingCollector(String outputOption) {
        switch (outputOption) {
            case "all":
                outputMappingsCollector = BatchParsing::getAllMappings;
                break;
            case "best":
                outputMappingsCollector = BatchParsing::getBestMapping;
                break;
            case "distinct":
                outputMappingsCollector = BatchParsing::getDistinctMappings;
                break;
            default:
                throw new RuntimeException(outputOption +
                        "is not a valid output option. Try 'best', 'all' or 'distinct'.");
        }
    }

    private static void retrieveSubstitutionFunction(String pathToSubstitutionMatrixFile) {
        String errorMsg = "Could not retrieve substitution matrix from file";
        try {
            substitutionFunction = PrepareInput.extractSubstitutionFunctionFromFile(pathToSubstitutionMatrixFile);
        } catch (IOException e) {
            throw new RuntimeException(errorMsg, e);
        }
    }

    private static void retrieveCyclicGenomes(String pathToGenomesFile) {
        retrieveGenomes(pathToGenomesFile);
        genomesById.values().forEach(list -> list.addAll(list.subList(0, Math.min(20, list.size()))));
        outputMappingsFilter = BatchParsing::filterCyclicMappings;
    }

    private static void retrieveGenomes(String pathToGenomes) {
        try {
            genomesById = PrepareInput.getGenomesFromFile(pathToGenomes);
            genomesEndPointById = new HashMap<>();
            for(Map.Entry<String, ArrayList<GeneGroup>> entry: genomesById.entrySet()) {
                genomesEndPointById.put(entry.getKey(), entry.getValue().size());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not retrieve genomes", e);
        }
    }

    private static void retrievePqts(String pathToPqtsFile) {
        pqts = new HashMap<>();
        pqtsParenthesisRepresentation = new HashMap<>();
        try {
            PrepareInput.getPqtsFromFile(pathToPqtsFile, pqts, pqtsParenthesisRepresentation);
        } catch (IOException e) {
            throw new RuntimeException("Could not retrieve PQ-trees", e);
        }
    }

    private static Integer defaultDeletionLimit(Integer integer) {
        return 0;
    }

    private static void retrieveStringDeletionFactor(String stringDeletionFactorString) {
        double stringDeletionFactor = Double.valueOf(stringDeletionFactorString);
        calcStringDeletionLimit = len -> (int) Math.ceil(len / stringDeletionFactor);
    }

    private static void retrieveTreeDeletionFactor(String treeDeletionFactorString) {
        double treeDeletionFactor = Double.valueOf(treeDeletionFactorString);
        calcTreeDeletionLimit = len -> (int) Math.ceil(len / treeDeletionFactor);
    }

    private static void retrieveStringDeletionLimitFunction(String stringDeletionLimitString) {
        int stringDeletionLimit = Integer.valueOf(stringDeletionLimitString);
        calcStringDeletionLimit = len -> stringDeletionLimit;
    }

    private static void retrieveTreeDeletionLimitFunction(String treeDeletionLimitString) {
        int treeDeletionLimit = Integer.valueOf(treeDeletionLimitString);
        calcTreeDeletionLimit = len -> treeDeletionLimit;
    }

    private static void retrieveThresholdFactor(String thresholdFactorString) {
        double thresholdFactor = Double.valueOf(thresholdFactorString);
        calcThreshold = len -> len * thresholdFactor;
    }

    private static void retrieveThresholdFunction(String thresholdString) {
        double threshold = Double.valueOf(thresholdString);
        calcThreshold = len -> threshold;
    }
}
