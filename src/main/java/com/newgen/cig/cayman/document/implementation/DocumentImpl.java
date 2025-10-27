package com.newgen.cig.cayman.document.implementation;

import ISPack.CImageServer;
import ISPack.CPISDocumentTxn;
import ISPack.ISUtil.JPISException;
import Jdts.DataObject.JPDBString;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.cig.cayman.document.interfaces.DocumentInterface;
import com.newgen.cig.cayman.document.model.*;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class DocumentImpl implements DocumentInterface {

    private static final Logger LOG = Logger.getLogger(DocumentImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ConnectCabinet cabinet;

    @Autowired
    private CabinetProperties properties;

    @Autowired
    private GlobalSessionService sessionService;

    @Autowired
    private Operations operations;

    @Override
    public String connectCabinet() throws Exception {
        return cabinet.connect();
    }

    @Override
    public String download(String docIndex) throws Exception {
        String xml = downloadDocXML(docIndex);
        return operations.executeXML(xml);
    }

    @Override
    public String fetchDoc(String docIndex) throws Exception {
        String url = properties.getSite() + properties.getSiteIP() + ":" + properties.getSitePort() + properties.getSiteURI() + properties.getDocumentRequest();

        // Assuming DocumentRequest.NGOGetDocumentBDO is a static inner class, or you have proper initialization for it
        DocumentRequest.NGOGetDocumentBDO bdo = new DocumentRequest.NGOGetDocumentBDO();
        bdo.setCabinetName(properties.getCabinetName());
        bdo.setUserName("");
        bdo.setUserPassword("");
        bdo.setUserDBId(sessionService.getSessionId());
        bdo.setDocIndex(docIndex);
        bdo.setAuthToken("");
        bdo.setAuthTokenType("");
        bdo.setLocale("en_us");

        LOG.info("//---------- Executing fetchDoc API ----------//");
        LOG.debug("Executing URL: " + url + ", with body: " + bdo);

        try {
            ResponseEntity<String> docRes = restTemplate.postForEntity(url, bdo, String.class);

            LOG.info("//----------- Executed Successfully ----------//");
            LOG.debug("Response Status: " + docRes.getStatusCode() + ", Response Body: " + docRes.getBody());

            String documentResponse = docRes.getBody();
            LOG.debug("Document Response: " + documentResponse);

            JsonNode jNode = stringToJsonObject(documentResponse);

            LOG.debug("Converted to JsonNode: "+jNode.toString());

            // Check if the response body is not null
            if (jNode.get("NGOGetDocumentBDOResponse") == null) {
                LOG.error("Received null response body");
                throw new Exception("Error: No content received from the server.");
            }

            String statusCode = jNode.get("NGOGetDocumentBDOResponse").get("statusCode").toString();

            if ("\"0\"".equals(statusCode)) {
                int length = jNode.get("NGOGetDocumentBDOResponse").get("docContent").toString().length();
                String ret = jNode.get("NGOGetDocumentBDOResponse").get("docContent").toString().substring(1,length-1);
                LOG.debug("Document Content Base64: "+ret);
                return ret;
            }

            LOG.error("Error Occurred: " + documentResponse);
            throw new Exception("Error: " + jNode.get("NGOGetDocumentBDOResponse").get("message"));

        } catch (Exception e) {
            LOG.error("Exception occurred while fetching document: " + e.getMessage(), e);
            throw new Exception("Failed to fetch document", e);
        }

    }

    private String downloadDocXML(String docIndex){
        String xml = "<? xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
                "<NGOGetDocumentBDO>"+
                "<cabinetName>"+properties.getCabinetName()+"</cabinetName>"+
                "<docIndex>"+docIndex+"</docIndex>"+
                "<siteId>"+properties.getSiteId()+"</siteId>"+
                "<volumeId>"+properties.getVolumeId()+"</volumeId>"+
                "<userDBId>"+sessionService.getSessionId()+"</userDBId>"+
                "<locale>en_us</locale>"+
                "</NGOGetDocumentBDO>";
        LOG.info("Document Download XML: "+ xml);
        LOG.debug("Document Download XML: "+ xml);
        return xml;
    }

    public JsonNode stringToJsonObject(String jsonString) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(jsonString);
    }
}
