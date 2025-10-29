package com.newgen.cig.cayman.document.interfaces;

import com.newgen.cig.cayman.document.model.dto.RsaDecryptionRequest;

/**
 * Contract for decryption operations.
 *
 * <p>Defines AES and RSA decryption behavior for text and objects.</p>
 *
 * @author Tarun Vishwakarma
 * @since 2025
 */
public interface DecryptionService {
    /** Decrypts data using the global application AES key */
    String decryptGlobalAes(String cipherText);

    /** Decrypts data using a provided RSA private key */
    String decryptWithRsa(RsaDecryptionRequest request);

    /** Decrypts data to an object using the global AES key */
    <T> T decryptObjectGlobalAes(String cipherText, Class<T> clazz);
}
