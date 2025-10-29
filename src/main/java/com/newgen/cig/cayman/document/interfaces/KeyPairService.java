package com.newgen.cig.cayman.document.interfaces;

import com.newgen.cig.cayman.document.model.dto.*;

/**
 * Contract for asymmetric key management and digital signatures.
 *
 * <p>Defines RSA/EC key pair generation and ECDSA signing/verification.</p>
 *
 * @author Tarun Vishwakarma
 * @since 2025
 */
public interface KeyPairService {
    /** Generates a new RSA key pair */
    KeyData generateRsaKeyPair();

    /** Generates a new EC key pair (for blockchain) */
    KeyData generateEcKeyPair();

    /** Signs data with a private key */
    SignatureResponse signData(SignatureRequest request);

    /** Verifies a signature with a public key */
    VerificationResponse verifySignature(VerificationRequest request);
}