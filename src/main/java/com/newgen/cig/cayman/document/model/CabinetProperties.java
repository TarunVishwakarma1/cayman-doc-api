package com.newgen.cig.cayman.document.model;

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
}
