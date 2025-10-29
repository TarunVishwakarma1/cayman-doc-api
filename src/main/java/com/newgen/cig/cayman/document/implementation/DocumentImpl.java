package com.newgen.cig.cayman.document.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.cig.cayman.document.interfaces.DocumentInterface;
import com.newgen.cig.cayman.document.model.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DocumentImpl implements DocumentInterface {

    private static final Logger logger = LoggerFactory.getLogger(DocumentImpl.class);

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

    @Override
    public String connectCabinet() throws Exception {
        logger.trace("Entering connectCabinet() method");
        logger.info("Connecting to cabinet");
        try {
            logger.debug("Calling cabinet.connect()");
            String response = cabinet.connect();
            logger.info("Cabinet connected successfully. Response length: {}", response != null ? response.length() : 0);
            logger.debug("Cabinet connection response: {}", response);
            logger.trace("Exiting connectCabinet() method with success");
            return response;
        } catch (Exception e) {
            logger.error("Exception occurred while connecting to cabinet: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String fetchDoc(String docIndex) throws Exception {
        logger.trace("Entering fetchDoc() method with docIndex: {}", docIndex);
        logger.info("Fetching document. DocIndex: {}", docIndex);
        
        String url = properties.getSite() + properties.getSiteIP() + ":" + properties.getSitePort() + properties.getSiteURI() + properties.getDocumentRequest();
        logger.debug("Constructed API URL: {}", url);

        DocumentRequest.NGOGetDocumentBDO bdo = new DocumentRequest.NGOGetDocumentBDO();
        bdo.setCabinetName(properties.getCabinetName());
        bdo.setUserName("");
        bdo.setUserPassword("");
        String sessionId = sessionService.getSessionId();
        bdo.setUserDBId(sessionId);
        bdo.setDocIndex(docIndex);
        bdo.setAuthToken("");
        bdo.setAuthTokenType("");
        bdo.setLocale("en_us");

logger.trace("Request BDO created. CabinetName: {}, DocIndex: {}, SessionId: {}", 
                bdo.getCabinetName(), docIndex, sessionId);
        logger.info("Executing fetchDoc API. URL: {}, DocIndex: {}", url, docIndex);
        logger.debug("Request BDO details - CabinetName: {}, DocIndex: {}, UserDBId: {}", 
                bdo.getCabinetName(), bdo.getDocIndex(), bdo.getUserDBId());

        try {
            logger.debug("Sending POST request to URL: {}", url);
            ResponseEntity<String> docRes = restTemplate.postForEntity(url, bdo, String.class);
            logger.info("API call completed successfully. HTTP Status: {}", docRes.getStatusCode());
            logger.debug("Response status code: {}, Response headers: {}", 
                    docRes.getStatusCode(), docRes.getHeaders());

            String documentResponse = docRes.getBody();
            logger.debug("Response body received. Length: {}", documentResponse != null ? documentResponse.length() : 0);
            logger.trace("Parsing JSON response");
            JsonNode jNode = stringToJsonObject(documentResponse);

            if (jNode.get("NGOGetDocumentBDOResponse") == null) {
                logger.error("Received null NGOGetDocumentBDOResponse in JSON. Response body: {}", documentResponse);
                throw new Exception("Error: No content received from the server.");
            }

            String statusCode = trimString(jNode.get("NGOGetDocumentBDOResponse").get("statusCode").toString());
            String message = trimString(jNode.get("NGOGetDocumentBDOResponse").get("message").toString());
            docResponse.setMessage(message);
            docResponse.setStatusCode(statusCode);
            
            logger.debug("Response statusCode: {}, message: {}", statusCode, message);
            
            if ("0".equals(statusCode)) {
                logger.info("Document fetched successfully. DocIndex: {}", docIndex);
                
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

                logger.info("Document metadata extracted. Name: {}, Type: {}, Size: {}, AppName: {}", 
                        documentName, documentType, documentSize, createdByAppName);
                logger.debug("Document content length: {} characters", ret != null ? ret.length() : 0);
                logger.trace("Exiting fetchDoc() method with success");
                return ret;
            }

            logger.error("Error occurred in API response. StatusCode: {}, Message: {}, Full Response: {}", 
                    statusCode, message, documentResponse);
            throw new Exception("Error: " + message);

        } catch (Exception e) {
            logger.error("Exception occurred while fetching document. DocIndex: {}", docIndex, e);
            logger.error("Error message: {}, Cause: {}", e.getMessage(), e.getCause());
            throw new Exception("Failed to fetch document", e);
        }
    }


    public JsonNode stringToJsonObject(String jsonString) throws Exception {
        logger.trace("Entering stringToJsonObject() method. JSON string length: {}", jsonString != null ? jsonString.length() : 0);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            logger.debug("JSON string parsed successfully");
            logger.trace("Exiting stringToJsonObject() method with success");
            return jsonNode;
        } catch (Exception e) {
            logger.error("Exception occurred while parsing JSON string: {}", e.getMessage(), e);
            logger.error("JSON string that failed to parse: {}", jsonString);
            throw e;
        }
    }

    private String trimString(String str){
        logger.trace("Entering trimString() method. Original string length: {}", str != null ? str.length() : 0);
        if (str == null || str.length() < 2) {
            logger.warn("String too short to trim. Returning as is. String: {}", str);
            return str;
        }
        int len = str.length();
        String trimmed = str.substring(1, len-1);
        logger.debug("String trimmed. Original length: {}, Trimmed length: {}", len, trimmed.length());
        logger.trace("Exiting trimString() method");
        return trimmed;
    }
}
