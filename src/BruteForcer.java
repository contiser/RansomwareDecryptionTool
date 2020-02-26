import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Bruteforce tool for ITS Lab1
 *
 * @author Sergio Conti Rossini
 * @version 1.0
 */
public class BruteForcer {
    public static final String KALGORITHM = "AES";
    public static final String CALGORITHM = KALGORITHM + "/CBC/PKCS5Padding";
    private static String inFile;
    private static String outFile;

    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException {
        long startTime = System.nanoTime();
        BruteForcer.inFile = args[0];
        BruteForcer.outFile = args[1];
        ArrayList<byte[]> keys;
        keys = keyGen();
        for (byte[] rawKey : keys) {
            try {
                System.out.print("Attempt with : " + Arrays.toString(rawKey));
                attemptDecryption(rawKey);
                System.out.println("... Success!\n");
                FileEntropyCalculator.calculateFileEntropy(Paths.get("D:\\SWITCHdrive\\Documents\\ITS\\Lab 1\\itsec-secret-key-crypto\\mystery.decryptattempt"));
                if (EntropyJudger.isLikelyToBeNaturalLang(FileEntropyCalculator.getFileEntropy())) {
                    System.out.println("The decrypted file seems being in a natural language too!");
                    System.out.println("Process duration: " + (System.nanoTime() - startTime) / 1000000 + "ms");
                    System.exit(0);
                }
            } catch (Exception ignored) {
                System.out.print("... Failed\n");
            }
        }
    }

    /**
     * Method to generate some possible key starting from the written iv of the file
     *
     * @return List of key candidates for bruteforce
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws NoSuchPaddingException
     */
    static ArrayList<byte[]> keyGen() throws NoSuchAlgorithmException, IOException, NoSuchPaddingException {
        ArrayList<byte[]> keyList = new ArrayList<>();
        InputStream is = new FileInputStream(inFile);
        IvParameterSpec ivParameterSpec = readIv(is, Cipher.getInstance(CALGORITHM));
        keyList.add(ivParameterSpec.getIV());
        for (int d = 0; d <= 200; d++) {
            byte[] keyCandidate = keyList.get(0).clone();
            byte newByte = (byte) (keyCandidate[0] - d);
            keyCandidate[0] = newByte;
            keyList.add(keyCandidate);
        }
        for (int d = 0; d <= 200; d++) {
            byte[] keyCandidate = keyList.get(0).clone();
            keyCandidate[0] = (byte) (keyCandidate[0] + d);
            keyList.add(keyCandidate);
        }
        return keyList;
    }

    public static void attemptDecryption(byte[] rawKey) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        decrypt(rawKey);
        FileEntropyCalculator.calculateFileEntropy(Paths.get(outFile));
    }

    /**
     * Method took from original ransomware source code
     *
     * @param rawKey key with which the data will be decrypted
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     */

    public static void decrypt(byte[] rawKey) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeyException {
        SecretKey key = new SecretKeySpec(rawKey, 0, rawKey.length, KALGORITHM);
        Cipher cipher = Cipher.getInstance(CALGORITHM);

        try (InputStream is = new FileInputStream(inFile);
             OutputStream os = new FileOutputStream(outFile)) {
            IvParameterSpec ivParameterSpec = readIv(is, cipher);

            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
            crypt(is, os, cipher);
        }
    }

    /**
     * Method took from original ransomware source code
     *
     * @param is     inputStream for the encrypted file
     * @param cipher cipher instance used
     * @return returning the iv read from file
     * @throws IOException
     */

    public static IvParameterSpec readIv(InputStream is, Cipher cipher) throws IOException {
        byte[] rawIv = new byte[cipher.getBlockSize()];
        int inBytes = is.read(rawIv);

        if (inBytes != cipher.getBlockSize()) {
            throw new IOException("can't read IV from file");
        }

        return new IvParameterSpec(rawIv);
    }

    /**
     * Method took from original ransomware source code
     *
     * @param is     InputStream to plain text file to be encrypted
     * @param os     OutPutStream to the output file where the encrypted data will be saved to
     * @param cipher instance of used cipher
     * @throws IOException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static void crypt(InputStream is, OutputStream os, Cipher cipher) throws IOException, BadPaddingException, IllegalBlockSizeException {
        boolean more = true;
        byte[] input = new byte[cipher.getBlockSize()];

        while (more) {
            int inBytes = is.read(input);

            if (inBytes > 0) {
                os.write(cipher.update(input, 0, inBytes));
            } else {
                more = false;
            }
        }
        os.write(cipher.doFinal());
    }
}
