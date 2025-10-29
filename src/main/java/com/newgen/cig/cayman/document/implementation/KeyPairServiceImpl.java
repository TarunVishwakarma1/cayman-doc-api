package com.newgen.cig.cayman.document.implementation;

import com.newgen.cig.cayman.document.interfaces.KeyPairService;
import com.newgen.cig.cayman.document.model.dto.*;
import com.newgen.cig.cayman.document.utils.KeyPair;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;

@Service
public class KeyPairServiceImpl implements KeyPairService {

    @Override
    public KeyData generateRsaKeyPair() {
        java.security.KeyPair keyPair = KeyPair.generateRsaKeyPair();
        String publicKey = KeyPair.encodeKey(keyPair.getPublic());
        String privateKey = KeyPair.encodeKey(keyPair.getPrivate());
        return new KeyData(publicKey, privateKey);
    }

    @Override
    public KeyData generateEcKeyPair() {
        java.security.KeyPair keyPair = KeyPair.generateEcKeyPair();
        String publicKey = KeyPair.encodeKey(keyPair.getPublic());
        String privateKey = KeyPair.encodeKey(keyPair.getPrivate());
        return new KeyData(publicKey, privateKey);
    }

    @Override
    public SignatureResponse signData(SignatureRequest request) {
        PrivateKey privateKey = KeyPair.loadEcPrivateKey(request.base64PrivateKey());
        String signature = KeyPair.sign(request.data(), privateKey);
        return new SignatureResponse(request.data(), signature);
    }

    @Override
    public VerificationResponse verifySignature(VerificationRequest request) {
        PublicKey publicKey = KeyPair.loadEcPublicKey(request.base64PublicKey());
        boolean isVerified = KeyPair.verify(request.data(), request.signature(), publicKey);
        return new VerificationResponse(isVerified);
    }
}