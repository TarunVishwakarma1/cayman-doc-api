package com.newgen.cig.cayman.document.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.cig.cayman.document.exception.CryptoException;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

public final class Encryption {

    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final String AES_TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private Encryption() {}

    /**
     * Encrypts a string using AES-GCM with the given SecretKey.
     * The IV is randomly generated and prepended to the ciphertext.
     */
    public static String encryptAes(String plainText, SecretKey secretKey) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Prepend IV to ciphertext
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new CryptoException("Error encrypting with AES", e);
        }
    }

    /**
     * Encrypts a string using an RSA Public Key.
     */
    public static String encryptRsa(String plainText, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception e) {
            throw new CryptoException("Error encrypting with RSA", e);
        }
    }

    /**
     * Encrypts any Java object by first serializing it to JSON.
     */
    public static String encryptObjectAes(Object obj, SecretKey secretKey) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            return encryptAes(json, secretKey);
        } catch (Exception e) {
            throw new CryptoException("Error serializing or encrypting object with AES", e);
        }
    }

    /**
     * Encrypts any Java object by first serializing it to JSON.
     * Note: RSA has data size limits.
     */
    public static String encryptObjectRsa(Object obj, PublicKey publicKey) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            return encryptRsa(json, publicKey);
        } catch (Exception e) {
            throw new CryptoException("Error serializing or encrypting object with RSA", e);
        }
    }
}