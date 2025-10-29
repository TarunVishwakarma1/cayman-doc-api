package com.newgen.cig.cayman.document.controller;

import com.newgen.cig.cayman.document.interfaces.DecryptionService;
import com.newgen.cig.cayman.document.interfaces.EncryptionService;
import com.newgen.cig.cayman.document.interfaces.KeyPairService;
import com.newgen.cig.cayman.document.model.dto.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/security")
public class SecurityController {

    private final EncryptionService encryptionService;
    private final DecryptionService decryptionService;
    private final KeyPairService keyPairService;

    public SecurityController(EncryptionService encryptionService,
                              DecryptionService decryptionService,
                              KeyPairService keyPairService) {
        this.encryptionService = encryptionService;
        this.decryptionService = decryptionService;
        this.keyPairService = keyPairService;
    }

    @GetMapping()
    public String welcome(){
        return"<h1>Welcome to Security</h1>";
    }

    // --- KeyPair Endpoints ---
    @GetMapping("/keys/rsa")
    public KeyData getRsaKeys() {
        return keyPairService.generateRsaKeyPair();
    }

    @GetMapping("/keys/ec")
    public KeyData getEcKeys() {
        return keyPairService.generateEcKeyPair();
    }

    // --- AES Endpoints ---
    @PostMapping("/aes/encrypt")
    public TextResponse encryptAes(@RequestBody TextRequest request) {
        String cipherText = encryptionService.encryptGlobalAes(request.text());
        return new TextResponse(cipherText);
    }

    @PostMapping("/aes/decrypt")
    public TextResponse decryptAes(@RequestBody TextRequest request) {
        String plainText = decryptionService.decryptGlobalAes(request.text());
        return new TextResponse(plainText);
    }

    // --- RSA Endpoints ---
    @PostMapping("/rsa/encrypt")
    public TextResponse encryptRsa(@RequestBody RsaEncryptionRequest request) {
        String cipherText = encryptionService.encryptWithRsa(request);
        return new TextResponse(cipherText);
    }

    @PostMapping("/rsa/decrypt")
    public TextResponse decryptRsa(@RequestBody RsaDecryptionRequest request) {
        String plainText = decryptionService.decryptWithRsa(request);
        return new TextResponse(plainText);
    }

    // --- Signing & Verification Endpoints ---
    @PostMapping("/sign")
    public SignatureResponse sign(@RequestBody SignatureRequest request) {
        return keyPairService.signData(request);
    }

    @PostMapping("/verify")
    public VerificationResponse verify(@RequestBody VerificationRequest request) {
        return keyPairService.verifySignature(request);
    }
}