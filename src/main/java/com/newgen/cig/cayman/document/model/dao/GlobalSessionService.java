package com.newgen.cig.cayman.document.model.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service for managing global session state across the application.
 * 
 * <p>This service maintains a single session ID that is shared across all requests.
 * The session ID is refreshed periodically by a scheduled task to ensure continuous
 * connectivity with the OmniDocs cabinet.</p>
 * 
 * <h3>Thread Safety:</h3>
 * <p>This service uses synchronized methods to ensure thread-safe access to the
 * session ID in a multi-threaded environment.</p>
 * 
 * <h3>Session Lifecycle:</h3>
 * <ul>
 *   <li>Session is initialized on application startup</li>
 *   <li>Session is refreshed every 5 minutes automatically</li>
 *   <li>Session can be manually cleared if needed</li>
 * </ul>
 * 
 * @author Tarun Vishwakarma
 * @version 1.0
 * @since 2025
 */
@Service
public class GlobalSessionService {

    private static final Logger logger = LoggerFactory.getLogger(GlobalSessionService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String sessionId;
    private LocalDateTime sessionCreatedAt;
    private LocalDateTime sessionLastRefreshedAt;
    private volatile boolean isSessionValid = false;

    /**
     * Sets the session ID and marks the session as valid.
     * 
     * <p>This method is thread-safe and logs all session updates for audit purposes.</p>
     * 
     * @param sessionId the new session ID to store
     */
    public synchronized void setSessionId(String sessionId) {
        logger.trace("Entering setSessionId() method");
        
        String previousSessionId = this.sessionId;
        LocalDateTime now = LocalDateTime.now();
        
        if (previousSessionId == null) {
            logger.info("Initializing new session. SessionId: {}", maskSessionId(sessionId));
            this.sessionCreatedAt = now;
        } else {
            logger.info("Updating existing session. Previous SessionId: {}, New SessionId: {}", 
                    maskSessionId(previousSessionId), maskSessionId(sessionId));
        }
        
        this.sessionId = sessionId;
        this.sessionLastRefreshedAt = now;
        this.isSessionValid = (sessionId != null && !sessionId.trim().isEmpty());
        
        logger.debug("Session state updated. Valid: {}, Created: {}, LastRefreshed: {}", 
                isSessionValid, 
                sessionCreatedAt != null ? sessionCreatedAt.format(DATE_FORMATTER) : "N/A",
                sessionLastRefreshedAt.format(DATE_FORMATTER));
        
        logger.trace("Exiting setSessionId() method");
    }

    /**
     * Retrieves the current session ID.
     * 
     * <p>This method is thread-safe and returns the cached session ID without
     * creating a new session.</p>
     * 
     * @return the current session ID, or null if no session exists
     */
    public synchronized String getSessionId() {
        logger.trace("Entering getSessionId() method");
        
        if (sessionId == null || sessionId.trim().isEmpty()) {
            logger.warn("Session ID is null or empty. Session may not be initialized.");
            logger.trace("Exiting getSessionId() method with null session");
            return null;
        }
        
        logger.debug("Retrieving session ID. SessionId: {}, Valid: {}, Age: {} seconds", 
                maskSessionId(sessionId), 
                isSessionValid,
                sessionLastRefreshedAt != null ? 
                        java.time.Duration.between(sessionLastRefreshedAt, LocalDateTime.now()).getSeconds() : "N/A");
        
        logger.trace("Exiting getSessionId() method with valid session");
        return sessionId;
    }

    /**
     * Checks if the current session is valid.
     * 
     * @return true if a valid session exists, false otherwise
     */
    public synchronized boolean isSessionValid() {
        logger.trace("Checking session validity. Valid: {}", isSessionValid);
        return isSessionValid;
    }

    /**
     * Gets the timestamp when the session was created.
     * 
     * @return the session creation timestamp, or null if no session exists
     */
    public synchronized LocalDateTime getSessionCreatedAt() {
        return sessionCreatedAt;
    }

    /**
     * Gets the timestamp when the session was last refreshed.
     * 
     * @return the last refresh timestamp, or null if no session exists
     */
    public synchronized LocalDateTime getSessionLastRefreshedAt() {
        return sessionLastRefreshedAt;
    }

    /**
     * Clears the current session ID and marks the session as invalid.
     * 
     * <p>This method should be called when logging out or when a session
     * needs to be explicitly invalidated.</p>
     */
    public synchronized void clearSession() {
        logger.trace("Entering clearSession() method");
        
        if (sessionId != null) {
            logger.info("Clearing session. Previous SessionId: {}, Created: {}, LastRefreshed: {}", 
                    maskSessionId(sessionId),
                    sessionCreatedAt != null ? sessionCreatedAt.format(DATE_FORMATTER) : "N/A",
                    sessionLastRefreshedAt != null ? sessionLastRefreshedAt.format(DATE_FORMATTER) : "N/A");
        } else {
            logger.debug("Attempting to clear session, but no session exists");
        }
        
        this.sessionId = null;
        this.isSessionValid = false;
        this.sessionCreatedAt = null;
        this.sessionLastRefreshedAt = null;
        
        logger.debug("Session cleared successfully");
        logger.trace("Exiting clearSession() method");
    }

    /**
     * Masks the session ID for secure logging.
     * Only shows first 8 characters followed by asterisks.
     * 
     * @param sessionId the session ID to mask
     * @return masked session ID for logging
     */
    private String maskSessionId(String sessionId) {
        if (sessionId == null) {
            return "null";
        }
        if (sessionId.length() <= 8) {
            return "***";
        }
        return sessionId.substring(0, 8) + "***";
    }
}
