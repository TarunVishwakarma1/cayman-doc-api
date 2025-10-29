package com.newgen.cig.cayman.document.utils;

import com.newgen.cig.cayman.document.exception.CryptoException;
import com.newgen.cig.cayman.document.model.enums.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class KeyPair {

    private static final Logger logger = LoggerFactory.getLogger(KeyPair.class);

    private static final String ALGO_RSA = "RSA";
    private static final String ALGO_EC = "EC";
    private static final String EC_CURVE = "secp256r1"; // Common blockchain curve
    private static final String SIGNATURE_ALGO_EC = "SHA256withECDSA";

    private KeyPair() {}

    // --- Key Generation ---

    public static java.security.KeyPair generateRsaKeyPair() {
        logger.trace("Entering generateRsaKeyPair() method");
        logger.info("Generating RSA key pair");
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGO_RSA);
            keyGen.initialize(2048);
            logger.debug("RSA KeyPairGenerator initialized with 2048 bit key size");
            java.security.KeyPair keyPair = keyGen.generateKeyPair();
            logger.info("RSA key pair generated successfully");
            logger.trace("Exiting generateRsaKeyPair() method with success");
            return keyPair;
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to generate RSA key pair. Algorithm not found: {}", ALGO_RSA, e);
            throw new CryptoException(ErrorCode.KEY_GENERATION_ERROR, "Error generating RSA key pair - algorithm not found", e);
        }
    }

    public static java.security.KeyPair generateEcKeyPair() {
        logger.trace("Entering generateEcKeyPair() method");
        logger.info("Generating EC key pair with curve: {}", EC_CURVE);
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGO_EC);
            keyGen.initialize(new ECGenParameterSpec(EC_CURVE));
            logger.debug("EC KeyPairGenerator initialized with curve: {}", EC_CURVE);
            java.security.KeyPair keyPair = keyGen.generateKeyPair();
            logger.info("EC key pair generated successfully");
            logger.trace("Exiting generateEcKeyPair() method with success");
            return keyPair;
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            logger.error("Failed to generate EC key pair. Algorithm: {}, Curve: {}", ALGO_EC, EC_CURVE, e);
            throw new CryptoException(ErrorCode.KEY_GENERATION_ERROR, "Error generating EC key pair - algorithm or curve not found", e);
        }
    }

    // --- Signing & Verification (Blockchain functions) ---

    public static String sign(String data, PrivateKey privateKey) {
        logger.trace("Entering sign() method");
        logger.info("Signing data with private key");
        logger.debug("Data to sign length: {} characters", data != null ? data.length() : 0);
        try {
            Signature ecdsa = Signature.getInstance(SIGNATURE_ALGO_EC);
            ecdsa.initSign(privateKey);
            logger.debug("Signature initialized with algorithm: {}", SIGNATURE_ALGO_EC);
            ecdsa.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signature = ecdsa.sign();
            String encodedSignature = Base64.getEncoder().encodeToString(signature);
            logger.info("Data signed successfully. Signature length: {} bytes", signature.length);
            logger.debug("Base64 encoded signature length: {} characters", encodedSignature.length());
            logger.trace("Exiting sign() method with success");
            return encodedSignature;
        } catch (Exception e) {
            logger.error("Failed to sign data. Algorithm: {}", SIGNATURE_ALGO_EC, e);
            throw new CryptoException(ErrorCode.SIGNATURE_ERROR, "Error signing data", e);
        }
    }

    public static boolean verify(String data, String base64Signature, PublicKey publicKey) {
        logger.trace("Entering verify() method");
        logger.info("Verifying signature");
        logger.debug("Data to verify length: {} characters, Signature length: {} characters", 
                data != null ? data.length() : 0, 
                base64Signature != null ? base64Signature.length() : 0);
        try {
            Signature ecdsa = Signature.getInstance(SIGNATURE_ALGO_EC);
            ecdsa.initVerify(publicKey);
            logger.debug("Signature initialized for verification with algorithm: {}", SIGNATURE_ALGO_EC);
            ecdsa.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = Base64.getDecoder().decode(base64Signature);
            boolean isValid = ecdsa.verify(signatureBytes);
            logger.info("Signature verification completed. Result: {}", isValid);
            logger.trace("Exiting verify() method with result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("Failed to verify signature. Algorithm: {}", SIGNATURE_ALGO_EC, e);
            logger.warn("Verification failed, returning false");
            // A verification failure is not an exception, just a false result.
            return false;
        }
    }

    // --- Key Encoding/Decoding Utilities ---

    public static String encodeKey(Key key) {
        logger.trace("Entering encodeKey() method");
        logger.info("Encoding key to Base64");
        logger.debug("Key algorithm: {}, Key format: {}", 
                key != null ? key.getAlgorithm() : "null", 
                key != null ? key.getFormat() : "null");
        String encoded = Base64.getEncoder().encodeToString(key.getEncoded());
        logger.debug("Key encoded successfully. Encoded length: {} characters", encoded.length());
        logger.trace("Exiting encodeKey() method");
        return encoded;
    }

    public static PublicKey loadRsaPublicKey(String base64Key) {
        logger.trace("Entering loadRsaPublicKey() method");
        logger.info("Loading RSA public key from Base64");
        logger.debug("Base64 key length: {} characters", base64Key != null ? base64Key.length() : 0);
        PublicKey key = loadPublicKey(base64Key, ALGO_RSA);
        logger.info("RSA public key loaded successfully");
        logger.trace("Exiting loadRsaPublicKey() method");
        return key;
    }

    public static PrivateKey loadRsaPrivateKey(String base64Key) {
        logger.trace("Entering loadRsaPrivateKey() method");
        logger.info("Loading RSA private key from Base64");
        logger.debug("Base64 key length: {} characters", base64Key != null ? base64Key.length() : 0);
        PrivateKey key = loadPrivateKey(base64Key, ALGO_RSA);
        logger.info("RSA private key loaded successfully");
        logger.trace("Exiting loadRsaPrivateKey() method");
        return key;
    }

    public static PublicKey loadEcPublicKey(String base64Key) {
        logger.trace("Entering loadEcPublicKey() method");
        logger.info("Loading EC public key from Base64");
        logger.debug("Base64 key length: {} characters", base64Key != null ? base64Key.length() : 0);
        PublicKey key = loadPublicKey(base64Key, ALGO_EC);
        logger.info("EC public key loaded successfully");
        logger.trace("Exiting loadEcPublicKey() method");
        return key;
    }

    public static PrivateKey loadEcPrivateKey(String base64Key) {
        logger.trace("Entering loadEcPrivateKey() method");
        logger.info("Loading EC private key from Base64");
        logger.debug("Base64 key length: {} characters", base64Key != null ? base64Key.length() : 0);
        PrivateKey key = loadPrivateKey(base64Key, ALGO_EC);
        logger.info("EC private key loaded successfully");
        logger.trace("Exiting loadEcPrivateKey() method");
        return key;
    }

    private static PublicKey loadPublicKey(String base64Key, String algorithm) {
        logger.trace("Entering loadPublicKey() method with algorithm: {}", algorithm);
        logger.debug("Loading public key. Algorithm: {}, Base64 key length: {}", 
                algorithm, base64Key != null ? base64Key.length() : 0);
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            logger.debug("Base64 key decoded. Key bytes length: {}", keyBytes.length);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            PublicKey publicKey = kf.generatePublic(spec);
            logger.info("Public key loaded successfully. Algorithm: {}", algorithm);
            logger.trace("Exiting loadPublicKey() method with success");
            return publicKey;
        } catch (Exception e) {
            logger.error("Failed to load public key. Algorithm: {}", algorithm, e);
            throw new CryptoException(ErrorCode.KEY_GENERATION_ERROR, "Error loading public key - invalid key format", e);
        }
    }

    private static PrivateKey loadPrivateKey(String base64Key, String algorithm) {
        logger.trace("Entering loadPrivateKey() method with algorithm: {}", algorithm);
        logger.debug("Loading private key. Algorithm: {}, Base64 key length: {}", 
                algorithm, base64Key != null ? base64Key.length() : 0);
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            logger.debug("Base64 key decoded. Key bytes length: {}", keyBytes.length);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            PrivateKey privateKey = kf.generatePrivate(spec);
            logger.info("Private key loaded successfully. Algorithm: {}", algorithm);
            logger.trace("Exiting loadPrivateKey() method with success");
            return privateKey;
        } catch (Exception e) {
            logger.error("Failed to load private key. Algorithm: {}", algorithm, e);
            throw new CryptoException(ErrorCode.KEY_GENERATION_ERROR, "Error loading private key - invalid key format", e);
        }
    }
}