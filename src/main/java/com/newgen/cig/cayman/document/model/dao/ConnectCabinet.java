package com.newgen.cig.cayman.document.model.dao;

import com.newgen.cig.cayman.document.implementation.Operations;
import com.newgen.dmsapi.DMSInputXml;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    private Operations operations;

    @Value("${newgen.cayman.connect.cabinet.username}")
    private String username;
    @Value("${newgen.cayman.connect.cabinet.password}")
    private String password;
    @Value("${newgen.cayman.connect.cabinet.cabinetName}")
    private String cabinetName;
    @Value("${newgen.cayman.connect.cabinet.jtsIP}")
    private String jtsIP;
    @Value("${newgen.cayman.connect.cabinet.jtsPort}")
    private String jtsPort;
    @Value("${newgen.cayman.connect.cabinet.mainGroupIndex}")
    private String mainGroupIndex;
    @Value("${newgen.cayman.connect.cabinet.userExists}")
    private String userExists;
    private String sessionId;

    // Getters and Setters
    public String getUsername() {
        return username;

    }

    public String getPassword() {
        return password;
    }

    public String getCabinetName() {
        return cabinetName;
    }

    public String getJtsIP() {
        return jtsIP;
    }

    public String getJtsPort() {
        return jtsPort;
    }

    public String getMainGroupIndex() {
        return mainGroupIndex;
    }
    public String getUserExists() {
        return userExists;
    }

    public void setSessionId(String sessionId){
        logger.trace("Setting session ID: {}", sessionId);
        this.sessionId = sessionId;
        sessionService.setSessionId(sessionId);
        logger.debug("Session ID set successfully");
    }

    public String getSessionId(){
        logger.trace("Getting session ID");
        String sessionId = sessionService.getSessionId();
        logger.debug("Session ID retrieved: {}", sessionId);
        return sessionId;
    }

    DMSInputXml objInp = new DMSInputXml();

    @PostConstruct
    public void init() {
        logger.info("ConnectCabinet component initialized");
        logger.debug("Cabinet configuration - Name: {}, IP: {}, Port: {}, Username: {}", 
                cabinetName, jtsIP, jtsPort, username);
    }

    /**
     * Connects to the cabinet and returns raw XML response.
     *
     * @return XML response from connect operation
     * @throws com.newgen.cig.cayman.document.exception.CabinetConnectionException on errors
     */
    public String connect() {
        logger.trace("Entering connect() method");
        logger.info("Connecting to cabinet. CabinetName: {}, Username: {}, JTS IP: {}, JTS Port: {}", 
                cabinetName, username, jtsIP, jtsPort);
        
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

        if (jtsIP == null || jtsIP.trim().isEmpty()) {
            logger.error("JTS IP is null or empty");
            throw new com.newgen.cig.cayman.document.exception.CabinetConnectionException("JTS IP is not configured");
        }
        
        try {
            logger.debug("Generating connect cabinet XML");
            String inputXML = objInp.getConnectCabinetXml(this.cabinetName, this.username, this.password, null, this.userExists, "en_us");
            logger.debug("Connect XML generated. Length: {}", inputXML != null ? inputXML.length() : 0);
            logger.trace("Executing XML to connect to cabinet via DMS Call Broker");
            String outXML = operations.executeXML(inputXML);
            
            if (outXML == null || outXML.trim().isEmpty()) {
                logger.error("Connection response is null or empty");
                throw new com.newgen.cig.cayman.document.exception.CabinetConnectionException("Connection response is empty");
            }
            
            logger.info("Cabinet connected successfully. CabinetName: {}, Response length: {}", cabinetName, outXML.length());
            logger.debug("Connection response received. Length: {}", outXML.length());
            logger.trace("Exiting connect() method with success");
            return outXML;
        } catch (com.newgen.cig.cayman.document.exception.CabinetConnectionException e) {
            logger.error("CabinetConnectionException occurred while connecting to cabinet. CabinetName: {}", cabinetName, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected exception occurred while connecting to cabinet. CabinetName: {}", cabinetName, e);
            throw new com.newgen.cig.cayman.document.exception.CabinetConnectionException("Failed to connect to cabinet: " + e.getMessage(), e);
        }
    }

    /**
     * Disconnects the current session from the cabinet.
     */
    public void disconnect() {
        logger.trace("Entering disconnect() method");
        logger.info("Disconnecting from cabinet. CabinetName: {}, SessionId: {}", cabinetName, 
                sessionId != null ? sessionId.substring(0, Math.min(10, sessionId.length())) + "..." : "null");
        
        if (sessionId == null || sessionId.trim().isEmpty()) {
            logger.warn("Session ID is null or empty, may not disconnect properly. CabinetName: {}", cabinetName);
        }
        
        try {
            logger.debug("Generating disconnect cabinet XML");
            String inpXML = objInp.getDisconnectCabinetXml(cabinetName, this.sessionId);
            logger.debug("Disconnect XML generated. Length: {}", inpXML != null ? inpXML.length() : 0);
            logger.trace("Executing XML to disconnect from cabinet via DMS Call Broker");
            String response = operations.executeXML(inpXML);
            logger.info("Cabinet disconnected successfully. CabinetName: {}, Response length: {}", 
                    cabinetName, response != null ? response.length() : 0);
            logger.debug("Disconnection response received");
            logger.trace("Exiting disconnect() method with success");
        } catch (com.newgen.cig.cayman.document.exception.CabinetConnectionException e) {
            logger.error("CabinetConnectionException occurred while disconnecting from cabinet. CabinetName: {}", cabinetName, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected exception occurred while disconnecting from cabinet. CabinetName: {}", cabinetName, e);
            throw new com.newgen.cig.cayman.document.exception.CabinetConnectionException("Failed to disconnect from cabinet: " + e.getMessage(), e);
        }
    }
}
