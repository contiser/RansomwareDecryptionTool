import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class BruteForcer {
    public static final String KALGORITHM = "AES";
    public static final String CALGORITHM = KALGORITHM + "/CBC/PKCS5Padding";
    private static String inFile;
    private static String outFile;

    public static void main(String[] args) {
        BruteForcer.inFile = args[0];
        BruteForcer.outFile = args[1];
        try {
            byte[] rawKey = keyGen().getEncoded();
            System.out.println("Attempt with : " + Arrays.toString(rawKey));
            System.out.println(Arrays.toString(rawKey) + attemptDecryption(rawKey));
        } catch (Exception ignored) {
        }
    }

    static SecretKey keyGen() throws NoSuchAlgorithmException, IOException, NoSuchPaddingException {
        KeyGenerator keyGen = KeyGenerator.getInstance(KALGORITHM);
        try (InputStream is = new FileInputStream(inFile)) {
            IvParameterSpec ivParameterSpec = readIv(is, Cipher.getInstance(CALGORITHM));
            ivParameterSpec.getIV();
            keyGen.init(128, new TotallySecureRandom());
        }
        return keyGen.generateKey();
    }

    public static boolean attemptDecryption(byte[] rawKey) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        decrypt(rawKey);
        FileEntropyCalculator.calculateFileEntropy(Paths.get(outFile));
        return EntropyJudger.isLikelyToBeNaturalLang(FileEntropyCalculator.getFileEntropy());
    }

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

    public static IvParameterSpec readIv(InputStream is, Cipher cipher) throws IOException {
        byte[] rawIv = new byte[cipher.getBlockSize()];
        int inBytes = is.read(rawIv);

        if (inBytes != cipher.getBlockSize()) {
            throw new IOException("can't read IV from file");
        }

        return new IvParameterSpec(rawIv);
    }

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
