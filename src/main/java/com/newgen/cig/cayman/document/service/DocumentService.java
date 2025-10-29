package com.newgen.cig.cayman.document.service;

import com.newgen.cig.cayman.document.implementation.Operations;
import com.newgen.cig.cayman.document.interfaces.DocumentInterface;
import com.newgen.cig.cayman.document.model.dao.GlobalSessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    @Autowired
    private DocumentInterface doc;

    @Autowired
    private GlobalSessionService globalSessionService;

    @Autowired
    private Operations operations;

    public String getSessionId() {
        logger.trace("Entering getSessionId() method");
        logger.info("Attempting to get session ID");
        logger.debug("Connecting to cabinet");
        String response = doc.connectCabinet();
        logger.debug("Cabinet connection response received. Response length: {}", response != null ? response.length() : 0);
        
        logger.trace("Extracting UserDBId from XML response");
        String sessionId = operations.getValueFromXML(response, "UserDBId");
        logger.debug("Session ID extracted from XML. SessionId: {}", sessionId);
        
        globalSessionService.setSessionId(sessionId);
        logger.info("Session ID retrieved and stored successfully. SessionId length: {}", sessionId != null ? sessionId.length() : 0);
        logger.trace("Exiting getSessionId() method with success");
        return sessionId;
    }


    public String fetchDocumentBase64(String docIndex) {
        logger.trace("Entering fetchDocumentBase64() method with docIndex: {}", docIndex);
        logger.info("Fetching document as base64 string. DocIndex: {}", docIndex);
        logger.debug("Calling doc.fetchDoc() for docIndex: {}", docIndex);
        String base64Document = doc.fetchDoc(docIndex);
        logger.info("Document fetched successfully as base64. DocIndex: {}, Base64 length: {}", 
                docIndex, base64Document != null ? base64Document.length() : 0);
        logger.debug("Base64 document preview: {}", 
                base64Document != null && base64Document.length() > 50 ? 
                base64Document.substring(0, 50) + "..." : base64Document);
        logger.trace("Exiting fetchDocumentBase64() method with success");
        return base64Document;
    }

    public byte[] fetchDocBytes(String docIndex) {
        logger.trace("Entering fetchDocBytes() method with docIndex: {}", docIndex);
        logger.info("Fetching document as byte array. DocIndex: {}", docIndex);
        logger.debug("Fetching document as base64 first for docIndex: {}", docIndex);
        String base64Pdf = doc.fetchDoc(docIndex);
        logger.debug("Base64 document received. Length: {}", base64Pdf != null ? base64Pdf.length() : 0);
        
        logger.trace("Decoding base64 to byte array");
        byte[] documentBytes = Base64.getDecoder().decode(base64Pdf);
        logger.info("Document decoded successfully. DocIndex: {}, Byte array size: {} bytes", 
                docIndex, documentBytes != null ? documentBytes.length : 0);
        logger.debug("Document byte array preview: {} bytes", documentBytes != null ? documentBytes.length : 0);
        logger.trace("Exiting fetchDocBytes() method with success");
        return documentBytes;
    }
}