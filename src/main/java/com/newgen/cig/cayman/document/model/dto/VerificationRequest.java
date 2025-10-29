package com.newgen.cig.cayman.document.model.dto;

public record VerificationRequest(String data, String signature, String base64PublicKey) {}
