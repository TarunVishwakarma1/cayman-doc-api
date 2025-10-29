package com.newgen.cig.cayman.document;

import com.newgen.cig.cayman.document.config.PropertyDecryptionInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Cayman Document API.
 * 
 * <p>This Spring Boot application provides RESTful APIs for document management operations
 * including document retrieval, encryption/decryption, and digital signature capabilities.</p>
 * 
 * <h3>Features:</h3>
 * <ul>
 *   <li>Document retrieval from Newgen OmniDocs cabinet</li>
 *   <li>AES and RSA encryption/decryption</li>
 *   <li>Digital signature generation and verification</li>
 *   <li>Rate limiting for API protection</li>
 *   <li>Property encryption for sensitive configuration</li>
 * </ul>
 * 
 * <h3>Configuration:</h3>
 * <p>Application properties are defined in application.yml with support for encrypted values
 * using the ENC(...) pattern.</p>
 * 
 * @author Tarun Vishwakarma
 * @version 1.0
 * @since 2025
 * @see PropertyDecryptionInitializer
 */
@SpringBootApplication
public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

	/**
	 * Main entry point for the Cayman Document API application.
	 * 
	 * <p>Initializes the Spring Boot application context with property decryption
	 * support for encrypted configuration values.</p>
	 * 
	 * @param args command-line arguments passed to the application
	 * @throws Exception if the application fails to start
	 */
	public static void main(String[] args) {
		logger.info("Starting Cayman Document API Application");
		logger.debug("Application arguments count: {}", args != null ? args.length : 0);
		try {
			SpringApplication app = new SpringApplication(Application.class);
            app.addInitializers(new PropertyDecryptionInitializer());
			app.run(args);
			logger.info("Cayman Document API Application started successfully");
		} catch (Exception e) {
			logger.error("Failed to start Cayman Document API Application", e);
			throw e;
		}
	}
}
