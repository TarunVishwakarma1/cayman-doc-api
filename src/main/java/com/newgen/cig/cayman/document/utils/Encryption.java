package com.newgen.cig.cayman.document.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.cig.cayman.document.exception.CryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

public final class Encryption {

    private static final Logger logger = LoggerFactory.getLogger(Encryption.class);

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
        logger.trace("Entering encryptAes() method");
        logger.debug("Encrypting plain text with AES-GCM. Plain text length: {}", plainText != null ? plainText.length() : 0);
        try {
            logger.trace("Generating random IV for AES-GCM encryption");
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            new SecureRandom().nextBytes(iv);
            logger.debug("IV generated. Length: {} bytes", iv.length);

            logger.trace("Initializing AES cipher with GCM mode");
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            logger.debug("AES cipher initialized successfully");

            logger.trace("Performing AES encryption");
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            logger.debug("AES encryption completed. Cipher text length: {} bytes", cipherText.length);

            logger.trace("Prepending IV to ciphertext");
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);

            String base64Result = Base64.getEncoder().encodeToString(byteBuffer.array());
            logger.info("AES encryption completed successfully. Base64 result length: {}", base64Result.length());
            logger.trace("Exiting encryptAes() method with success");
            return base64Result;
        } catch (Exception e) {
            logger.error("Exception occurred while encrypting with AES: {}", e.getMessage(), e);
            throw new CryptoException("Error encrypting with AES", e);
        }
    }

    /**
     * Encrypts a string using an RSA Public Key.
     */
    public static String encryptRsa(String plainText, PublicKey publicKey) {
        logger.trace("Entering encryptRsa() method");
        logger.debug("Encrypting plain text with RSA. Plain text length: {}", plainText != null ? plainText.length() : 0);
        try {
            logger.trace("Initializing RSA cipher");
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            logger.debug("RSA cipher initialized successfully");

            logger.trace("Performing RSA encryption");
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            logger.debug("RSA encryption completed. Cipher text length: {} bytes", cipherText.length);

            String base64Result = Base64.getEncoder().encodeToString(cipherText);
            logger.info("RSA encryption completed successfully. Base64 result length: {}", base64Result.length());
            logger.trace("Exiting encryptRsa() method with success");
            return base64Result;
        } catch (Exception e) {
            logger.error("Exception occurred while encrypting with RSA: {}", e.getMessage(), e);
            throw new CryptoException("Error encrypting with RSA", e);
        }
    }

    /**
     * Encrypts any Java object by first serializing it to JSON.
     */
    public static String encryptObjectAes(Object obj, SecretKey secretKey) {
        logger.trace("Entering encryptObjectAes() method");
        logger.debug("Encrypting object with AES. Object type: {}", obj != null ? obj.getClass().getName() : "null");
        try {
            logger.trace("Serializing object to JSON");
            String json = objectMapper.writeValueAsString(obj);
            logger.debug("Object serialized to JSON. JSON length: {}", json != null ? json.length() : 0);
            String result = encryptAes(json, secretKey);
            logger.info("Object encrypted with AES successfully");
            logger.trace("Exiting encryptObjectAes() method with success");
            return result;
        } catch (Exception e) {
            logger.error("Exception occurred while serializing or encrypting object with AES: {}", e.getMessage(), e);
            throw new CryptoException("Error serializing or encrypting object with AES", e);
        }
    }

    /**
     * Encrypts any Java object by first serializing it to JSON.
     * Note: RSA has data size limits.
     */
    public static String encryptObjectRsa(Object obj, PublicKey publicKey) {
        logger.trace("Entering encryptObjectRsa() method");
        logger.debug("Encrypting object with RSA. Object type: {}", obj != null ? obj.getClass().getName() : "null");
        try {
            logger.trace("Serializing object to JSON");
            String json = objectMapper.writeValueAsString(obj);
            logger.debug("Object serialized to JSON. JSON length: {}", json != null ? json.length() : 0);
            logger.warn("RSA encryption has data size limits. JSON length: {}", json != null ? json.length() : 0);
            String result = encryptRsa(json, publicKey);
            logger.info("Object encrypted with RSA successfully");
            logger.trace("Exiting encryptObjectRsa() method with success");
            return result;
        } catch (Exception e) {
            logger.error("Exception occurred while serializing or encrypting object with RSA: {}", e.getMessage(), e);
            throw new CryptoException("Error serializing or encrypting object with RSA", e);
        }
    }
}