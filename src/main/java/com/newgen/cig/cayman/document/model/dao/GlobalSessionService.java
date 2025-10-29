package com.newgen.cig.cayman.document.model.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GlobalSessionService {

    private static final Logger logger = LoggerFactory.getLogger(GlobalSessionService.class);

    private String sessionId;

    // Setter for sessionId
    public void setSessionId(String sessionId) {
        logger.trace("Setting session ID");
        logger.debug("Previous session ID: {}, New session ID: {}", this.sessionId, sessionId);
        this.sessionId = sessionId;
        logger.info("Session ID set successfully. SessionId length: {}", sessionId != null ? sessionId.length() : 0);
    }

    // Getter for sessionId
    public String getSessionId() {
        logger.trace("Getting session ID");
        logger.debug("Current session ID: {}", sessionId != null ? sessionId : "null");
        return this.sessionId;
    }

    // Clear sessionId if needed
    public void clearSession() {
        logger.trace("Clearing session ID");
        logger.info("Clearing current session. Previous session ID: {}", sessionId);
        this.sessionId = null;
        logger.debug("Session cleared successfully");
    }
}
