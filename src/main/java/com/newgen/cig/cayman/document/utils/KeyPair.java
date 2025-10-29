package com.newgen.cig.cayman.document.utils;

import com.newgen.cig.cayman.document.exception.CryptoException;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class KeyPair {

    private static final String ALGO_RSA = "RSA";
    private static final String ALGO_EC = "EC";
    private static final String EC_CURVE = "secp256r1"; // Common blockchain curve
    private static final String SIGNATURE_ALGO_EC = "SHA256withECDSA";

    private KeyPair() {}

    // --- Key Generation ---

    public static java.security.KeyPair generateRsaKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGO_RSA);
            keyGen.initialize(2048);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("Error generating RSA key pair", e);
        }
    }

    public static java.security.KeyPair generateEcKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGO_EC);
            keyGen.initialize(new ECGenParameterSpec(EC_CURVE));
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new CryptoException("Error generating EC key pair", e);
        }
    }

    // --- Signing & Verification (Blockchain functions) ---

    public static String sign(String data, PrivateKey privateKey) {
        try {
            Signature ecdsa = Signature.getInstance(SIGNATURE_ALGO_EC);
            ecdsa.initSign(privateKey);
            ecdsa.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signature = ecdsa.sign();
            return Base64.getEncoder().encodeToString(signature);
        } catch (Exception e) {
            throw new CryptoException("Error signing data", e);
        }
    }

    public static boolean verify(String data, String base64Signature, PublicKey publicKey) {
        try {
            Signature ecdsa = Signature.getInstance(SIGNATURE_ALGO_EC);
            ecdsa.initVerify(publicKey);
            ecdsa.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] signatureBytes = Base64.getDecoder().decode(base64Signature);
            return ecdsa.verify(signatureBytes);
        } catch (Exception e) {
            // A verification failure is not an exception, just a false result.
            return false;
        }
    }

    // --- Key Encoding/Decoding Utilities ---

    public static String encodeKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static PublicKey loadRsaPublicKey(String base64Key) {
        return loadPublicKey(base64Key, ALGO_RSA);
    }

    public static PrivateKey loadRsaPrivateKey(String base64Key) {
        return loadPrivateKey(base64Key, ALGO_RSA);
    }

    public static PublicKey loadEcPublicKey(String base64Key) {
        return loadPublicKey(base64Key, ALGO_EC);
    }

    public static PrivateKey loadEcPrivateKey(String base64Key) {
        return loadPrivateKey(base64Key, ALGO_EC);
    }

    private static PublicKey loadPublicKey(String base64Key, String algorithm) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            return kf.generatePublic(spec);
        } catch (Exception e) {
            throw new CryptoException("Error loading public key", e);
        }
    }

    private static PrivateKey loadPrivateKey(String base64Key, String algorithm) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(algorithm);
            return kf.generatePrivate(spec);
        } catch (Exception e) {
            throw new CryptoException("Error loading private key", e);
        }
    }
}