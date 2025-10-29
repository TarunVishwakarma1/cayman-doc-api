package com.newgen.cig.cayman.document.implementation;

import com.newgen.cig.cayman.document.model.dao.CabinetProperties;
import com.newgen.dmsapi.DMSCallBroker;
import com.newgen.dmsapi.DMSXmlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Operations {

    private static final Logger logger = LoggerFactory.getLogger(Operations.class);

    @Autowired
    private CabinetProperties cabinet;

    public String getValueFromXML(String xml, String key){
        logger.trace("Entering getValueFromXML() method. Key: {}", key);
        logger.debug("Parameters provided - XML length: {}, Key: {}", xml != null ? xml.length() : 0, key);
        try {
            logger.trace("Creating DMSXmlResponse from XML");
            DMSXmlResponse xmlResponse = new DMSXmlResponse(xml);
            logger.trace("Extracting value from XML for key: {}", key);
            String value = xmlResponse.getVal(key);
            logger.info("Value extracted successfully. Key: {}, Value length: {}", key, value != null ? value.length() : 0);
            logger.debug("Extracted value: {}", value);
            logger.trace("Exiting getValueFromXML() method with success");
            return value;
        } catch (Exception e) {
            logger.error("Exception occurred while extracting value from XML. Key: {}", key, e);
            throw e;
        }
    }

    public String executeXML(String xml) throws Exception {
        logger.trace("Entering executeXML() method");
        logger.info("Executing XML via DMS Call Broker");
        logger.debug("XML to be executed. Length: {}", xml != null ? xml.length() : 0);
        try {
            String jtsIP = cabinet.getJtsIP();
            String jtsPort = cabinet.getJtsPort();
            logger.debug("DMS Connection details - IP: {}, Port: {}", jtsIP, jtsPort);
            logger.trace("Calling DMSCallBroker.execute()");
            String outXML = DMSCallBroker.execute(xml, jtsIP, Integer.parseInt(jtsPort), 1);
            logger.info("XML executed successfully");
            logger.debug("XML response received. Length: {}", outXML != null ? outXML.length() : 0);
            logger.trace("Exiting executeXML() method with success");
            return outXML;
        }catch(Exception e){
            logger.error("Exception occurred while executing XML: {}", e.getMessage(), e);
            logger.error("XML that failed: {}", xml);
            logger.error("Stack trace: ", e);
            throw e;
        }
    }


}
