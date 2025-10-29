package com.newgen.cig.cayman.document.interfaces;

import com.newgen.cig.cayman.document.model.dto.RsaDecryptionRequest;

public interface DecryptionService {
    /** Decrypts data using the global application AES key */
    String decryptGlobalAes(String cipherText);

    /** Decrypts data using a provided RSA private key */
    String decryptWithRsa(RsaDecryptionRequest request);

    /** Decrypts data to an object using the global AES key */
    <T> T decryptObjectGlobalAes(String cipherText, Class<T> clazz);
}
