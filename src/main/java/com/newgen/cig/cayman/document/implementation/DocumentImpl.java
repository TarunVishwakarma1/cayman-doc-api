package com.newgen.cig.cayman.document.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.cig.cayman.document.interfaces.DocumentInterface;
import com.newgen.cig.cayman.document.model.dao.*;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DocumentImpl implements DocumentInterface {

    private static final Logger LOG = Logger.getLogger(DocumentImpl.class);

    @Autowired
    private DocumentResponse docResponse;

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
            String documentResponse = docRes.getBody();
            JsonNode jNode = stringToJsonObject(documentResponse);

            if (jNode.get("NGOGetDocumentBDOResponse") == null) {
                LOG.error("Received null response body");
                throw new Exception("Error: No content received from the server.");
            }

            String statusCode = trimString(jNode.get("NGOGetDocumentBDOResponse").get("statusCode").toString());
            String message = trimString(jNode.get("NGOGetDocumentBDOResponse").get("message").toString());
            docResponse.setMessage(message);
            docResponse.setStatusCode(statusCode);
            if ("0".equals(statusCode)) {

                String ret = trimString(jNode.get("NGOGetDocumentBDOResponse").get("docContent").toString());
                String createdByAppName = trimString(jNode.get("NGOGetDocumentBDOResponse").get("createdByAppName").toString());
                String documentName = trimString(jNode.get("NGOGetDocumentBDOResponse").get("documentName").toString());
                String documentType = trimString(jNode.get("NGOGetDocumentBDOResponse").get("documentType").toString());
                String documentSize = trimString(jNode.get("NGOGetDocumentBDOResponse").get("documentSize").toString());

                docResponse.setDocContent(ret);
                docResponse.setCreatedByAppName(createdByAppName);
                docResponse.setDocumentName(documentName);
                docResponse.setDocumentType(documentType);
                docResponse.setDocumentSize(documentSize);

                LOG.debug("Document Content Base64: "+ret);
                return ret;
            }

            LOG.error("Error Occurred: " + documentResponse);
            throw new Exception("Error: " + message);

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

    private String trimString(String str){
        int len = str.length();
        return str.substring(1, len-1);
    }
}
