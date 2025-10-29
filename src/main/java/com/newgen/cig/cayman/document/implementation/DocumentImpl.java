package com.newgen.cig.cayman.document.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newgen.cig.cayman.document.exception.*;
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
    public String connectCabinet() {
        logger.trace("Entering connectCabinet() method");
        logger.info("Connecting to cabinet");
        try {
            logger.debug("Calling cabinet.connect()");
            String response = cabinet.connect();
            
            if (response == null || response.trim().isEmpty()) {
                logger.error("Cabinet connection returned null or empty response");
                throw new CabinetConnectionException("Cabinet connection returned null or empty response");
            }
            
            logger.info("Cabinet connected successfully. Response length: {}", response.length());
            logger.debug("Cabinet connection response: {}", response);
            logger.trace("Exiting connectCabinet() method with success");
            return response;
        } catch (CabinetConnectionException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Exception occurred while connecting to cabinet: {}", e.getMessage(), e);
            throw new CabinetConnectionException("Failed to connect to cabinet", e);
        }
    }

    @Override
    public String fetchDoc(String docIndex) {
        logger.trace("Entering fetchDoc() method with docIndex: {}", docIndex);
        logger.info("Fetching document. DocIndex: {}", docIndex);
        
        // Validate docIndex
        if (docIndex == null || docIndex.trim().isEmpty()) {
            logger.error("Document index is null or empty");
            throw new InvalidParameterException("Document index cannot be null or empty");
        }
        
        String url = properties.getSite() + properties.getSiteIP() + ":" + properties.getSitePort() + 
                     properties.getSiteURI() + properties.getDocumentRequest();
        logger.debug("Constructed API URL: {}", url);

        DocumentRequest.NGOGetDocumentBDO bdo = new DocumentRequest.NGOGetDocumentBDO();
        bdo.setCabinetName(properties.getCabinetName());
        bdo.setUserName("");
        bdo.setUserPassword("");
        
        String sessionId = sessionService.getSessionId();
        if (sessionId == null || sessionId.trim().isEmpty()) {
            logger.error("Session ID is null or empty");
            throw new SessionExpiredException("Session ID is not available. Please login again");
        }
        
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
            
            if (documentResponse == null || documentResponse.trim().isEmpty()) {
                logger.error("Received null or empty response body");
                throw new ExternalServiceException("Received empty response from document service");
            }
            
            logger.debug("Response body received. Length: {}", documentResponse.length());
            logger.trace("Parsing JSON response");
            JsonNode jNode = stringToJsonObject(documentResponse);

            if (jNode.get("NGOGetDocumentBDOResponse") == null) {
                logger.error("Received null NGOGetDocumentBDOResponse in JSON. Response body: {}", documentResponse);
                throw new ExternalServiceException("Invalid response format: Missing NGOGetDocumentBDOResponse");
            }

            JsonNode responseNode = jNode.get("NGOGetDocumentBDOResponse");
            
            if (responseNode.get("statusCode") == null || responseNode.get("message") == null) {
                logger.error("Missing required fields in response");
                throw new ExternalServiceException("Invalid response format: Missing statusCode or message");
            }
            
            String statusCode = trimString(responseNode.get("statusCode").toString());
            String message = trimString(responseNode.get("message").toString());
            docResponse.setMessage(message);
            docResponse.setStatusCode(statusCode);
        
            logger.debug("Response statusCode: {}, message: {}", statusCode, message);
        
            if ("0".equals(statusCode)) {
                logger.info("Document fetched successfully. DocIndex: {}", docIndex);
                
                if (responseNode.get("docContent") == null) {
                    logger.error("Document content is missing in response");
                    throw new DocumentNotFoundException("Document content not found for docIndex: " + docIndex);
                }
                
                String ret = trimString(responseNode.get("docContent").toString());
                String createdByAppName = responseNode.get("createdByAppName") != null ? 
                        trimString(responseNode.get("createdByAppName").toString()) : "";
                String documentName = responseNode.get("documentName") != null ? 
                        trimString(responseNode.get("documentName").toString()) : "";
                String documentType = responseNode.get("documentType") != null ? 
                        trimString(responseNode.get("documentType").toString()) : "";
                String documentSize = responseNode.get("documentSize") != null ? 
                        trimString(responseNode.get("documentSize").toString()) : "";

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

            logger.error("Error occurred in API response. StatusCode: {}, Message: {}", statusCode, message);
            
            // Map specific error codes to exceptions
            if ("404".equals(statusCode) || message.toLowerCase().contains("not found")) {
                throw new DocumentNotFoundException("Document not found with docIndex: " + docIndex + ". " + message);
            } else if ("401".equals(statusCode) || message.toLowerCase().contains("unauthorized")) {
                throw new SessionExpiredException("Session expired or unauthorized: " + message);
            } else if ("429".equals(statusCode) || message.toLowerCase().contains("too many")) {
                throw new TooManyRequestsException("Too many requests: " + message);
            }
            
            throw new ExternalServiceException("Failed to fetch document: " + message);

        } catch (DocumentNotFoundException | SessionExpiredException | TooManyRequestsException | 
                 InvalidParameterException | ExternalServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Exception occurred while fetching document. DocIndex: {}", docIndex, e);
            logger.error("Error message: {}, Cause: {}", e.getMessage(), e.getCause());
            throw new ExternalServiceException("Failed to fetch document for docIndex: " + docIndex, e);
        }
    }

    public JsonNode stringToJsonObject(String jsonString) {
        logger.trace("Entering stringToJsonObject() method. JSON string length: {}", jsonString != null ? jsonString.length() : 0);
        
        if (jsonString == null || jsonString.trim().isEmpty()) {
            logger.error("JSON string is null or empty");
            throw new JsonParsingException("JSON string cannot be null or empty");
        }
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            logger.debug("JSON string parsed successfully");
            logger.trace("Exiting stringToJsonObject() method with success");
            return jsonNode;
        } catch (JsonParsingException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Exception occurred while parsing JSON string: {}", e.getMessage(), e);
            logger.error("JSON string that failed to parse: {}", jsonString);
            throw new JsonParsingException("Failed to parse JSON string", e);
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
