package automation;

import java.util.Arrays;

public class Parsing {
    public static void main(String[] args) {
        final int argNum = args.length;
        if(argNum == 0)
            throw new RuntimeException("No mode given. Try single");
        final String[] arguments = Arrays.copyOfRange(args, 1, argNum);
        final String mode = args[0];
        switch (mode) {
            case "single":
                SingleParsing.main(arguments);
                break;
            default:
                throw new RuntimeException(String.format("No such mode %s. Try single", mode));
        }
    }
}
