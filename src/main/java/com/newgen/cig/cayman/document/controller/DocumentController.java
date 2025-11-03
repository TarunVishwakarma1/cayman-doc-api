package com.newgen.cig.cayman.document.controller;

import com.newgen.cig.cayman.document.exception.InvalidParameterException;
import com.newgen.cig.cayman.document.exception.MissingParameterException;
import com.newgen.cig.cayman.document.model.dao.DocumentResponse;
import com.newgen.cig.cayman.document.model.dto.ApiResponse;
import com.newgen.cig.cayman.document.model.dto.ErrorResponse;
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

/**
 * REST controller exposing document-related endpoints.
 *
 * <p>Provides health check, session management, and document retrieval endpoints
 * for clients interacting with the Cayman Document API.</p>
 *
 * <h3>Endpoints:</h3>
 * <ul>
 *   <li><b>GET</b> {@code /api/v1} – Health check</li>
 *   <li><b>GET</b> {@code /api/v1/sessionId} – Get OmniDocs session id</li>
 *   <li><b>GET</b> {@code /api/v1/download/{docIndex}} – Download document as attachment</li>
 *   <li><b>GET</b> {@code /api/v1/fetchDoc/{base64}/{docIndex}} – Fetch document inline (base64 or bytes)</li>
 * </ul>
 *
 * @author Tarun Vishwakarma
 * @since 2025
 */
@RestController
@RequestMapping("/api/v1")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private DocumentResponse documentResponse;

    @Autowired
    private DocumentService documentService;


    /**
     * Simple health check endpoint.
     *
     * @return HTML greeting indicating the API is reachable
     */
    @GetMapping("")
    public String hello(){
        logger.trace("Entering hello() method");
        logger.info("Health check endpoint accessed");
        String response = "<h1>Hello, World!<h1>";
        logger.debug("Returning hello response: {}", response);
        logger.trace("Exiting hello() method");
        return response;
    }


    /**
     * Retrieves and returns a new session id from OmniDocs.
     *
     * @return {@link ResponseEntity} containing the session id as plain text
     */
    @GetMapping("/sessionId")
    public ResponseEntity<?> sessionId() {
        logger.trace("Entering sessionId() method");
        logger.info("Request received to get session ID");
        logger.debug("Calling documentService.getSessionId()");
        String sessionId = documentService.getSessionId();
        logger.info("Session ID retrieved successfully. SessionId length: {}", sessionId != null ? sessionId.length() : 0);
        logger.debug("Returning session ID with status OK");
        logger.trace("Exiting sessionId() method with success");
        return ResponseEntity.ok(sessionId);
    }


    /**
     * Downloads a document from OmniDocs as a file attachment.
     *
     * @param docIndex unique document identifier in OmniDocs
     * @return file download response with appropriate content type
     * @throws InvalidParameterException if parameters are invalid or content is empty
     */
    @GetMapping("/download/{docIndex}")
    public ResponseEntity<?> downloadDocument(@PathVariable String docIndex){
        logger.trace("Entering downloadDocument() method with docIndex: {}", docIndex);
        logger.info("Request received to download document. DocIndex: {}", docIndex);
        
        // Validate docIndex parameter
        if (docIndex == null || docIndex.trim().isEmpty()) {
            logger.warn("Invalid docIndex provided: null or empty");
            throw new InvalidParameterException("Document index cannot be null or empty");
        }
        
        logger.debug("Fetching document bytes for docIndex: {}", docIndex);
        byte[] body = documentService.fetchDocBytes(docIndex);
        logger.debug("Document bytes fetched successfully. Document size: {} bytes", body != null ? body.length : 0);
        
        if (body == null || body.length == 0) {
            logger.warn("Document bytes are null or empty for docIndex: {}", docIndex);
            throw new InvalidParameterException("Document content is empty");
        }
        
        String documentName = documentResponse.getDocumentName();
        String createdByAppName = documentResponse.getCreatedByAppName();
        
        if (documentName == null || createdByAppName == null) {
            logger.warn("Document metadata is incomplete. DocumentName: {}, CreatedByAppName: {}", documentName, createdByAppName);
            throw new InvalidParameterException("Document metadata is incomplete");
        }
        
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
    }


    /**
     * Fetches a document as inline content. When path variable {@code base64}
     * equals {@code base64}, returns base64 string; otherwise returns raw bytes.
     *
     * @param base64 either literal "base64" or "bytes" value for bytes
     * @param docIndex unique document identifier in OmniDocs
     * @return inline response with document content
     * @throws MissingParameterException when required parameters are missing
     * @throws InvalidParameterException when parameters are invalid
     */
    @GetMapping("/fetchDoc/{base64}/{docIndex}")
    public ResponseEntity<?> fetchDocument(@PathVariable String base64, @PathVariable String docIndex){
        logger.trace("Entering fetchDocument() method with base64: {}, docIndex: {}", base64, docIndex);
        logger.info("Request received to fetch document. Format: {}, DocIndex: {}", base64, docIndex);

        // Validate parameters
        if (base64 == null || base64.trim().isEmpty()) {
            logger.warn("Invalid base64 format parameter provided: null or empty");
            throw new MissingParameterException("Format parameter 'base64' is required");
        }

        if (!"base64".equals(base64) && !"bytes".equals(base64)) {
            logger.warn("Invalid base64 format parameter provided: {}", base64);
            throw new InvalidParameterException("Invalid format parameter");
        }

        if ("bytes".equals(base64)) {
            logger.debug("Base64 format parameter provided is 'bytes'. Returning document bytes");
        }

        if ("base64".equals(base64)) {
            logger.debug("Base64 format parameter provided is 'base64'. Returning document as base64 string");
        }

        if (docIndex == null || docIndex.trim().isEmpty()) {
            logger.warn("Invalid docIndex provided: null or empty");
            throw new InvalidParameterException("Document index cannot be null or empty");
        }

        Object body;
        if("base64".equals(base64)) {
            logger.debug("Fetching document as base64 string for docIndex: {}", docIndex);
            body = documentService.fetchDocumentBase64(docIndex);
            return ResponseEntity
                    .ok(new ApiResponse<>(200, "OK", body));
        } else {
            logger.debug("Fetching document as bytes for docIndex: {}", docIndex);
            body = documentService.fetchDocBytes(docIndex);
            logger.debug("Document fetched as bytes. Size: {} bytes", body != null ? ((byte[]) body).length : 0);
        }

        if (body == null) {
            logger.warn("Document body is null for docIndex: {}", docIndex);
            throw new InvalidParameterException("Document content is null");
        }

        String documentName = documentResponse.getDocumentName();
        String createdByAppName = documentResponse.getCreatedByAppName();

        if (documentName == null || createdByAppName == null) {
            logger.warn("Document metadata is incomplete. DocumentName: {}, CreatedByAppName: {}", documentName, createdByAppName);
            throw new InvalidParameterException("Document metadata is incomplete");
        }

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
    }

}
