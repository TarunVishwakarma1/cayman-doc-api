package com.newgen.cig.cayman.document.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.cig.cayman.document.exception.CryptoException;
import com.newgen.cig.cayman.document.model.enums.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Base64;

public final class Decryption {

    private static final Logger logger = LoggerFactory.getLogger(Decryption.class);

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
        logger.trace("Entering decryptAes() method");
        logger.debug("Decrypting AES-GCM encrypted data. Cipher text length: {}", 
                base64CipherTextWithIv != null ? base64CipherTextWithIv.length() : 0);
        
        if (base64CipherTextWithIv == null || base64CipherTextWithIv.trim().isEmpty()) {
            logger.error("Base64 cipher text with IV is null or empty");
            throw new CryptoException(ErrorCode.DECRYPTION_ERROR, "Base64 cipher text cannot be null or empty");
        }
        
        if (secretKey == null) {
            logger.error("Secret key is null");
            throw new CryptoException(ErrorCode.DECRYPTION_ERROR, "Secret key cannot be null");
        }
        
        try {
            logger.trace("Decoding base64 cipher text");
            byte[] cipherTextWithIv = Base64.getDecoder().decode(base64CipherTextWithIv);
            logger.debug("Base64 decoded. Total length: {} bytes", cipherTextWithIv.length);

           // িং Extract IV and ciphertext
            logger.trace("Extracting IV and ciphertext from byte buffer");
            ByteBuffer bb = ByteBuffer.wrap(cipherTextWithIv);
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            bb.get(iv);
            byte[] cipherText = new byte[bb.remaining()];
            bb.get(cipherText);
            logger.debug("IV extracted: {} bytes, Ciphertext length: {} bytes", iv.length, cipherText.length);

            logger.trace("Initializing AES cipher with GCM mode for decryption");
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            logger.debug("AES cipher initialized successfully for decryption");

            logger.trace("Performing AES decryption");
            byte[] plainTextBytes = cipher.doFinal(cipherText);
            logger.info("AES decryption completed successfully. Plain text length: {} bytes", plainTextBytes.length);
            
            String result = new String(plainTextBytes, StandardCharsets.UTF_8);
            logger.debug("Plain text converted to string. Length: {} characters", result.length());
            logger.trace("Exiting decryptAes() method with success");
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid base64 format for cipher text: {}", e.getMessage(), e);
            throw new CryptoException(ErrorCode.DECRYPTION_ERROR, "Invalid base64 format for cipher text", e);
        } catch (javax.crypto.AEADBadTagException e) {
            logger.error("Authentication failed during AES decryption: {}", e.getMessage(), e);
            throw new CryptoException(ErrorCode.DECRYPTION_ERROR, "Authentication failed - data may be corrupted or tampered", e);
        } catch (Exception e) {
            logger.error("Exception occurred while decrypting with AES: {}", e.getMessage(), e);
            throw new CryptoException(ErrorCode.DECRYPTION_ERROR, "Error decrypting with AES", e);
        }
    }

    /**
     * Decrypts an RSA-encrypted, Base64-encoded string using a Private Key.
     */
    public static String decryptRsa(String base64CipherText, PrivateKey privateKey) {
        logger.trace("Entering decryptRsa() method");
        logger.debug("Decrypting RSA encrypted data. Cipher text length: {}", 
                base64CipherText != null ? base64CipherText.length() : 0);
        
        if (base64CipherText == null || base64CipherText.trim().isEmpty()) {
            logger.error("Base64 cipher text is null or empty");
            throw new CryptoException(ErrorCode.DECRYPTION_ERROR, "Base64 cipher text cannot be null or empty");
        }
        
        if (privateKey == null) {
            logger.error("Private key is null");
            throw new CryptoException(ErrorCode.DECRYPTION_ERROR, "Private key cannot be null");
        }
        
        try {
            logger.trace("Decoding base64 cipher text");
            byte[] cipherText = Base64.getDecoder().decode(base64CipherText);
            logger.debug("Base64 decoded. Ciphertext length: {} bytes", cipherText.length);

            logger.trace("Initializing RSA cipher for decryption");
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            logger.debug("RSA cipher initialized successfully for decryption");

            logger.trace("Performing RSA decryption");
            byte[] plainTextBytes = cipher.doFinal(cipherText);
            logger.info("RSA decryption completed successfully. Plain text length: {} bytes", plainTextBytes.length);
            
            String result = new String(plainTextBytes, StandardCharsets.UTF_8);
            logger.debug("Plain text converted to string. Length: {} characters", result.length());
            logger.trace("Exiting decryptRsa() method with success");
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid base64 format for cipher text: {}", e.getMessage(), e);
            throw new CryptoException(ErrorCode.DECRYPTION_ERROR, "Invalid base64 format for cipher text", e);
        } catch (javax.crypto.BadPaddingException e) {
            logger.error("Bad padding during RSA decryption - key mismatch or corrupted data: {}", e.getMessage(), e);
            throw new CryptoException(ErrorCode.DECRYPTION_ERROR, "Bad padding - key mismatch or corrupted data", e);
        } catch (Exception e) {
            logger.error("Exception occurred while decrypting with RSA: {}", e.getMessage(), e);
            throw new CryptoException(ErrorCode.DECRYPTION_ERROR, "Error decrypting with RSA", e);
        }
    }

    /**
     * Decrypts an AES-encrypted string back into a Java object.
     */
    public static <T> T decryptObjectAes(String base64CipherTextWithIv, SecretKey secretKey, Class<T> clazz) {
        logger.trace("Entering decryptObjectAes() method. Target class: {}", clazz != null ? clazz.getName() : "null");
        logger.info("Decrypting and deserializing object with AES. Target class: {}", clazz != null ? clazz.getSimpleName() : "null");
        
        if (clazz == null) {
            logger.error("Target class is null");
            throw new CryptoException(ErrorCode.DECRYPTION_ERROR, "Target class cannot be null");
        }
        
        try {
            logger.debug("Decrypting AES encrypted JSON string");
            String json = decryptAes(base64CipherTextWithIv, secretKey);
            logger.debug("JSON string decrypted. Length: {} characters", json != null ? json.length() : 0);
            
            logger.trace("Deserializing JSON string to object of type: {}", clazz.getName());
            T result = objectMapper.readValue(json, clazz);
            logger.info("Object decrypted and deserialized successfully. Type: {}", clazz.getSimpleName());
            logger.trace("Exiting decryptObjectAes() method with success");
            return result;
        } catch (CryptoException e) {
            logger.error("CryptoException during AES object decryption: {}", e.getMessage(), e);
            throw e;
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            logger.error("JSON processing error during deserialization: {}", e.getMessage(), e);
            throw new CryptoException(ErrorCode.DECRYPTION_ERROR, "Error deserializing JSON to object", e);
        } catch (Exception e) {
            logger.error("Exception occurred while decrypting or deserializing object with AES: {}", e.getMessage(), e);
            throw new CryptoException(ErrorCode.DECRYPTION_ERROR, "Error decrypting or deserializing object with AES", e);
        }
    }

    /**
     * Decrypts an RSA-encrypted string back into a Java object.
     */
    public static <T> T decryptObjectRsa(String base64CipherText, PrivateKey privateKey, Class<T> clazz) {
        logger.trace("Entering decryptObjectRsa() method. Target class: {}", clazz != null ? clazz.getName() : "null");
        logger.info("Decrypting and deserializing object with RSA. Target class: {}", clazz != null ? clazz.getSimpleName() : "null");
        
        if (clazz == null) {
            logger.error("Target class is null");
            throw new CryptoException(ErrorCode.DECRYPTION_ERROR, "Target class cannot be null");
        }
        
        try {
            logger.debug("Decrypting RSA encrypted JSON string");
            String json = decryptRsa(base64CipherText, privateKey);
            logger.debug("JSON string decrypted. Length: {} characters", json != null ? json.length() : 0);
            
            logger.trace("Deserializing JSON string to object of type: {}", clazz.getName());
            T result = objectMapper.readValue(json, clazz);
            logger.info("Object decrypted and deserialized successfully. Type: {}", clazz.getSimpleName());
            logger.trace("Exiting decryptObjectRsa() method with success");
            return result;
        } catch (CryptoException e) {
            logger.error("CryptoException during RSA object decryption: {}", e.getMessage(), e);
            throw e;
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            logger.error("JSON processing error during deserialization: {}", e.getMessage(), e);
            throw new CryptoException(ErrorCode.DECRYPTION_ERROR, "Error deserializing JSON to object", e);
        } catch (Exception e) {
            logger.error("Exception occurred while decrypting or deserializing object with RSA: {}", e.getMessage(), e);
            throw new CryptoException(ErrorCode.DECRYPTION_ERROR, "Error decrypting or deserializing object with RSA", e);
        }
    }
}
