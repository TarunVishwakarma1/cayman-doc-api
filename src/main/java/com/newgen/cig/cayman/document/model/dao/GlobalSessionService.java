package com.newgen.cig.cayman.document.model.dao;

import org.springframework.stereotype.Service;

@Service
public class GlobalSessionService {

    private String sessionId;

    // Setter for sessionId
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    // Getter for sessionId
    public String getSessionId() {
        return this.sessionId;
    }

    // Clear sessionId if needed
    public void clearSession() {
        this.sessionId = null;
    }
}
