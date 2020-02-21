import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class to calculate the Entropy of a byte array
 *
 * @author Sergio Conti Rossini
 * @version 1.0
 */
public class EntropyCalculator {
    private Map<Byte, Double> bytesMap = new HashMap<>();
    private AtomicReference<Double> entropy = new AtomicReference<>(0.0);
    byte[] input;

    public EntropyCalculator(byte[] input) {
        this.input = input;
    }

    /**
     * Method to calculate the entropy
     *
     * @return Returns the entropy for the given bytes
     */
    public double calculateEntropy() {
        if (input.length > 0) {
            buildByteMap();
            calculateMapMembersEntropy();
            sumEntropies();
        }
        return entropy.get();
    }

    /**
     * Method to build an HashMap containing the value of the byte as key and the recurrence count
     */
    private void buildByteMap() {
        for (byte b : input) {
            if (!bytesMap.containsKey(b)) {
                bytesMap.put(b, 1.0);
            } else {
                bytesMap.replace(b, (bytesMap.get(b) + 1.0));
            }
        }
    }

    /**
     * Method to sum all the entropies calculated in the HashMap
     */
    private void sumEntropies() {
        bytesMap.forEach((k, v) -> entropy.updateAndGet(v1 -> v1 - v));
    }

    /**
     * Method to process all HashMap items and replace their occurrence count with the entropy
     */
    private void calculateMapMembersEntropy() {
        bytesMap.forEach((k, v) -> bytesMap.replace(k, (v / input.length) * log2((v / input.length))));
    }

    /**
     * Method to calculate the base 2 logarithm
     *
     * @param x the number of which the base 2 logarithm will be calculated
     * @return returns the result of the base 2 logarithm
     */
    private double log2(double x) {
        return Math.log10(x) / Math.log10(2);
    }

    double getVowelsEntropy() {
        double vowelsEntropy = 0.0;
        for (char c : new char[]{'a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U'}) {
            if (bytesMap.containsKey((byte) c)) {
                vowelsEntropy -= bytesMap.get((byte) c);
            }
        }
        return vowelsEntropy;
    }
}
