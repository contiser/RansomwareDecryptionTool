import java.security.SecureRandom;

public class TotallySecureRandom extends SecureRandom {
    long now;
    @Override
    public void nextBytes(byte[] bytes) {

        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (now & 0xff);
            now >>= 8;
        }
    }

    public long getNow() {
        return now;
    }

    public void setNow(long now) {
        this.now = now;
    }
}
