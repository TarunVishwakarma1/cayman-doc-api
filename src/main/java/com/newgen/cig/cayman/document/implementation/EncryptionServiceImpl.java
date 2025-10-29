package com.newgen.cig.cayman.document.implementation;

import com.newgen.cig.cayman.document.exception.CryptoException;
import com.newgen.cig.cayman.document.interfaces.EncryptionService;
import com.newgen.cig.cayman.document.model.dto.RsaEncryptionRequest;
import com.newgen.cig.cayman.document.utils.Encryption;
import com.newgen.cig.cayman.document.utils.KeyPair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;

@Service
public class EncryptionServiceImpl implements EncryptionService {

    private final SecretKey globalAesKey;

    public EncryptionServiceImpl(@Value("${my.security.aes-secret}") String aesSecret) {
        this.globalAesKey = createAesKey(aesSecret);
    }

    @Override
    public String encryptGlobalAes(String plainText) {
        return Encryption.encryptAes(plainText, globalAesKey);
    }

    @Override
    public String encryptWithRsa(RsaEncryptionRequest request) {
        PublicKey publicKey = KeyPair.loadRsaPublicKey(request.base64PublicKey());
        return Encryption.encryptRsa(request.plainText(), publicKey);
    }

    @Override
    public String encryptObjectGlobalAes(Object obj) {
        return Encryption.encryptObjectAes(obj, globalAesKey);
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