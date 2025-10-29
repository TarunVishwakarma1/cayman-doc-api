package com.newgen.cig.cayman.document.model.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CabinetProperties {

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

    public String getCabinetName() {

        return cabinetName;
    }

    public void setCabinetName(String cabinetName) {
        this.cabinetName = cabinetName;
    }

    public String getJtsIP() {
        return jtsIP;
    }

    public void setJtsIP(String jtsIP) {
        this.jtsIP = jtsIP;
    }

    public String getJtsPort() {
        return jtsPort;
    }

    public void setJtsPort(String jtsPort) {
        this.jtsPort = jtsPort;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
    }

    public String getSitePort() {
        return sitePort;
    }

    public void setSitePort(String sitePort) {
        this.sitePort = sitePort;
    }

    public String getSiteIP() {
        return siteIP;
    }

    public void setSiteIP(String siteIP) {
        this.siteIP = siteIP;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSiteURI() {
        return siteURI;
    }

    public void setSiteURI(String siteURI) {
        this.siteURI = siteURI;
    }

    public String getDocumentRequest() {
        return documentRequest;
    }

    public void setDocumentRequest(String documentRequest) {
        this.documentRequest = documentRequest;
    }

    public String getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }
}
