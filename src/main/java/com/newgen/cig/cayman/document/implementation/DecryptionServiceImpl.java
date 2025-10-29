package com.newgen.cig.cayman.document.implementation;

import com.newgen.cig.cayman.document.exception.CryptoException;
import com.newgen.cig.cayman.document.interfaces.DecryptionService;
import com.newgen.cig.cayman.document.model.dto.RsaDecryptionRequest;
import com.newgen.cig.cayman.document.model.enums.ErrorCode;
import com.newgen.cig.cayman.document.utils.Decryption;
import com.newgen.cig.cayman.document.utils.KeyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Arrays;

/**
 * Default implementation of {@link DecryptionService} backed by
 * AES-GCM for symmetric decryption and RSA for asymmetric decryption.
 *
 * <p>The global AES key is derived from the property
 * {@code my.security.aes-secret} using SHA-256 to obtain a 256-bit key.</p>
 *
 * <h3>Responsibilities:</h3>
 * <ul>
 *   <li>Decrypt cipher text with a global AES key</li>
 *   <li>Decrypt cipher text with a provided RSA private key</li>
 *   <li>Decrypt arbitrary objects serialized as JSON</li>
 * </ul>
 *
 * @author Tarun Vishwakarma
 * @since 2025
 */
@Service
public class DecryptionServiceImpl implements DecryptionService {

    private static final Logger logger = LoggerFactory.getLogger(DecryptionServiceImpl.class);
    
    private final SecretKey globalAesKey;

    public DecryptionServiceImpl(@Value("${my.security.aes-secret}") String aesSecret) {
        logger.trace("Initializing DecryptionServiceImpl");
        logger.info("Creating global AES key from provided secret");
        try {
            this.globalAesKey = createAesKey(aesSecret);
            logger.info("DecryptionServiceImpl initialized successfully with AES key");
            logger.debug("AES key algorithm: {}, Format: {}", 
                    globalAesKey.getAlgorithm(), globalAesKey.getFormat());
        } catch (Exception e) {
            logger.error("Exception occurred while initializing DecryptionServiceImpl: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String decryptGlobalAes(String cipherText) {
        logger.trace("Entering decryptGlobalAes() method");
        logger.info("Decrypting text using global AES key");
        try {
            logger.debug("Cipher text length: {}", cipherText != null ? cipherText.length() : 0);
            String plainText = Decryption.decryptAes(cipherText, globalAesKey);
            logger.info("Text decrypted successfully with AES");
            logger.debug("Plain text length: {}", plainText != null ? plainText.length() : 0);
            logger.trace("Exiting decryptGlobalAes() method with success");
            return plainText;
        } catch (Exception e) {
            logger.error("Exception occurred while decrypting with global AES: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String decryptWithRsa(RsaDecryptionRequest request) {
        logger.trace("Entering decryptWithRsa() method");
        logger.info("Decrypting text using RSA private key");
        try {
            logger.debug("Cipher text length: {}, Private key provided: {}", 
                    request.cipherText() != null ? request.cipherText().length() : 0,
                    request.base64PrivateKey() != null);
            logger.trace("Loading RSA private key from base64 string");
            PrivateKey privateKey = KeyPair.loadRsaPrivateKey(request.base64PrivateKey());
            logger.debug("RSA private key loaded successfully");
            String plainText = Decryption.decryptRsa(request.cipherText(), privateKey);
            logger.info("Text decrypted successfully with RSA");
            logger.debug("Plain text length: {}", plainText != null ? plainText.length() : 0);
            logger.trace("Exiting decryptWithRsa() method with success");
            return plainText;
        } catch (Exception e) {
            logger.error("Exception occurred while decrypting with RSA: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public <T> T decryptObjectGlobalAes(String cipherText, Class<T> clazz) {
        logger.trace("Entering decryptObjectGlobalAes() method");
        logger.info("Decrypting object using global AES key. Target class: {}", clazz != null ? clazz.getName() : "null");
        try {
            logger.debug("Cipher text length: {}", cipherText != null ? cipherText.length() : 0);
            T decryptedObject = Decryption.decryptObjectAes(cipherText, globalAesKey, clazz);
            logger.info("Object decrypted successfully with AES");
            logger.debug("Decrypted object type: {}", decryptedObject != null ? decryptedObject.getClass().getName() : "null");
            logger.trace("Exiting decryptObjectGlobalAes() method with success");
            return decryptedObject;
        } catch (Exception e) {
            logger.error("Exception occurred while decrypting object with global AES: {}", e.getMessage(), e);
            throw e;
        }
    }

    /** Creates a 256-bit AES key from a string secret using SHA-256. */
    private SecretKey createAesKey(String secret) {
        logger.trace("Entering createAesKey() method");
        logger.debug("Creating AES key from secret. Secret length: {}", secret != null ? secret.length() : 0);
        try {
            logger.trace("Getting SHA-256 MessageDigest instance");
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            logger.trace("Digesting secret to generate key bytes");
            byte[] keyBytes = sha.digest(secret.getBytes(StandardCharsets.UTF_8));
            keyBytes = Arrays.copyOf(keyBytes, 32); // Use 32 bytes (256 bits)
            logger.debug("AES key bytes generated. Key length: {} bytes (256 bits)", keyBytes.length);
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            logger.info("AES key created successfully");
            logger.trace("Exiting createAesKey() method with success");
            return key;
        } catch (NoSuchAlgorithmException e) {
            logger.error("Exception occurred while creating AES key. Algorithm not found: SHA-256", e);
            throw new CryptoException(ErrorCode.KEY_GENERATION_ERROR, "Error creating AES key - SHA-256 algorithm not found", e);
        }
    }
}