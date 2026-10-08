package com.consilux.infra;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESGCMUtil {
	private static final int GCM_IV_LENGTH = 16; // bytes
    private static final int GCM_TAG_LENGTH = 16; // bytes (128 bits)
    
  //Método para gerar IV aleatório - Importante ser aleatório por criptografia
    public static byte[] generateIV() {
    	byte[] iv = new byte[] {(byte)138, (byte)142, (byte)44, (byte)246, (byte)141, (byte)169, 
                (byte)128, (byte)241, (byte)7, (byte)81, (byte)199, (byte)35, 
                (byte)97, (byte)59, (byte)76, (byte)129};
        //new SecureRandom().nextBytes(iv);    	
        return iv;
    }
    
 //Método que realiza a criptografia
    public static String encrypt(String plaintext, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] cipherText = cipher.doFinal(plaintext.getBytes());

        byte[] cipherMessage = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, cipherMessage, 0, iv.length);
        System.arraycopy(cipherText, 0, cipherMessage, iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(cipherMessage);
    }

    //Método realiza a descriptografia
    public static String decrypt(String encryptedText, SecretKey key) throws Exception {
        byte[] cipherMessage = Base64.getDecoder().decode(encryptedText);

        byte[] iv = new byte[] {(byte)138, (byte)142, (byte)44, (byte)246, (byte)141, (byte)169, 
                (byte)128, (byte)241, (byte)7, (byte)81, (byte)199, (byte)35, 
                (byte)97, (byte)59, (byte)76, (byte)129};
        System.arraycopy(cipherMessage, 0, iv, 0, iv.length);

        byte[] cipherText = new byte[cipherMessage.length - GCM_IV_LENGTH];
        System.arraycopy(cipherMessage, GCM_IV_LENGTH, cipherText, 0, cipherText.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] plainText = cipher.doFinal(cipherText);

        return new String(plainText);
    }
    
    public static String decryptAESGCM(String headerCipherText, String headerNonce, String headerTag, String md5) throws Exception {
        byte[] cipherText = Base64.getDecoder().decode(headerCipherText);
        byte[] nonce = Base64.getDecoder().decode(headerNonce);
        byte[] tag = Base64.getDecoder().decode(headerTag);

        // Converte chave md5 para bytes
        byte[] keyBytes = hexStringToByteArray(md5);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        // AES-GCM espera tag embutida no final do cipherText
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(cipherText);
        outputStream.write(tag);
        byte[] cipherTextWithTag = outputStream.toByteArray();

        GCMParameterSpec gcmSpec = new GCMParameterSpec(16 * 8, nonce);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
        
        byte[] decrypted = cipher.doFinal(cipherTextWithTag);
        
        return new String(decrypted, "UTF-8");
    }
    
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
