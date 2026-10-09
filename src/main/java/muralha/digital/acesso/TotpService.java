package muralha.digital.acesso;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

public final class TotpService {

    private static final String BASE32_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final int    PERIOD       = 30;
    private static final int    DIGITS       = 6;

    private TotpService() {}

    // ── Geração de chave ─────────────────────────────────────────────────────

    /** Gera uma chave Base32 de 20 bytes (160 bits) como seed TOTP. */
    public static String gerarSegredo() {
        byte[] bytes = new byte[20];
        new SecureRandom().nextBytes(bytes);
        return base32Encode(bytes);
    }

    // ── Validação ─────────────────────────────────────────────────────────────

    /** Aceita o período atual ±1 (tolerância de clock skew de 30s). */
    public static boolean validar(String segredoBase32, String codigoDigitado) {
        if (segredoBase32 == null || codigoDigitado == null) return false;
        long t = System.currentTimeMillis() / 1000L / PERIOD;
        for (long delta = -1; delta <= 1; delta++) {
            String esperado = calcularCodigo(segredoBase32, t + delta);
            if (esperado != null && esperado.equals(codigoDigitado.trim())) return true;
        }
        return false;
    }

    // ── URI para QR Code ─────────────────────────────────────────────────────

    public static String gerarOtpAuthUri(String segredo, String login, String issuer) {
        try {
            String enc = java.net.URLEncoder.encode(login, "UTF-8");
            String iss = java.net.URLEncoder.encode(issuer, "UTF-8");
            return "otpauth://totp/" + iss + ":" + enc +
                   "?secret=" + segredo + "&issuer=" + iss + "&algorithm=SHA1&digits=6&period=30";
        } catch (Exception e) { return ""; }
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    private static String calcularCodigo(String segredoBase32, long contagem) {
        try {
            byte[] key = base32Decode(segredoBase32);
            byte[] msg = ByteBuffer.allocate(8).putLong(contagem).array();
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(msg);
            int offset = hash[hash.length - 1] & 0x0F;
            int trunc = ((hash[offset]     & 0x7F) << 24)
                      | ((hash[offset + 1] & 0xFF) << 16)
                      | ((hash[offset + 2] & 0xFF) << 8)
                      | (hash[offset + 3]  & 0xFF);
            int code = trunc % (int) Math.pow(10, DIGITS);
            return String.format("%0" + DIGITS + "d", code);
        } catch (Exception e) { return null; }
    }

    // ── Base32 ────────────────────────────────────────────────────────────────

    public static String base32Encode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int buffer = 0, bitsLeft = 0;
        for (byte b : data) {
            buffer = (buffer << 8) | (b & 0xFF);
            bitsLeft += 8;
            while (bitsLeft >= 5) {
                bitsLeft -= 5;
                sb.append(BASE32_CHARS.charAt((buffer >> bitsLeft) & 0x1F));
            }
        }
        if (bitsLeft > 0) sb.append(BASE32_CHARS.charAt((buffer << (5 - bitsLeft)) & 0x1F));
        return sb.toString();
    }

    private static byte[] base32Decode(String s) {
        s = s.toUpperCase().replaceAll("[^A-Z2-7]", "");
        int outLen = s.length() * 5 / 8;
        byte[] out = new byte[outLen];
        int buffer = 0, bitsLeft = 0, idx = 0;
        for (char c : s.toCharArray()) {
            buffer = (buffer << 5) | BASE32_CHARS.indexOf(c);
            bitsLeft += 5;
            if (bitsLeft >= 8) { bitsLeft -= 8; out[idx++] = (byte)(buffer >> bitsLeft); }
        }
        return out;
    }
}
