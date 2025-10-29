package com.newgen.cig.cayman.document.controller;

import com.newgen.cig.cayman.document.interfaces.DecryptionService;
import com.newgen.cig.cayman.document.interfaces.EncryptionService;
import com.newgen.cig.cayman.document.interfaces.KeyPairService;
import com.newgen.cig.cayman.document.model.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * Security controller exposing cryptographic operations.
 *
 * <p>Provides endpoints for key pair generation, AES encryption/decryption,
 * RSA encryption/decryption, and signing/verification operations.</p>
 *
 * <h3>Endpoints:</h3>
 * <ul>
 *   <li><b>GET</b> {@code /api/security} – Welcome/health</li>
 *   <li><b>GET</b> {@code /api/security/keys/rsa} – Generate RSA key pair</li>
 *   <li><b>GET</b> {@code /api/security/keys/ec} – Generate EC key pair</li>
 *   <li><b>POST</b> {@code /api/security/aes/encrypt} – AES encrypt text</li>
 *   <li><b>POST</b> {@code /api/security/aes/decrypt} – AES decrypt text</li>
 *   <li><b>POST</b> {@code /api/security/rsa/encrypt} – RSA encrypt text</li>
 *   <li><b>POST</b> {@code /api/security/rsa/decrypt} – RSA decrypt text</li>
 *   <li><b>POST</b> {@code /api/security/sign} – Sign data</li>
 *   <li><b>POST</b> {@code /api/security/verify} – Verify signature</li>
 * </ul>
 *
 * @author Tarun Vishwakarma
 * @since 2025
 */
@RestController
@RequestMapping("/api/security")
public class SecurityController {

    private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);
    
    private final EncryptionService encryptionService;
    private final DecryptionService decryptionService;
    private final KeyPairService keyPairService;

    public SecurityController(EncryptionService encryptionService,
                              DecryptionService decryptionService,
                              KeyPairService keyPairService) {
        logger.trace("Initializing SecurityController with service dependencies");
        this.encryptionService = encryptionService;
        this.decryptionService = decryptionService;
        this.keyPairService = keyPairService;
        logger.info("SecurityController initialized successfully");
    }


    /**
     * Welcome endpoint for quick reachability checks.
     *
     * @return HTML welcome banner
     */
    @GetMapping()
    public String welcome(){
        logger.trace("Entering welcome() method");
        logger.info("Security endpoint accessed");
        String response = "<h1>Welcome to Security</h1>";
        logger.debug("Returning welcome response");
        logger.trace("Exiting welcome() method");
        return response;
    }

    // --- KeyPair Endpoints ---

    /**
     * Generates a new RSA key pair.
     *
     * @return {@link KeyData} containing base64-encoded public and private keys
     */
    @GetMapping("/keys/rsa")
    public KeyData getRsaKeys() {
        logger.trace("Entering getRsaKeys() method");
        logger.info("Request received to generate RSA key pair");
        try {
            logger.debug("Calling keyPairService.generateRsaKeyPair()");
            KeyData keyData = keyPairService.generateRsaKeyPair();
            logger.info("RSA key pair generated successfully");
            logger.debug("Public key length: {}, Private key length: {}", 
                    keyData.publicKey() != null ? keyData.publicKey().length() : 0,
                    keyData.privateKey() != null ? keyData.privateKey().length() : 0);
            logger.trace("Exiting getRsaKeys() method with success");
            return keyData;
        } catch (Exception e) {
            logger.error("Exception occurred while generating RSA key pair: {}", e.getMessage(), e);
            throw e;
        }
    }


    /**
     * Generates a new EC (Elliptic Curve) key pair.
     *
     * @return {@link KeyData} containing base64-encoded public and private keys
     */
    @GetMapping("/keys/ec")
    public KeyData getEcKeys() {
        logger.trace("Entering getEcKeys() method");
        logger.info("Request received to generate EC key pair");
        try {
            logger.debug("Calling keyPairService.generateEcKeyPair()");
            KeyData keyData = keyPairService.generateEcKeyPair();
            logger.info("EC key pair generated successfully");
            logger.debug("Public key length: {}, Private key length: {}", 
                    keyData.publicKey() != null ? keyData.publicKey().length() : 0,
                    keyData.privateKey() != null ? keyData.privateKey().length() : 0);
            logger.trace("Exiting getEcKeys() method with success");
            return keyData;
        } catch (Exception e) {
            logger.error("Exception occurred while generating EC key pair: {}", e.getMessage(), e);
            throw e;
        }
    }

    // --- AES Endpoints ---

    /**
     * Encrypts plain text using global AES key.
     *
     * @param request input containing the plain text
     * @return encrypted text wrapped in {@link TextResponse}
     */
    @PostMapping("/aes/encrypt")
    public TextResponse encryptAes(@RequestBody TextRequest request) {
        logger.trace("Entering encryptAes() method");
        logger.info("Request received to encrypt text using AES");
        try {
            String plainText = request.text();
            logger.debug("Plain text length: {}", plainText != null ? plainText.length() : 0);
            logger.trace("Calling encryptionService.encryptGlobalAes()");
            String cipherText = encryptionService.encryptGlobalAes(plainText);
            logger.info("Text encrypted successfully using AES");
            logger.debug("Cipher text length: {}", cipherText != null ? cipherText.length() : 0);
            logger.trace("Exiting encryptAes() method with success");
            return new TextResponse(cipherText);
        } catch (Exception e) {
            logger.error("Exception occurred while encrypting with AES: {}", e.getMessage(), e);
            throw e;
        }
    }


    /**
     * Decrypts cipher text using global AES key.
     *
     * @param request input containing the base64 cipher text
     * @return decrypted text wrapped in {@link TextResponse}
     */
    @PostMapping("/aes/decrypt")
    public TextResponse decryptAes(@RequestBody TextRequest request) {
        logger.trace("Entering decryptAes() method");
        logger.info("Request received to decrypt text using AES");
        try {
            String cipherText = request.text();
            logger.debug("Cipher text length: {}", cipherText != null ? cipherText.length() : 0);
            logger.trace("Calling decryptionService.decryptGlobalAes()");
            String plainText = decryptionService.decryptGlobalAes(cipherText);
            logger.info("Text decrypted successfully using AES");
            logger.debug("Plain text length: {}", plainText != null ? plainText.length() : 0);
            logger.trace("Exiting decryptAes() method with success");
            return new TextResponse(plainText);
        } catch (Exception e) {
            logger.error("Exception occurred while decrypting with AES: {}", e.getMessage(), e);
            throw e;
        }
    }

    // --- RSA Endpoints ---

    /**
     * Encrypts plain text using an RSA public key.
     *
     * @param request input containing plain text and public key
     * @return encrypted text wrapped in {@link TextResponse}
     */
    @PostMapping("/rsa/encrypt")
    public TextResponse encryptRsa(@RequestBody RsaEncryptionRequest request) {
        logger.trace("Entering encryptRsa() method");
        logger.info("Request received to encrypt text using RSA");
        try {
            String plainText = request.plainText();
            logger.debug("Plain text length: {}, Public key provided: {}", 
                    plainText != null ? plainText.length() : 0,
                    request.base64PublicKey() != null);
            logger.trace("Calling encryptionService.encryptWithRsa()");
            String cipherText = encryptionService.encryptWithRsa(request);
            logger.info("Text encrypted successfully using RSA");
            logger.debug("Cipher text length: {}", cipherText != null ? cipherText.length() : 0);
            logger.trace("Exiting encryptRsa() method with success");
            return new TextResponse(cipherText);
        } catch (Exception e) {
            logger.error("Exception occurred while encrypting with RSA: {}", e.getMessage(), e);
            throw e;
        }
    }


    /**
     * Decrypts cipher text using an RSA private key.
     *
     * @param request input containing cipher text and private key
     * @return decrypted text wrapped in {@link TextResponse}
     */
    @PostMapping("/rsa/decrypt")
    public TextResponse decryptRsa(@RequestBody RsaDecryptionRequest request) {
        logger.trace("Entering decryptRsa() method");
        logger.info("Request received to decrypt text using RSA");
        try {
            logger.debug("Cipher text length: {}, Private key provided: {}", 
                    request.cipherText() != null ? request.cipherText().length() : 0,
                    request.base64PrivateKey() != null);
            logger.trace("Calling decryptionService.decryptWithRsa()");
            String plainText = decryptionService.decryptWithRsa(request);
            logger.info("Text decrypted successfully using RSA");
            logger.debug("Plain text length: {}", plainText != null ? plainText.length() : 0);
            logger.trace("Exiting decryptRsa() method with success");
            return new TextResponse(plainText);
        } catch (Exception e) {
            logger.error("Exception occurred while decrypting with RSA: {}", e.getMessage(), e);
            throw e;
        }
    }

    // --- Signing & Verification Endpoints ---

    /**
     * Signs input data using a provided private key.
     *
     * @param request input containing data and private key
     * @return {@link SignatureResponse} containing the signature
     */
    @PostMapping("/sign")
    public SignatureResponse sign(@RequestBody SignatureRequest request) {
        logger.trace("Entering sign() method");
        logger.info("Request received to sign data");
        try {
            logger.debug("Data length: {}, Private key provided: {}", 
                    request.data() != null ? request.data().length() : 0,
                    request.base64PrivateKey() != null);
            logger.trace("Calling keyPairService.signData()");
            SignatureResponse response = keyPairService.signData(request);
            logger.info("Data signed successfully");
            logger.debug("Signature length: {}", 
                    response.signature() != null ? response.signature().length() : 0);
            logger.trace("Exiting sign() method with success");
            return response;
        } catch (Exception e) {
            logger.error("Exception occurred while signing data: {}", e.getMessage(), e);
            throw e;
        }
    }


    /**
     * Verifies a signature for the supplied data using a public key.
     *
     * @param request input containing data, signature, and public key
     * @return {@link VerificationResponse} indicating verification result
     */
    @PostMapping("/verify")
    public VerificationResponse verify(@RequestBody VerificationRequest request) {
        logger.trace("Entering verify() method");
        logger.info("Request received to verify signature");
        try {
            logger.debug("Data length: {}, Signature length: {}, Public key provided: {}", 
                    request.data() != null ? request.data().length() : 0,
                    request.signature() != null ? request.signature().length() : 0,
                    request.base64PublicKey() != null);
            logger.trace("Calling keyPairService.verifySignature()");
            VerificationResponse response = keyPairService.verifySignature(request);
            logger.info("Signature verification completed. Verified: {}", response.isVerified());
            logger.debug("Verification result: {}", response.isVerified());
            logger.trace("Exiting verify() method with success");
            return response;
        } catch (Exception e) {
            logger.error("Exception occurred while verifying signature: {}", e.getMessage(), e);
            throw e;
        }
    }
}