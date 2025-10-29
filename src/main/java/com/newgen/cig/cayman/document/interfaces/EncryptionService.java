package com.newgen.cig.cayman.document.interfaces;

import com.newgen.cig.cayman.document.model.dto.RsaEncryptionRequest;

/**
 * Contract for encryption operations.
 *
 * <p>Defines AES and RSA encryption behavior for text and objects.</p>
 *
 * @author Tarun Vishwakarma
 * @since 2025
 */
public interface EncryptionService {
    /** Encrypts data using the global application AES key */
    String encryptGlobalAes(String plainText);

    /** Encrypts data using a provided RSA public key */
    String encryptWithRsa(RsaEncryptionRequest request);

    /** Encrypts any object using the global AES key */
    String encryptObjectGlobalAes(Object obj);
}
