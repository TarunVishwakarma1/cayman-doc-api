package com.newgen.cig.cayman.document.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Application configuration class that defines Spring beans for the application.
 * 
 * <p>This configuration class provides bean definitions for HTTP client operations
 * and other application-wide components.</p>
 * 
 * <h3>Beans Provided:</h3>
 * <ul>
 *   <li>{@link RestTemplate} - HTTP client for external API calls</li>
 * </ul>
 * 
 * @author Tarun Vishwakarma
 * @version 1.0
 * @since 2025
 */
@Configuration
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    /**
     * Creates and configures a {@link RestTemplate} bean for HTTP operations.
     * 
     * <p>The RestTemplate is used for making RESTful API calls to external services,
     * particularly the Newgen OmniDocs REST web services.</p>
     * 
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * @Autowired
     * private RestTemplate restTemplate;
     * 
     * String response = restTemplate.getForObject(url, String.class);
     * }</pre>
     * 
     * @param restTemplateBuilder the builder provided by Spring Boot for creating RestTemplate
     * @return configured RestTemplate instance ready for use
     * @throws RuntimeException if RestTemplate creation fails
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder){
        logger.trace("Creating RestTemplate bean");
        logger.info("Configuring RestTemplate bean");
        try {
            RestTemplate restTemplate = restTemplateBuilder.build();
            logger.info("RestTemplate bean created successfully");
            logger.debug("RestTemplate instance created: {}", restTemplate.getClass().getName());
            return restTemplate;
        } catch (Exception e) {
            logger.error("Exception occurred while creating RestTemplate bean: {}", e.getMessage(), e);
            throw e;
        }
    }

}
