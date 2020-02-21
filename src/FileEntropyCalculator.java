import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class to calculate the entropy of a given file, using the EntropyCalculator class to make the calculation
 *
 * @author Sergio Conti Rossini
 * @version 1.0
 */
public class FileEntropyCalculator {
    private static double fileEntropy = 0.0;
    private static double vowelsEntropy = 0.0;

    public static void main(String[] args) {
        checkArguments(args);
        calculateFileEntropy(Paths.get(args[0]));
    }

    /**
     * Method to read the bytes from the file and give them to the EntropyCalculator
     *
     * @param path The path of the file to process
     */
    static void calculateFileEntropy(Path path) {
        try {
            byte[] fileBytes = Files.readAllBytes(path);
            EntropyCalculator ec = new EntropyCalculator(fileBytes);
            fileEntropy = ec.calculateEntropy();
            vowelsEntropy = ec.getVowelsEntropy();
            System.out.println("The entropy of the provided file is: " + fileEntropy);
        } catch (
                IOException e) {
            System.out.println("The file provided seems not to be existing or accessible... " + path.toString());
        }
    }

    static void checkArguments(String[] args) {
        if (args.length != 1) {
            System.out.println("Please provide exactly 1 file path as argument!");
            System.exit(-1);
        }
    }

    public static double getFileEntropy() {
        return fileEntropy;
    }

    public static double getVowelsEntropy() {
        return vowelsEntropy;
    }
}
