package com.newgen.cig.cayman.document.model.dao;

import com.newgen.cig.cayman.document.implementation.Operations;
import com.newgen.dmsapi.DMSInputXml;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    public String connect() throws Exception {
        logger.trace("Entering connect() method");
        logger.info("Connecting to cabinet. CabinetName: {}, Username: {}", cabinetName, username);
        try {
            logger.debug("Generating connect cabinet XML");
            String inputXML = objInp.getConnectCabinetXml(this.cabinetName, this.username, this.password, null, this.userExists, "en_us");
            logger.debug("Connect XML generated. Length: {}", inputXML != null ? inputXML.length() : 0);
            logger.trace("Executing XML to connect to cabinet");
            String outXML = operations.executeXML(inputXML);
            logger.info("Cabinet connected successfully. CabinetName: {}", cabinetName);
            logger.debug("Connection response received. Length: {}", outXML != null ? outXML.length() : 0);
            return outXML;
        } catch (Exception e) {
            logger.error("Exception occurred while connecting to cabinet. CabinetName: {}", cabinetName, e);
            throw e;
        }
    }

    public void disconnect() throws  Exception {
        logger.trace("Entering disconnect() method");
        logger.info("Disconnecting from cabinet. CabinetName: {}, SessionId: {}", cabinetName, sessionId);
        try {
            logger.debug("Generating disconnect cabinet XML");
            String inpXML = objInp.getDisconnectCabinetXml(cabinetName, this.sessionId);
            logger.debug("Disconnect XML generated. Length: {}", inpXML != null ? inpXML.length() : 0);
            logger.trace("Executing XML to disconnect from cabinet");
            operations.executeXML(inpXML);
            logger.info("Cabinet disconnected successfully. CabinetName: {}", cabinetName);
            logger.trace("Exiting disconnect() method with success");
        } catch (Exception e) {
            logger.error("Exception occurred while disconnecting from cabinet. CabinetName: {}", cabinetName, e);
            throw e;
        }
    }
}
