package com.newgen.cig.cayman.document.implementation;

import com.newgen.cig.cayman.document.interfaces.KeyPairService;
import com.newgen.cig.cayman.document.model.dto.*;
import com.newgen.cig.cayman.document.utils.KeyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Implementation of {@link KeyPairService} providing RSA/EC key pair
 * generation and ECDSA signature utilities.
 *
 * <h3>Responsibilities:</h3>
 * <ul>
 *   <li>Generate RSA and EC key pairs</li>
 *   <li>Sign data with EC private key</li>
 *   <li>Verify signatures with EC public key</li>
 * </ul>
 *
 * @author Tarun Vishwakarma
 * @since 2025
 */
@Service
public class KeyPairServiceImpl implements KeyPairService {

    private static final Logger logger = LoggerFactory.getLogger(KeyPairServiceImpl.class);

    @Override
    public KeyData generateRsaKeyPair() {
        logger.trace("Entering generateRsaKeyPair() method");
        logger.info("Generating RSA key pair");
        try {
            logger.debug("Calling KeyPair.generateRsaKeyPair()");
            java.security.KeyPair keyPair = KeyPair.generateRsaKeyPair();
            logger.debug("RSA key pair generated successfully");
            
            logger.trace("Encoding public and private keys to base64");
            String publicKey = KeyPair.encodeKey(keyPair.getPublic());
            String privateKey = KeyPair.encodeKey(keyPair.getPrivate());
            
            logger.info("RSA key pair generated and encoded successfully");
            logger.debug("Public key length: {}, Private key length: {}", 
                    publicKey != null ? publicKey.length() : 0,
                    privateKey != null ? privateKey.length() : 0);
            logger.trace("Exiting generateRsaKeyPair() method with success");
            return new KeyData(publicKey, privateKey);
        } catch (Exception e) {
            logger.error("Exception occurred while generating RSA key pair: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public KeyData generateEcKeyPair() {
        logger.trace("Entering generateEcKeyPair() method");
        logger.info("Generating EC key pair");
        try {
            logger.debug("Calling KeyPair.generateEcKeyPair()");
            java.security.KeyPair keyPair = KeyPair.generateEcKeyPair();
            logger.debug("EC key pair generated successfully");
            
            logger.trace("Encoding public and private keys to base64");
            String publicKey = KeyPair.encodeKey(keyPair.getPublic());
            String privateKey = KeyPair.encodeKey(keyPair.getPrivate());
            
            logger.info("EC key pair generated and encoded successfully");
            logger.debug("Public key length: {}, Private key length: {}", 
                    publicKey != null ? publicKey.length() : 0,
                    privateKey != null ? privateKey.length() : 0);
            logger.trace("Exiting generateEcKeyPair() method with success");
            return new KeyData(publicKey, privateKey);
        } catch (Exception e) {
            logger.error("Exception occurred while generating EC key pair: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public SignatureResponse signData(SignatureRequest request) {
        logger.trace("Entering signData() method");
        logger.info("Signing data with EC private key");
        try {
            logger.debug("Data length: {}, Private key provided: {}", 
                    request.data() != null ? request.data().length() : 0,
                    request.base64PrivateKey() != null);
            logger.trace("Loading EC private key from base64 string");
            PrivateKey privateKey = KeyPair.loadEcPrivateKey(request.base64PrivateKey());
            logger.debug("EC private key loaded successfully");
            
            logger.trace("Signing data with private key");
            String signature = KeyPair.sign(request.data(), privateKey);
            logger.info("Data signed successfully");
            logger.debug("Signature length: {}", signature != null ? signature.length() : 0);
            logger.trace("Exiting signData() method with success");
            return new SignatureResponse(request.data(), signature);
        } catch (Exception e) {
            logger.error("Exception occurred while signing data: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public VerificationResponse verifySignature(VerificationRequest request) {
        logger.trace("Entering verifySignature() method");
        logger.info("Verifying signature with EC public key");
        try {
            logger.debug("Data length: {}, Signature length: {}, Public key provided: {}", 
                    request.data() != null ? request.data().length() : 0,
                    request.signature() != null ? request.signature().length() : 0,
                    request.base64PublicKey() != null);
            logger.trace("Loading EC public key from base64 string");
            PublicKey publicKey = KeyPair.loadEcPublicKey(request.base64PublicKey());
            logger.debug("EC public key loaded successfully");
            
            logger.trace("Verifying signature");
            boolean isVerified = KeyPair.verify(request.data(), request.signature(), publicKey);
            logger.info("Signature verification completed. Verified: {}", isVerified);
            logger.debug("Verification result: {}", isVerified);
            logger.trace("Exiting verifySignature() method with success");
            return new VerificationResponse(isVerified);
        } catch (Exception e) {
            logger.error("Exception occurred while verifying signature: {}", e.getMessage(), e);
            throw e;
        }
    }
}