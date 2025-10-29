package com.newgen.cig.cayman.document.implementation;

import com.newgen.cig.cayman.document.exception.CryptoException;
import com.newgen.cig.cayman.document.interfaces.EncryptionService;
import com.newgen.cig.cayman.document.model.dto.RsaEncryptionRequest;
import com.newgen.cig.cayman.document.model.enums.ErrorCode;
import com.newgen.cig.cayman.document.utils.Encryption;
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
import java.security.PublicKey;
import java.util.Arrays;

/**
 * Default implementation of {@link EncryptionService} backed by
 * AES-GCM for symmetric encryption and RSA for asymmetric encryption.
 *
 * <p>The global AES key is derived from the property
 * {@code my.security.aes-secret} using SHA-256 to obtain a 256-bit key.</p>
 *
 * <h3>Responsibilities:</h3>
 * <ul>
 *   <li>Encrypt plain text with a global AES key</li>
 *   <li>Encrypt plain text with a provided RSA public key</li>
 *   <li>Encrypt arbitrary objects by serializing to JSON first</li>
 * </ul>
 *
 * @author Tarun Vishwakarma
 * @since 2025
 */
@Service
public class EncryptionServiceImpl implements EncryptionService {

    private static final Logger logger = LoggerFactory.getLogger(EncryptionServiceImpl.class);
    
    private final SecretKey globalAesKey;

    public EncryptionServiceImpl(@Value("${my.security.aes-secret}") String aesSecret) {
        logger.trace("Initializing EncryptionServiceImpl");
        logger.info("Creating global AES key from provided secret");
        try {
            this.globalAesKey = createAesKey(aesSecret);
            logger.info("EncryptionServiceImpl initialized successfully with AES key");
            logger.debug("AES key algorithm: {}, Format: {}", 
                    globalAesKey.getAlgorithm(), globalAesKey.getFormat());
        } catch (Exception e) {
            logger.error("Exception occurred while initializing EncryptionServiceImpl: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String encryptGlobalAes(String plainText) {
        logger.trace("Entering encryptGlobalAes() method");
        logger.info("Encrypting text using global AES key");
        try {
            logger.debug("Plain text length: {}", plainText != null ? plainText.length() : 0);
            String cipherText = Encryption.encryptAes(plainText, globalAesKey);
            logger.info("Text encrypted successfully with AES");
            logger.debug("Cipher text length: {}", cipherText != null ? cipherText.length() : 0);
            logger.trace("Exiting encryptGlobalAes() method with success");
            return cipherText;
        } catch (Exception e) {
            logger.error("Exception occurred while encrypting with global AES: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String encryptWithRsa(RsaEncryptionRequest request) {
        logger.trace("Entering encryptWithRsa() method");
        logger.info("Encrypting text using RSA public key");
        try {
            logger.debug("Plain text length: {}, Public key provided: {}", 
                    request.plainText() != null ? request.plainText().length() : 0,
                    request.base64PublicKey() != null);
            logger.trace("Loading RSA public key from base64 string");
            PublicKey publicKey = KeyPair.loadRsaPublicKey(request.base64PublicKey());
            logger.debug("RSA public key loaded successfully");
            String cipherText = Encryption.encryptRsa(request.plainText(), publicKey);
            logger.info("Text encrypted successfully with RSA");
            logger.debug("Cipher text length: {}", cipherText != null ? cipherText.length() : 0);
            logger.trace("Exiting encryptWithRsa() method with success");
            return cipherText;
        } catch (Exception e) {
            logger.error("Exception occurred while encrypting with RSA: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String encryptObjectGlobalAes(Object obj) {
        logger.trace("Entering encryptObjectGlobalAes() method");
        logger.info("Encrypting object using global AES key");
        try {
            logger.debug("Object type: {}", obj != null ? obj.getClass().getName() : "null");
            String cipherText = Encryption.encryptObjectAes(obj, globalAesKey);
            logger.info("Object encrypted successfully with AES");
            logger.debug("Cipher text length: {}", cipherText != null ? cipherText.length() : 0);
            logger.trace("Exiting encryptObjectGlobalAes() method with success");
            return cipherText;
        } catch (Exception e) {
            logger.error("Exception occurred while encrypting object with global AES: {}", e.getMessage(), e);
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