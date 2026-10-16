package muralha.digital.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashUtil {

    private HashUtil() {}

    public static String sha256Hex(byte[] data) {
        return hex(data, "SHA-256");
    }

    /**
     * Hash em hexadecimal minúsculo com o algoritmo informado
     * ("SHA-256", "SHA-384" ou "SHA-512").
     */
    public static String hex(byte[] data, String algoritmo) {
        try {
            MessageDigest md = MessageDigest.getInstance(algoritmo);
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(algoritmo + " indisponivel", e);
        }
    }
}
