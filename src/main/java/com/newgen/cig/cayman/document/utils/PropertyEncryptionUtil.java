package com.newgen.cig.cayman.document.utils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Scanner;

public class PropertyEncryptionUtil {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Property Encryption Utility ===");
        System.out.print("Enter AES Secret Key: ");
        String aesSecret = scanner.nextLine();
        
        System.out.print("Enter plain text to encrypt: ");
        String plainText = scanner.nextLine();
        
        try {
            SecretKey secretKey = createAesKey(aesSecret);
            String encrypted = Encryption.encryptAes(plainText, secretKey);
            
            System.out.println("\n--- Result ---");
            System.out.println("Encrypted Value: " + encrypted);
            System.out.println("\nAdd this to application.properties:");
            System.out.println("property.name=ENC(" + encrypted + ")");
            
            // Verify decryption
            String decrypted = Decryption.decryptAes(encrypted, secretKey);
            System.out.println("\nVerification (decrypted): " + decrypted);
            System.out.println("Match: " + plainText.equals(decrypted));
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
    
    private static SecretKey createAesKey(String secret) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(hash, "AES");
    }
}
