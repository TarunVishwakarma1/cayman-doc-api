package com.newgen.cig.cayman.document.model.dto;

public record RsaDecryptionRequest(String cipherText, String base64PrivateKey) {}
