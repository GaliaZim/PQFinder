package automation;

import structures.GeneGroup;
import structures.Mapping;
import structures.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;

public class Finder {
    public static void main(String[] args) {
        final int argNum = args.length;
        if(argNum == 0)
            throw new RuntimeException("No mode given. Try single");
        final String[] arguments = Arrays.copyOfRange(args, 1, argNum);
        final String mode = args[0];
        switch (mode) {
            case "single":
                SingleFinder.main(arguments);
                break;
            case "batch":
                BatchFinder.main(arguments);
                break;
            default:
                throw new RuntimeException(String.format("No such mode %s. Try single or batch", mode));
        }
    }

    static void noArgumentErrorThrower(String argument) {
        throw new RuntimeException("Did not receive an argument for the " + argument);
    }

    static void argumentErrorThrower(String option){
        throw new RuntimeException("unknown option: " + option);
    }

    static String getFormattedOneToOneMapping(Mapping mapping, ArrayList<GeneGroup> genome,
                                              Function<Mapping, HashMap<Node,Integer>> getLeafMappings) {
        HashMap<Node,Integer> leafMappings = getLeafMappings.apply(mapping);
        StringBuilder sb = new StringBuilder();
        for(Node leaf: mapping.getNode().getLeafs()) {
            Integer index = leafMappings.get(leaf);
            if(index == null)
                sb.append(String.format(" ; (%s,DEL)", leaf.getLabel()));
            else
                sb.append(String.format(" ; (%s,%s[%d])", leaf.getLabel(), genome.get(index - 1), index));
        }
        return sb.substring(3);
    }

    static Double noSubstitutionsFunction(GeneGroup g1, GeneGroup g2) {
        if(g1.getCog().equals(g2.getCog()))
            return 1.0;
        else
            return Double.NEGATIVE_INFINITY;
    }
}
