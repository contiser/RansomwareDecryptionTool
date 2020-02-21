import java.nio.file.Paths;

/**
 * Class to classify if the file could contain a natural language text or a random/generated one
 *
 * @author Sergio Conti Rossini
 * @version 1.0
 */
public class EntropyJudger {

    public static void main(String[] args) {
        FileEntropyCalculator.checkArguments(args);
        FileEntropyCalculator.calculateFileEntropy(Paths.get(args[0]));
        isLikelyToBeNaturalLang(FileEntropyCalculator.getFileEntropy());
    }

    /**
     * Method trying to classify if the text is likely to be machine/generated or in a human language
     * considering that often a human language has an high vowels entropy
     *
     * @param entropy the entropy of the file
     */
    private static void isLikelyToBeNaturalLang(double entropy) {
        if (entropy <= 5.5 || FileEntropyCalculator.getVowelsEntropy() >= 1) {
            System.out.println("The text could be a natural language text");
            System.out.println("Vowels entropy: " + FileEntropyCalculator.getVowelsEntropy());
        } else {
            System.out.println("The text seems to be machine-generated");
        }
    }
}
