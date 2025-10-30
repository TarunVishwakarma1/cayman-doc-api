package com.newgen.cig.cayman.document.service;

import com.newgen.cig.cayman.document.exception.CabinetConnectionException;
import com.newgen.cig.cayman.document.exception.DocumentNotFoundException;
import com.newgen.cig.cayman.document.exception.ExternalServiceException;
import com.newgen.cig.cayman.document.exception.XmlParsingException;
import com.newgen.cig.cayman.document.implementation.Operations;
import com.newgen.cig.cayman.document.interfaces.DocumentInterface;
import com.newgen.cig.cayman.document.model.dao.GlobalSessionService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * Service class for document management operations with Newgen OmniDocs.
 * 
 * <p>This service provides business logic for document retrieval and session management
 * with the Newgen OmniDocs cabinet system. It acts as an intermediary between the
 * controller layer and the OmniDocs integration layer.</p>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Session ID management with OmniDocs cabinet</li>
 *   <li>Automatic session refresh every 5 minutes</li>
 *   <li>Document retrieval in multiple formats (base64, bytes)</li>
 *   <li>Automatic XML response parsing</li>
 *   <li>Global session caching for performance</li>
 * </ul>
 * 
 * <h3>Session Management:</h3>
 * <p>The service maintains a single session with the cabinet that is automatically
 * refreshed every 5 minutes. This ensures continuous connectivity without creating
 * unnecessary sessions for each request.</p>
 * 
 * <h3>Service Dependencies:</h3>
 * <ul>
 *   <li>{@link DocumentInterface} - OmniDocs cabinet integration</li>
 *   <li>{@link Operations} - XML parsing and utility operations</li>
 *   <li>{@link GlobalSessionService} - Session state management</li>
 * </ul>
 * 
 * @author Tarun Vishwakarma
 * @version 2.0
 * @since 2025
 * @see DocumentInterface
 * @see Operations
 * @see GlobalSessionService
 */
@Service
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 1000;

    @Autowired
    private DocumentInterface doc;

    @Autowired
    private GlobalSessionService globalSessionService;

    @Autowired
    private Operations operations;

    /**
     * Initializes the service and creates the initial session on application startup.
     * 
     * <p>This method is called automatically after dependency injection is complete.
     * It establishes the first connection to the OmniDocs cabinet and stores the
     * session ID for subsequent use.</p>
     * 
     * @throws CabinetConnectionException if initial session creation fails
     */
    @PostConstruct
    public void initializeSession() {
        logger.info("========== Initializing DocumentService ==========");
        logger.debug("Starting initial session creation on application startup");
        
        try {
            createNewSession();
            logger.info("DocumentService initialized successfully with session");
        } catch (Exception e) {
            logger.error("Failed to initialize DocumentService with session. Application may not function correctly.", e);
            // Don't throw exception to allow application to start, scheduled task will retry
        }
        
        logger.info("========== DocumentService Initialization Complete ==========");
    }

    /**
     * Scheduled task that refreshes the session every 5 minutes.
     * 
     * <p>This method runs automatically in the background to maintain an active
     * session with the OmniDocs cabinet. If session creation fails, it will retry
     * up to 3 times before giving up.</p>
     * 
     * <h3>Schedule:</h3>
     * <ul>
     *   <li>Fixed rate: 5 minutes (300,000 milliseconds)</li>
     *   <li>Initial delay: 5 minutes after startup</li>
     * </ul>
     * 
     * <h3>Error Handling:</h3>
     * <p>If session refresh fails after all retry attempts, the error is logged
     * but the application continues to run. The next scheduled execution will
     * attempt to create a new session.</p>
     * 
     * @see #createNewSession()
     */
    @Scheduled(fixedRate = 300000, initialDelay = 300000) // 5 minutes = 300,000 ms
    public void refreshSessionPeriodically() {
        logger.info("========== Scheduled Session Refresh Started ==========");
        logger.debug("Executing scheduled session refresh task");
        
        try {
            createNewSession();
            logger.info("Scheduled session refresh completed successfully");
        } catch (Exception e) {
            logger.error("Scheduled session refresh failed. Next attempt in 5 minutes.", e);
        }
        
        logger.info("========== Scheduled Session Refresh Ended ==========");
    }

    /**
     * Creates a new session with the OmniDocs cabinet with retry logic.
     * 
     * <p>This method establishes a connection to the cabinet, extracts the session ID,
     * and stores it in the global session service. It includes retry logic to handle
     * transient failures.</p>
     * 
     * <h3>Process Flow:</h3>
     * <ol>
     *   <li>Attempts to connect to cabinet (with retries)</li>
     *   <li>Parses XML response to extract UserDBId</li>
     *   <li>Validates the session ID</li>
     *   <li>Stores session ID in GlobalSessionService</li>
     * </ol>
     * 
     * <h3>Retry Logic:</h3>
     * <p>If connection fails, retries up to 3 times with 1 second delay between attempts.</p>
     * 
     * @return the newly created session ID
     * @throws CabinetConnectionException if all retry attempts fail
     */
    private String createNewSession() {
        logger.trace("Entering createNewSession() method");
        logger.info("Creating new cabinet session");
        
        int attempt = 0;
        Exception lastException = null;
        
        while (attempt < MAX_RETRY_ATTEMPTS) {
            attempt++;
            
            try {
                logger.debug("Session creation attempt {} of {}", attempt, MAX_RETRY_ATTEMPTS);
                
                // Connect to cabinet
                logger.debug("Connecting to cabinet to establish new session");
                String response = doc.connectCabinet();
                
                if (response == null || response.trim().isEmpty()) {
                    logger.warn("Cabinet connection returned empty response on attempt {}", attempt);
                    throw new CabinetConnectionException("Empty response from cabinet connection");
                }
                
                logger.debug("Cabinet connection successful. Response length: {} characters", response.length());
                
                // Extract session ID from XML
                logger.trace("Extracting UserDBId from XML response");
                String sessionId = operations.getValueFromXML(response, "UserDBId");
                
                if (sessionId == null || sessionId.trim().isEmpty()) {
                    logger.error("Failed to extract valid session ID from XML response on attempt {}", attempt);
                    throw new XmlParsingException("UserDBId not found in cabinet response");
                }
                
                logger.debug("Session ID extracted successfully. SessionId: {}...", 
                        sessionId.length() > 8 ? sessionId.substring(0, 8) : "***");
                
                // Store session ID
                globalSessionService.setSessionId(sessionId);
                
                logger.info("New session created successfully. Attempt: {}, SessionId length: {}", 
                        attempt, sessionId.length());
                logger.trace("Exiting createNewSession() method with success");
                
                return sessionId;
                
            } catch (CabinetConnectionException | XmlParsingException e) {
                lastException = e;
                logger.warn("Session creation failed on attempt {} of {}: {}", 
                        attempt, MAX_RETRY_ATTEMPTS, e.getMessage());
                
                if (attempt < MAX_RETRY_ATTEMPTS) {
                    logger.debug("Retrying session creation in {} ms", RETRY_DELAY_MS);
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        logger.warn("Retry delay interrupted", ie);
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                
            } catch (Exception e) {
                lastException = e;
                logger.error("Unexpected error during session creation on attempt {}", attempt, e);
                
                if (attempt < MAX_RETRY_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        logger.warn("Retry delay interrupted", ie);
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        // All attempts failed
        logger.error("Failed to create session after {} attempts", MAX_RETRY_ATTEMPTS);
        logger.trace("Exiting createNewSession() method with failure");
        throw new CabinetConnectionException(
                "Failed to create cabinet session after " + MAX_RETRY_ATTEMPTS + " attempts", 
                lastException);
    }

    /**
     * Retrieves the current cached session ID without creating a new one.
     * 
     * <p>This method returns the session ID maintained by the GlobalSessionService.
     * The session is automatically refreshed by the scheduled task every 5 minutes.</p>
     * 
     * <h3>Behavior:</h3>
     * <ul>
     *   <li>Returns cached session if available</li>
     *   <li>Returns null if no session exists (shouldn't happen in normal operation)</li>
     *   <li>Does NOT create a new session</li>
     * </ul>
     * 
     * @return the current session ID, or null if no session exists
     */
    public String getSessionId() {
        logger.trace("Entering getSessionId() method");
        logger.info("Retrieving cached session ID");
        
        String sessionId = globalSessionService.getSessionId();
        
        if (sessionId == null || sessionId.trim().isEmpty()) {
            logger.warn("No valid session available. Session may not be initialized yet.");
            logger.debug("Session state - Valid: {}", globalSessionService.isSessionValid());
        } else {
            logger.info("Cached session ID retrieved successfully. SessionId length: {}", sessionId.length());
            logger.debug("Session metadata - Created: {}, LastRefreshed: {}", 
                    globalSessionService.getSessionCreatedAt(),
                    globalSessionService.getSessionLastRefreshedAt());
        }
        
        logger.trace("Exiting getSessionId() method");
        return sessionId;
    }

    /**
     * Fetches a document from the cabinet as a base64 encoded string.
     * 
     * <p>This method retrieves a document using its document index and returns it
     * as a base64 encoded string. This format is useful for JSON responses and
     * web applications that need to display or process documents.</p>
     * 
     * <h3>Use Cases:</h3>
     * <ul>
     *   <li>Embedding documents in JSON responses</li>
     *   <li>Displaying documents in web browsers</li>
     *   <li>Transmitting documents over REST APIs</li>
     * </ul>
     * 
     * @param docIndex the unique document index in the cabinet
     * @return base64 encoded string representation of the document
     * @throws DocumentNotFoundException if document with given index doesn't exist
     * @throws ExternalServiceException if cabinet service is unavailable
     * @see #fetchDocBytes(String) for binary format
     */
    public String fetchDocumentBase64(String docIndex) {
        logger.trace("Entering fetchDocumentBase64() method with docIndex: {}", docIndex);
        logger.info("Fetching document as base64 string. DocIndex: {}", docIndex);
        
        try {
            logger.debug("Calling doc.fetchDoc() for docIndex: {}", docIndex);
            String base64Document = doc.fetchDoc(docIndex);
            
            if (base64Document == null || base64Document.trim().isEmpty()) {
                logger.warn("Document fetch returned empty result for docIndex: {}", docIndex);
                throw new DocumentNotFoundException("Document not found or empty: " + docIndex);
            }
            
            logger.info("Document fetched successfully as base64. DocIndex: {}, Base64 length: {}", 
                    docIndex, base64Document.length());
            logger.debug("Base64 document preview: {}", 
                    base64Document.length() > 50 ? base64Document.substring(0, 50) + "..." : base64Document);
            logger.trace("Exiting fetchDocumentBase64() method with success");
            
            return base64Document;
            
        } catch (DocumentNotFoundException e) {
            logger.error("Document not found. DocIndex: {}", docIndex, e);
            throw e;
        } catch (Exception e) {
            logger.error("Error fetching document as base64. DocIndex: {}", docIndex, e);
            throw new ExternalServiceException("Failed to fetch document: " + e.getMessage(), e);
        }
    }

    /**
     * Fetches a document from the cabinet as a byte array.
     * 
     * <p>This method retrieves a document and converts it from base64 to raw bytes.
     * This format is suitable for file downloads, storage, and binary processing.</p>
     * 
     * <h3>Use Cases:</h3>
     * <ul>
     *   <li>Direct file downloads</li>
     *   <li>Saving documents to filesystem</li>
     *   <li>Binary stream processing</li>
     *   <li>Document size calculations</li>
     * </ul>
     * 
     * @param docIndex the unique document index in the cabinet
     * @return byte array containing the raw document data
     * @throws DocumentNotFoundException if document with given index doesn't exist
     * @throws ExternalServiceException if cabinet service is unavailable
     * @throws IllegalArgumentException if base64 decoding fails
     * @see #fetchDocumentBase64(String) for base64 format
     */
    public byte[] fetchDocBytes(String docIndex) {
        logger.trace("Entering fetchDocBytes() method with docIndex: {}", docIndex);
        logger.info("Fetching document as byte array. DocIndex: {}", docIndex);
        
        try {
            logger.debug("Fetching document as base64 first for docIndex: {}", docIndex);
            String base64Pdf = doc.fetchDoc(docIndex);
            
            if (base64Pdf == null || base64Pdf.trim().isEmpty()) {
                logger.warn("Document fetch returned empty result for docIndex: {}", docIndex);
                throw new DocumentNotFoundException("Document not found or empty: " + docIndex);
            }
            
            logger.debug("Base64 document received. Length: {} characters", base64Pdf.length());
            logger.trace("Decoding base64 to byte array");
            
            byte[] documentBytes = Base64.getDecoder().decode(base64Pdf);
            
            logger.info("Document decoded successfully. DocIndex: {}, Byte array size: {} bytes", 
                    docIndex, documentBytes.length);
            logger.debug("Document bytes preview - First 10 bytes: {}", 
                    documentBytes.length >= 10 ? 
                            String.format("%02X %02X %02X %02X %02X %02X %02X %02X %02X %02X...", 
                                    documentBytes[0], documentBytes[1], documentBytes[2], documentBytes[3], 
                                    documentBytes[4], documentBytes[5], documentBytes[6], documentBytes[7],
                                    documentBytes[8], documentBytes[9]) : 
                            "Document too small");
            logger.trace("Exiting fetchDocBytes() method with success");
            
            return documentBytes;
            
        } catch (DocumentNotFoundException e) {
            logger.error("Document not found. DocIndex: {}", docIndex, e);
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Failed to decode base64 document. DocIndex: {}", docIndex, e);
            throw new ExternalServiceException("Invalid base64 encoding in document: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error fetching document as bytes. DocIndex: {}", docIndex, e);
            throw new ExternalServiceException("Failed to fetch document: " + e.getMessage(), e);
        }
    }
}