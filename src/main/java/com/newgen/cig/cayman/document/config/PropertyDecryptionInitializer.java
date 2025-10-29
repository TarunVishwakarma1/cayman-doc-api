package com.newgen.cig.cayman.document.config;

import com.newgen.cig.cayman.document.utils.Decryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyDecryptionInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger logger = LoggerFactory.getLogger(PropertyDecryptionInitializer.class);
    private static final Pattern ENCRYPTED_VALUE_PATTERN = Pattern.compile("^ENC\\((.+)\\)$");
    private static final String CABINET_PROPERTY_PREFIX = "newgen.cayman.connect.cabinet.";
    private static final String AES_SECRET_PROPERTY = "my.security.aes-secret";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        logger.info("========================================");
        logger.info("PropertyDecryptionInitializer - Starting property decryption");

        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        logger.debug("Active profiles: {}", (Object[]) environment.getActiveProfiles());
        logger.debug("Total property sources: {}", environment.getPropertySources().size());
        
        // Log all property source names for debugging
        for (PropertySource<?> ps : environment.getPropertySources()) {
            logger.debug("Property source found: {} (type: {})", ps.getName(), ps.getClass().getSimpleName());
        }
        
        // Get the AES secret
        String aesSecret = environment.getProperty(AES_SECRET_PROPERTY);
        
        if (aesSecret == null || aesSecret.isEmpty()) {
            logger.warn("AES secret not found. Skipping property decryption.");
            logger.warn("Property '{}' = '{}'", AES_SECRET_PROPERTY, aesSecret);
            return;
        }
        
        logger.info("AES secret found. Creating decryption key...");
        
        SecretKey secretKey;
        try {
            secretKey = createAesKey(aesSecret);
            logger.info("AES key created successfully");
        } catch (Exception e) {
            logger.error("Failed to create AES key", e);
            throw new RuntimeException("Failed to create AES key", e);
        }
        
        Map<String, Object> decryptedProperties = new HashMap<>();
        int encryptedCount = 0;
        int decryptedCount = 0;
        int totalPropertiesScanned = 0;
        
        // Process all property sources
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            logger.trace("Examining property source: {}", propertySource.getName());
            
            // Handle both MapPropertySource and OriginTrackedMapPropertySource
            if (propertySource instanceof MapPropertySource mapPropertySource) {
                logger.debug("Processing MapPropertySource: {}", propertySource.getName());
                Map<String, Object> source = mapPropertySource.getSource();
                logger.debug("Property source '{}' contains {} properties", propertySource.getName(), source.size());
                
                for (Map.Entry<String, Object> entry : source.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    
                    if (key.startsWith(CABINET_PROPERTY_PREFIX)) {
                        totalPropertiesScanned++;
                        
                        // Extract the actual string value from OriginTrackedValue if needed
                        String stringValue = extractStringValue(value);
                        
                        logger.trace("Found cabinet property: {} = {} (type: {})", 
                            key, stringValue, value != null ? value.getClass().getSimpleName() : "null");
                        
                        if (stringValue != null) {
                            Matcher matcher = ENCRYPTED_VALUE_PATTERN.matcher(stringValue);
                            
                            if (matcher.matches()) {
                                encryptedCount++;
                                String encryptedValue = matcher.group(1);
                                logger.info("Found encrypted property: {}", key);
                                logger.debug("Encrypted value: ENC({}...)", 
                                    encryptedValue.length() > 10 ? encryptedValue.substring(0, 10) : encryptedValue);
                                
                                try {
                                    String decryptedValue = Decryption.decryptAes(encryptedValue, secretKey);
                                    decryptedProperties.put(key, decryptedValue);
                                    decryptedCount++;
                                    logger.info("Successfully decrypted: {}", key);
                                    logger.debug("Decrypted value length: {} characters", decryptedValue.length());
                                } catch (Exception e) {
                                    logger.error("Failed to decrypt property: {}", key, e);
                                    throw new RuntimeException("Failed to decrypt property: " + key, e);
                                }
                            } else {
                                logger.trace("Property '{}' does not match ENC(...) pattern", key);
                            }
                        } else {
                            logger.trace("Property '{}' could not be converted to String", key);
                        }
                    }
                }
            } else {
                logger.trace("Skipping property source '{}' - not a MapPropertySource (type: {})", 
                    propertySource.getName(), propertySource.getClass().getSimpleName());
            }
        }
        
        logger.info("Property scan complete:");
        logger.info("  - Total cabinet properties scanned: {}", totalPropertiesScanned);
        logger.info("  - Encrypted properties found: {}", encryptedCount);
        logger.info("  - Successfully decrypted: {}", decryptedCount);
        
        if (encryptedCount == 0 && totalPropertiesScanned > 0) {
            logger.warn("No encrypted properties found even though {} cabinet properties were scanned!", totalPropertiesScanned);
            logger.warn("Check if the ENC(...) pattern is correct in application.properties");
        }
        
        if (!decryptedProperties.isEmpty()) {
            environment.getPropertySources().addFirst(
                new MapPropertySource("decryptedProperties", decryptedProperties)
            );
            logger.info("Added {} decrypted properties to environment with highest priority", decryptedProperties.size());
            logger.debug("Decrypted property keys: {}", decryptedProperties.keySet());
        }
        
        logger.info("Property decryption completed");
        logger.info("========================================");
    }
    
    /**
     * Extracts the string value from either a plain String or an OriginTrackedValue wrapper.
     */
    private String extractStringValue(Object value) {
        if (value == null) {
            return null;
        }
        
        // If it's a plain String, return it
        if (value instanceof String) {
            return (String) value;
        }
        
        // If it's wrapped in OriginTrackedValue, unwrap it
        if (value instanceof OriginTrackedValue originTrackedValue) {
            Object unwrapped = originTrackedValue.getValue();
            if (unwrapped instanceof String) {
                return (String) unwrapped;
            }
        }
        
        // Try toString() as a fallback
        return value.toString();
    }
    
    private SecretKey createAesKey(String secret) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(hash, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create AES key", e);
        }
    }
}