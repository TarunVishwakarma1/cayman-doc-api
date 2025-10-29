package com.newgen.cig.cayman.document.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.cig.cayman.document.exception.CryptoException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Base64;

public final class Decryption {

    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Decryption() {}

    /**
     * Decrypts an AES-GCM encrypted, Base64-encoded string.
     * Assumes the IV is prepended to the ciphertext.
     */
    public static String decryptAes(String base64CipherTextWithIv, SecretKey secretKey) {
        try {
            byte[] cipherTextWithIv = Base64.getDecoder().decode(base64CipherTextWithIv);

            // Extract IV and ciphertext
            ByteBuffer bb = ByteBuffer.wrap(cipherTextWithIv);
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            bb.get(iv);
            byte[] cipherText = new byte[bb.remaining()];
            bb.get(cipherText);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            byte[] plainTextBytes = cipher.doFinal(cipherText);

            return new String(plainTextBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CryptoException("Error decrypting with AES", e);
        }
    }

    /**
     * Decrypts an RSA-encrypted, Base64-encoded string using a Private Key.
     */
    public static String decryptRsa(String base64CipherText, PrivateKey privateKey) {
        try {
            byte[] cipherText = Base64.getDecoder().decode(base64CipherText);
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] plainTextBytes = cipher.doFinal(cipherText);
            return new String(plainTextBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CryptoException("Error decrypting with RSA", e);
        }
    }

    /**
     * Decrypts an AES-encrypted string back into a Java object.
     */
    public static <T> T decryptObjectAes(String base64CipherTextWithIv, SecretKey secretKey, Class<T> clazz) {
        try {
            String json = decryptAes(base64CipherTextWithIv, secretKey);
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new CryptoException("Error decrypting or deserializing object with AES", e);
        }
    }

    /**
     * Decrypts an RSA-encrypted string back into a Java object.
     */
    public static <T> T decryptObjectRsa(String base64CipherText, PrivateKey privateKey, Class<T> clazz) {
        try {
            String json = decryptRsa(base64CipherText, privateKey);
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new CryptoException("Error decrypting or deserializing object with RSA", e);
        }
    }
}