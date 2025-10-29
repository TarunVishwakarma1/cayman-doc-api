package com.newgen.cig.cayman.document;

import com.newgen.cig.cayman.document.config.PropertyDecryptionInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);

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
