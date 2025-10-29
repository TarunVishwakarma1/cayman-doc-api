package com.newgen.cig.cayman.document.implementation;

import com.newgen.cig.cayman.document.exception.CryptoException;
import com.newgen.cig.cayman.document.interfaces.DecryptionService;
import com.newgen.cig.cayman.document.model.dto.RsaDecryptionRequest;
import com.newgen.cig.cayman.document.utils.Decryption;
import com.newgen.cig.cayman.document.utils.KeyPair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Arrays;

@Service
public class DecryptionServiceImpl implements DecryptionService {

    private final SecretKey globalAesKey;

    public DecryptionServiceImpl(@Value("${my.security.aes-secret}") String aesSecret) {
        this.globalAesKey = createAesKey(aesSecret);
    }

    @Override
    public String decryptGlobalAes(String cipherText) {
        return Decryption.decryptAes(cipherText, globalAesKey);
    }

    @Override
    public String decryptWithRsa(RsaDecryptionRequest request) {
        PrivateKey privateKey = KeyPair.loadRsaPrivateKey(request.base64PrivateKey());
        return Decryption.decryptRsa(request.cipherText(), privateKey);
    }

    @Override
    public <T> T decryptObjectGlobalAes(String cipherText, Class<T> clazz) {
        return Decryption.decryptObjectAes(cipherText, globalAesKey, clazz);
    }

    /** Creates a 256-bit AES key from a string secret using SHA-256. */
    private SecretKey createAesKey(String secret) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = sha.digest(secret.getBytes(StandardCharsets.UTF_8));
            keyBytes = Arrays.copyOf(keyBytes, 32); // Use 32 bytes (256 bits)
            return new SecretKeySpec(keyBytes, "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("Error creating AES key", e);
        }
    }
}