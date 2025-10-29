package com.newgen.cig.cayman.document.implementation;

import com.newgen.cig.cayman.document.exception.XmlParsingException;
import com.newgen.cig.cayman.document.exception.CabinetConnectionException;
import com.newgen.cig.cayman.document.model.dao.CabinetProperties;
import com.newgen.dmsapi.DMSCallBroker;
import com.newgen.dmsapi.DMSXmlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * Extracts a value from XML by key.
     *
     * @param xml raw XML content
     * @param key target node key
     * @return extracted value
     * @throws XmlParsingException on invalid XML or missing key
     */
    public String getValueFromXML(String xml, String key){
        logger.trace("Entering getValueFromXML() method. Key: {}", key);
        logger.debug("Parameters provided - XML length: {}, Key: {}", xml != null ? xml.length() : 0, key);
        
        if (xml == null || xml.trim().isEmpty()) {
            logger.error("XML is null or empty");
            throw new XmlParsingException("XML content cannot be null or empty");
        }
        
        if (key == null || key.trim().isEmpty()) {
            logger.error("Key is null or empty");
            throw new XmlParsingException("Key cannot be null or empty");
        }
        
        try {
            logger.trace("Creating DMSXmlResponse from XML");
            DMSXmlResponse xmlResponse = new DMSXmlResponse(xml);
            logger.trace("Extracting value from XML for key: {}", key);
            String value = xmlResponse.getVal(key);
            logger.info("Value extracted successfully. Key: {}, Value length: {}", key, value != null ? value.length() : 0);
            logger.debug("Extracted value: {}", value);
            logger.trace("Exiting getValueFromXML() method with success");
            return value;
        } catch (XmlParsingException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Exception occurred while extracting value from XML. Key: {}", key, e);
            throw new XmlParsingException("Failed to extract value from XML for key: " + key, e);
        }
    }

    /**
     * Executes XML via DMS Call Broker using configured JTS IP/Port.
     *
     * @param xml request XML to send
     * @return response XML from DMS
     * @throws CabinetConnectionException on connectivity/config errors
     * @throws XmlParsingException when XML input is invalid
     */
    public String executeXML(String xml) {
        logger.trace("Entering executeXML() method");
        logger.info("Executing XML via DMS Call Broker");
        logger.debug("XML to be executed. Length: {}", xml != null ? xml.length() : 0);
        
        if (xml == null || xml.trim().isEmpty()) {
            logger.error("XML is null or empty");
            throw new XmlParsingException("XML content cannot be null or empty");
        }
        
        try {
            String jtsIP = cabinet.getJtsIP();
            String jtsPort = cabinet.getJtsPort();
            
            if (jtsIP == null || jtsIP.trim().isEmpty()) {
                logger.error("JTS IP is not configured");
                throw new CabinetConnectionException("JTS IP is not configured");
            }
            
            if (jtsPort == null || jtsPort.trim().isEmpty()) {
                logger.error("JTS Port is not configured");
                throw new CabinetConnectionException("JTS Port is not configured");
            }
            
            logger.debug("DMS Connection details - IP: {}, Port: {}", jtsIP, jtsPort);
            logger.trace("Calling DMSCallBroker.execute()");
            
            String outXML = DMSCallBroker.execute(xml, jtsIP, Integer.parseInt(jtsPort), 1);
            
            if (outXML == null || outXML.trim().isEmpty()) {
                logger.error("DMS returned empty or null response");
                throw new CabinetConnectionException("DMS returned empty or null response");
            }
            
            logger.info("XML executed successfully");
            logger.debug("XML response received. Length: {}", outXML.length());
            logger.trace("Exiting executeXML() method with success");
            return outXML;
        } catch (NumberFormatException e) {
            logger.error("Invalid JTS port format: {}", cabinet.getJtsPort(), e);
            throw new CabinetConnectionException("Invalid JTS port format: " + cabinet.getJtsPort(), e);
        } catch (CabinetConnectionException | XmlParsingException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Exception occurred while executing XML: {}", e.getMessage(), e);
            logger.error("XML that failed: {}", xml);
            throw new CabinetConnectionException("Failed to execute XML via DMS Call Broker", e);
        }
    }
}
