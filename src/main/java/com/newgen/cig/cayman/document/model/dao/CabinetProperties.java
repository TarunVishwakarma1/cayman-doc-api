package com.newgen.cig.cayman.document.model.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class CabinetProperties {

    private static final Logger logger = LoggerFactory.getLogger(CabinetProperties.class);

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
    @Value("${newgen.cayman.connect.cabinet.siteId}")
    private String siteId;
    @Value("${newgen.cayman.connect.cabinet.volumeId}")
    private String volumeId;
    @Value("${newgen.cayman.connect.cabinet.sitePort}")
    private String sitePort;
    @Value("${newgen.cayman.connect.cabinet.siteIP}")
    private String siteIP;
    @Value("${newgen.cayman.connect.cabinet.site}")
    private String site;
    @Value("${newgen.cayman.connect.cabinet.siteURI}")
    private String siteURI;
    @Value("${newgen.cayman.connect.cabinet.site.document.request}")
    private String documentRequest;
    @Value("${newgen.cayman.connect.cabinet.site.api.request.json}")
    private String requestJson;

    @PostConstruct
    public void init() {
        logger.info("CabinetProperties component initialized");
        logger.debug("Cabinet configuration - Name: {}, Username: {}, JTS IP: {}, JTS Port: {}, Site: {}, Site IP: {}, Site Port: {}", 
                cabinetName, username, jtsIP, jtsPort, site, siteIP, sitePort);
        logger.trace("All cabinet properties loaded from configuration");
    }

    public String getUsername() {
        logger.trace("Getting cabinet username");
        return username;
    }

    public void setUsername(String username) {
        logger.trace("Setting cabinet username");
        this.username = username;
    }

    public String getPassword() {
        logger.trace("Getting cabinet password");
        return password;
    }

    public void setPassword(String password) {
        logger.trace("Setting cabinet password");
        this.password = password;
    }

    public String getCabinetName() {
        logger.trace("Getting cabinet name");
        return cabinetName;
    }

    public void setCabinetName(String cabinetName) {
        logger.trace("Setting cabinet name: {}", cabinetName);
        logger.debug("Previous cabinet name: {}, New cabinet name: {}", this.cabinetName, cabinetName);
        this.cabinetName = cabinetName;
    }

    public String getJtsIP() {
        logger.trace("Getting JTS IP");
        return jtsIP;
    }

    public void setJtsIP(String jtsIP) {
        logger.trace("Setting JTS IP: {}", jtsIP != null ? "***" : "null");
        logger.debug("Previous JTS IP: {}, New JTS IP: {}", this.jtsIP != null ? "***" : "null", jtsIP != null ? "***" : "null");
        this.jtsIP = jtsIP;
    }

    public String getJtsPort() {
        logger.trace("Getting JTS Port");
        return jtsPort;
    }

    public void setJtsPort(String jtsPort) {
        logger.trace("Setting JTS Port: {}", jtsPort);
        logger.debug("Previous JTS Port: {}, New JTS Port: {}", this.jtsPort, jtsPort);
        this.jtsPort = jtsPort;
    }

    public String getSiteId() {
        logger.trace("Getting site ID");
        return siteId;
    }

    public void setSiteId(String siteId) {
        logger.trace("Setting site ID: {}", siteId);
        this.siteId = siteId;
    }

    public String getVolumeId() {
        logger.trace("Getting volume ID");
        return volumeId;
    }

    public void setVolumeId(String volumeId) {
        logger.trace("Setting volume ID: {}", volumeId);
        this.volumeId = volumeId;
    }

    public String getSitePort() {
        logger.trace("Getting site port");
        return sitePort;
    }

    public void setSitePort(String sitePort) {
        logger.trace("Setting site port: {}", sitePort);
        this.sitePort = sitePort;
    }

    public String getSiteIP() {
        logger.trace("Getting site IP");
        return siteIP;
    }

    public void setSiteIP(String siteIP) {
        logger.trace("Setting site IP: {}", siteIP != null ? "***" : "null");
        this.siteIP = siteIP;
    }

    public String getSite() {
        logger.trace("Getting site");
        return site;
    }

    public void setSite(String site) {
        logger.trace("Setting site: {}", site);
        this.site = site;
    }

    public String getSiteURI() {
        logger.trace("Getting site URI");
        return siteURI;
    }

    public void setSiteURI(String siteURI) {
        logger.trace("Setting site URI: {}", siteURI);
        this.siteURI = siteURI;
    }

    public String getDocumentRequest() {
        logger.trace("Getting document request path");
        return documentRequest;
    }

    public void setDocumentRequest(String documentRequest) {
        logger.trace("Setting document request path: {}", documentRequest);
        this.documentRequest = documentRequest;
    }

    public String getRequestJson() {
        logger.trace("Getting request JSON path");
        return requestJson;
    }

    public void setRequestJson(String requestJson) {
        logger.trace("Setting request JSON path: {}", requestJson);
        this.requestJson = requestJson;
    }
}
