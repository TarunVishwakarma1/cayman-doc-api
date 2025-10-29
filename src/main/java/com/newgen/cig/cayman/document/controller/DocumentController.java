package com.newgen.cig.cayman.document.controller;

import com.newgen.cig.cayman.document.model.dao.DocumentResponse;
import com.newgen.cig.cayman.document.model.enums.DocumentType;
import com.newgen.cig.cayman.document.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private DocumentResponse documentResponse;

    @Autowired
    private DocumentService documentService;

    @GetMapping("")
    public String hello(){
        logger.trace("Entering hello() method");
        logger.info("Health check endpoint accessed");
        String response = "<h1>Hello, World!<h1>";
        logger.debug("Returning hello response: {}", response);
        logger.trace("Exiting hello() method");
        return response;
    }

    @GetMapping("/sessionId")
    public ResponseEntity<?> sessionId() {
        logger.trace("Entering sessionId() method");
        logger.info("Request received to get session ID");
        try{
            logger.debug("Calling documentService.getSessionId()");
            String sessionId = documentService.getSessionId();
            logger.info("Session ID retrieved successfully. SessionId length: {}", sessionId != null ? sessionId.length() : 0);
            logger.debug("Returning session ID with status OK");
            logger.trace("Exiting sessionId() method with success");
            return new ResponseEntity<>(sessionId, HttpStatus.OK);
        }catch(Exception e){
            logger.error("Exception occurred while retrieving session ID: {}", e.getMessage(), e);
            logger.error("Stack trace: ", e);
            return new ResponseEntity<>("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{docIndex}")
    public ResponseEntity<?> downloadDocument(@PathVariable String docIndex){
        logger.trace("Entering downloadDocument() method with docIndex: {}", docIndex);
        logger.info("Request received to download document. DocIndex: {}", docIndex);
        try{
            logger.debug("Fetching document bytes for docIndex: {}", docIndex);
            byte[] body = documentService.fetchDocBytes(docIndex);
            logger.debug("Document bytes fetched successfully. Document size: {} bytes", body != null ? body.length : 0);
            
            String documentName = documentResponse.getDocumentName();
            String createdByAppName = documentResponse.getCreatedByAppName();
            String filename = documentName + "." + createdByAppName;
            MediaType contentType = MediaType.valueOf(
                    DocumentType.fromExtension(createdByAppName).getContentType());
            
            logger.info("Preparing download response. Filename: {}, ContentType: {}", filename, contentType);
            logger.debug("Content-Disposition header: attachment; filename=\"{}\"", filename);
            logger.trace("Exiting downloadDocument() method with success");
            
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(contentType)
                    .body(body);
        }catch (Exception e) {
            logger.error("Exception occurred while downloading document. DocIndex: {}", docIndex, e);
            logger.error("Error details: {}", e.getMessage(), e);
            return new ResponseEntity<>("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fetchDoc/{base64}/{docIndex}")
    public ResponseEntity<?> fetchDocument(@PathVariable String base64, @PathVariable String docIndex){
        logger.trace("Entering fetchDocument() method with base64: {}, docIndex: {}", base64, docIndex);
        logger.info("Request received to fetch document. Format: {}, DocIndex: {}", base64, docIndex);
        Object body;
        try{
            if("base64".equals(base64)) {
                logger.debug("Fetching document as base64 string for docIndex: {}", docIndex);
                body = documentService.fetchDocumentBase64(docIndex);
                logger.debug("Document fetched as base64. Response type: {}", body != null ? body.getClass().getSimpleName() : "null");
            }else{
                logger.debug("Fetching document as bytes for docIndex: {}", docIndex);
                body = documentService.fetchDocBytes(docIndex);
                logger.debug("Document fetched as bytes. Size: {} bytes", body != null && body instanceof byte[] ? ((byte[]) body).length : 0);
            }
            
            String documentName = documentResponse.getDocumentName();
            String createdByAppName = documentResponse.getCreatedByAppName();
            String filename = documentName + "." + createdByAppName;
            MediaType contentType = MediaType.valueOf(
                    DocumentType.fromExtension(createdByAppName).getContentType());
            
            logger.info("Preparing inline response. Filename: {}, ContentType: {}", filename, contentType);
            logger.debug("Content-Disposition header: inline; filename=\"{}\"", filename);
            logger.trace("Exiting fetchDocument() method with success");
            
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .contentType(contentType)
                    .body(body);
        }catch (Exception e) {
            logger.error("Exception occurred while fetching document. Format: {}, DocIndex: {}", base64, docIndex, e);
            logger.error("Error details: {}", e.getMessage(), e);
            return new ResponseEntity<>("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
