package com.newgen.cig.cayman.document.implementation;

import com.newgen.cig.cayman.document.model.dao.CabinetProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.newgen.cig.cayman.document.exception.JsonParsingException;

    /**
     * Helper service for XML-related operations with Newgen DMS APIs.
     *
     * <p>Provides utilities to extract values from XML responses and to
     * execute XML against the DMS Call Broker using configured JTS endpoints.</p>
     *
     * <h3>Responsibilities:</h3>
     * <ul>
     *   <li>Read values from XML using DMSXmlResponse</li>
     *   <li>Execute XML commands against DMSCallBroker</li>
     *   <li>Validate configuration and map common errors</li>
     * </ul>
     *
     * @author Tarun Vishwakarma
     * @since 2025
     */
    @Service
    public class Operations {

    private static final Logger logger = LoggerFactory.getLogger(Operations.class);

    @Autowired
    private CabinetProperties cabinet;

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Extracts a value from JSON by key path.
     *
     * @param json raw JSON content
     * @param key target node key (supports nested paths like "NGOExecuteAPIResponseBDO.outputData.NGOConnectCabinet_Output.UserDBId")
     * @return extracted value as string
     * @throws JsonParsingException on invalid JSON or missing key
     */
    public String getValueFromJSON(String json, String key) {
        logger.trace("Entering getValueFromJSON() method. Key: {}", key);
        logger.debug("Parameters provided - JSON length: {}, Key: {}", json != null ? json.length() : 0, key);
        
        if (json == null || json.trim().isEmpty()) {
            logger.error("JSON is null or empty");
            throw new JsonParsingException("JSON content cannot be null or empty");
        }
        
        if (key == null || key.trim().isEmpty()) {
            logger.error("Key is null or empty");
            throw new JsonParsingException("Key cannot be null or empty");
        }
        
        try {
            logger.trace("Parsing JSON content");
            JsonNode rootNode = objectMapper.readTree(json);
            
            // Navigate through nested path if key contains dots
            JsonNode currentNode = rootNode;
            String[] pathParts = key.split("\\.");
            
            for (String part : pathParts) {
                logger.trace("Navigating to JSON node: {}", part);
                currentNode = currentNode.get(part);
                if (currentNode == null) {
                    logger.error("JSON path not found: {}", key);
                    throw new JsonParsingException("JSON path not found: " + key);
                }
            }
            
            String value = currentNode.isTextual() ? currentNode.asText() : currentNode.toString();
            
            // Remove quotes if it's a number represented as text
            if (currentNode.isNumber()) {
                value = String.valueOf(currentNode.asLong());
            }
            
            logger.info("Value extracted successfully. Key: {}, Value: {}", key, value);
            logger.trace("Exiting getValueFromJSON() method with success");
            return value;
        } catch (JsonParsingException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Exception occurred while extracting value from JSON. Key: {}", key, e);
            throw new JsonParsingException("Failed to extract value from JSON for key: " + key, e);
        }
    }
}
