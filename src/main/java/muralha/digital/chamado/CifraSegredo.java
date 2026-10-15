package muralha.digital.chamado;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cifra de segredos guardados no banco (senha do SMTP dos chamados): AES/GCM do JDK,
 * chave de 32 bytes na variavel de ambiente GTW_CHAMADO_CHAVE_SMTP (Base64).
 * Formato: "v1:" + Base64(iv[12] || ciphertext+tag[16]).
 */
public final class CifraSegredo {

    public static final String VARIAVEL_CHAVE = "GTW_CHAMADO_CHAVE_SMTP";
    /** Limite do texto claro: o resultado precisa caber em varchar(255). */
    public static final int MAX_CARACTERES = 120;

    private static final String PREFIXO = "v1:";
    private static final String ALGORITMO = "AES/GCM/NoPadding";
    private static final int TAM_IV = 12;
    private static final int TAM_TAG_BITS = 128;
    private static final int TAM_CHAVE = 32;
    private static final int TAM_MAX_RESULTADO = 255;
    private static final SecureRandom RANDOM = new SecureRandom();

    private CifraSegredo() {
    }

    public static String cifrar(String texto, byte[] chave) {
        if (texto == null) throw new IllegalArgumentException("Texto nulo.");
        if (texto.length() > MAX_CARACTERES) {
            throw new IllegalArgumentException("O segredo deve ter no máximo " + MAX_CARACTERES + " caracteres.");
        }
        validarChave(chave);
        try {
            byte[] iv = new byte[TAM_IV];
            RANDOM.nextBytes(iv);
            Cipher c = Cipher.getInstance(ALGORITMO);
            c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(chave, "AES"), new GCMParameterSpec(TAM_TAG_BITS, iv));
            byte[] cifrado = c.doFinal(texto.getBytes(StandardCharsets.UTF_8));
            byte[] bruto = new byte[TAM_IV + cifrado.length];
            System.arraycopy(iv, 0, bruto, 0, TAM_IV);
            System.arraycopy(cifrado, 0, bruto, TAM_IV, cifrado.length);
            String resultado = PREFIXO + Base64.getEncoder().encodeToString(bruto);
            if (resultado.length() > TAM_MAX_RESULTADO) {
                throw new IllegalArgumentException("O segredo é longo demais para ser gravado.");
            }
            return resultado;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao cifrar o segredo.", e);
        }
    }

    /** Lanca IllegalArgumentException para prefixo desconhecido, valor adulterado ou chave errada. */
    public static String decifrar(String valor, byte[] chave) {
        if (valor == null || !valor.startsWith(PREFIXO)) {
            throw new IllegalArgumentException("Formato de segredo cifrado desconhecido.");
        }
        validarChave(chave);
        byte[] bruto;
        try {
            bruto = Base64.getDecoder().decode(valor.substring(PREFIXO.length()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Segredo cifrado inválido.");
        }
        if (bruto.length < TAM_IV + TAM_TAG_BITS / 8) {
            throw new IllegalArgumentException("Segredo cifrado inválido.");
        }
        try {
            Cipher c = Cipher.getInstance(ALGORITMO);
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(chave, "AES"),
                    new GCMParameterSpec(TAM_TAG_BITS, bruto, 0, TAM_IV));
            byte[] claro = c.doFinal(bruto, TAM_IV, bruto.length - TAM_IV);
            return new String(claro, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException("Não foi possível decifrar o segredo (valor adulterado ou chave diferente).");
        }
    }

    /** Chave da variavel de ambiente GTW_CHAMADO_CHAVE_SMTP, ou null se ausente/invalida. */
    public static byte[] chaveDoAmbiente() {
        return chaveDe(System.getenv(VARIAVEL_CHAVE));
    }

    /** Base64 de exatamente 32 bytes; qualquer outra coisa devolve null. */
    static byte[] chaveDe(String base64) {
        if (base64 == null || base64.trim().isEmpty()) return null;
        try {
            byte[] k = Base64.getDecoder().decode(base64.trim());
            return k.length == TAM_CHAVE ? k : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static void validarChave(byte[] chave) {
        if (chave == null || chave.length != TAM_CHAVE) {
            throw new IllegalArgumentException("Chave de cifragem inválida (são necessários 32 bytes).");
        }
    }
}
