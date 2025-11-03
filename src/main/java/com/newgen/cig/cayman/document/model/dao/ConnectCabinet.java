package com.newgen.cig.cayman.document.model.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

    /**
     * Manages establishing and closing sessions with the OmniDocs cabinet.
     *
     * <p>Builds XML for connect/disconnect and delegates execution to
     * {@link com.newgen.cig.cayman.document.implementation.Operations}.</p>
     *
     * <h3>Responsibilities:</h3>
     * <ul>
     *   <li>Generate connect/disconnect XML</li>
     *   <li>Execute XML and store session id in {@link GlobalSessionService}</li>
     * </ul>
     *
     * @author Tarun Vishwakarma
     * @since 2025
     */
@Component
public class ConnectCabinet {
    private static final Logger logger = LoggerFactory.getLogger(ConnectCabinet.class);

    @Autowired
    private GlobalSessionService sessionService;

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private CabinetProperties properties;

    @Value("${newgen.cayman.connect.cabinet.username}")
    private String username;
    @Value("${newgen.cayman.connect.cabinet.password}")
    private String password;
    @Value("${newgen.cayman.connect.cabinet.cabinetName}")
    private String cabinetName;
    @Value("${newgen.cayman.connect.cabinet.userExists}")
    private String userExists;

    private ObjectMapper objectMapper = new ObjectMapper();

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCabinetName() {
        return cabinetName;
    }

    public String getUserExists() {
        return userExists;
    }

    public void setSessionId(String sessionId){
        logger.trace("Setting session ID: {}", sessionId);
        sessionService.setSessionId(sessionId);
        logger.debug("Session ID set successfully");
    }

    public String getSessionId(){
        logger.trace("Getting session ID");
        String sessionId = sessionService.getSessionId();
        logger.debug("Session ID retrieved: {}", sessionId);
        return sessionId;
    }

    @PostConstruct
    public void init() {
        logger.info("ConnectCabinet component initialized");
        logger.debug("Cabinet configuration - Name: {}, Username: {}", cabinetName, username);
    }

    /**
     * Connects to the cabinet and returns raw JSON response.
     *
     * @return JSON response from connect operation
     * @throws com.newgen.cig.cayman.document.exception.CabinetConnectionException on errors
     */
    public String connect() {
        logger.trace("Entering connect() method");
        logger.info("Connecting to cabinet. CabinetName: {}, Username: {}", cabinetName, username);
        
        if (cabinetName == null || cabinetName.trim().isEmpty()) {
            logger.error("Cabinet name is null or empty");
            throw new com.newgen.cig.cayman.document.exception.CabinetConnectionException("Cabinet name is not configured");
        }
        
        if (username == null || username.trim().isEmpty()) {
            logger.error("Username is null or empty");
            throw new com.newgen.cig.cayman.document.exception.CabinetConnectionException("Username is not configured");
        }

        if (password == null || password.trim().isEmpty()) {
            logger.error("Password is null or empty");
            throw new com.newgen.cig.cayman.document.exception.CabinetConnectionException("Password is not configured");
        }

        try {
            // Build the same URL as fetchDoc
            String url = properties.getSiteURL()
                    + properties.getSiteURI()
                    + properties.getRequestJson();
            logger.debug("Constructed API URL: {}", url);

            // Build request JSON structure
            Map<String, Object> ngoConnectCabinetInput = new HashMap<>();
            ngoConnectCabinetInput.put("Option", "NGOConnectCabinet");
            ngoConnectCabinetInput.put("UserExist", this.userExists);
            ngoConnectCabinetInput.put("cabinetName", this.cabinetName);
            ngoConnectCabinetInput.put("UserName", this.username);
            ngoConnectCabinetInput.put("UserPassword", this.password);
            ngoConnectCabinetInput.put("locale", "en_us");

            Map<String, Object> inputData = new HashMap<>();
            inputData.put("NGOConnectCabinet_Input", ngoConnectCabinetInput);

            Map<String, Object> ngoExecuteAPOBDO = new HashMap<>();
            ngoExecuteAPOBDO.put("inputData", inputData);
            ngoExecuteAPOBDO.put("base64Encoded", "N");
            ngoExecuteAPOBDO.put("locale", "en_us");
            ngoExecuteAPOBDO.put("authToken", "");
            ngoExecuteAPOBDO.put("authTokenType", "");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("NGOExecuteAPOBDO", ngoExecuteAPOBDO);

            logger.debug("Request body created for cabinet connection");
            logger.trace("Sending POST request to URL: {}", url);

            ResponseEntity<String> response = restTemplate.postForEntity(url, requestBody, String.class);
            
            logger.info("API call completed successfully. HTTP Status: {}", response.getStatusCode());
            logger.debug("Response status code: {}", response.getStatusCode());

            String responseBody = response.getBody();
            
            if (responseBody == null || responseBody.trim().isEmpty()) {
                logger.error("Received null or empty response body");
                throw new com.newgen.cig.cayman.document.exception.CabinetConnectionException("Received empty response from cabinet service");
            }
            
            logger.debug("Response body received. Length: {}", responseBody.length());
            
            // Parse response to extract UserDBId (session ID)
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            JsonNode responseNode = jsonNode.get("NGOExecuteAPIResponseBDO");
            
            if (responseNode == null) {
                logger.error("Invalid response format: Missing NGOExecuteAPIResponseBDO");
                throw new com.newgen.cig.cayman.document.exception.CabinetConnectionException("Invalid response format");
            }
            
            String statusCode = responseNode.get("statusCode") != null ? 
                    responseNode.get("statusCode").asText() : "";
            
            if (!"0".equals(statusCode)) {
                logger.error("Connection failed with status code: {}", statusCode);
                throw new com.newgen.cig.cayman.document.exception.CabinetConnectionException(
                        "Failed to connect to cabinet. Status code: " + statusCode);
            }
            
            JsonNode outputData = responseNode.get("outputData");
            if (outputData != null) {
                JsonNode connectOutput = outputData.get("NGOConnectCabinet_Output");
                if (connectOutput != null) {
                    JsonNode userDbIdNode = connectOutput.get("UserDBId");
                    if (userDbIdNode != null) {
                        String userDbId = String.valueOf(userDbIdNode.asLong());
                        logger.info("Cabinet connected successfully. UserDBId: {}", userDbId);
                        setSessionId(userDbId);
                    }
                }
            }
            
            logger.info("Cabinet connected successfully. CabinetName: {}", cabinetName);
            logger.trace("Exiting connect() method with success");
            return responseBody;
            
        } catch (com.newgen.cig.cayman.document.exception.CabinetConnectionException e) {
            logger.error("CabinetConnectionException occurred while connecting to cabinet. CabinetName: {}", cabinetName, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected exception occurred while connecting to cabinet. CabinetName: {}", cabinetName, e);
            throw new com.newgen.cig.cayman.document.exception.CabinetConnectionException("Failed to connect to cabinet: " + e.getMessage(), e);
        }
    }
}
