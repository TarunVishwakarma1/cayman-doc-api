package com.newgen.cig.cayman.document.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

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
