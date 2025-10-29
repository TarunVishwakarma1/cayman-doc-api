package com.newgen.cig.cayman.document.model.dao;

import com.newgen.cig.cayman.document.implementation.Operations;
import com.newgen.dmsapi.DMSInputXml;
import jakarta.annotation.PostConstruct;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConnectCabinet {

    private static final Logger LOG = Logger.getLogger(ConnectCabinet.class);

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
        this.sessionId = sessionId;
        sessionService.setSessionId(sessionId);
    }

    public String getSessionId(){
        return sessionService.getSessionId();
    }

    @PostConstruct
    public void logProperties() {
        LOG.debug("username: " + this.username);
        LOG.debug("password: " + this.password);
        LOG.debug("cabinetName: " + this.cabinetName);
        LOG.debug("jtsIP: " + this.jtsIP);
        LOG.debug("jtsPort: " + this.jtsPort);
        LOG.debug("userExists: " + this.userExists);
    }

    DMSInputXml objInp = new DMSInputXml();

    public String connect() throws Exception {
        String inputXML = objInp.getConnectCabinetXml(this.cabinetName, this.username, this.password, null, this.userExists, "en_us");
        LOG.info("//--------- Connecting To Cabinet ---------//");
        String outXML = operations.executeXML(inputXML);
        LOG.info("//--------- Cabinet Connected Successfully ---------//");
        return outXML;
    }

    public void disconnect() throws  Exception {
        String inpXML = objInp.getDisconnectCabinetXml(cabinetName, this.sessionId);
        LOG.info("//--------- Disconnecting Cabinet ---------//");
        operations.executeXML(inpXML);
        LOG.info("//--------- Cabinet Disconnected Successfully ---------//");
    }
}
