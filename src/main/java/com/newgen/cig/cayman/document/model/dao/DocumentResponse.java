package com.newgen.cig.cayman.document.model.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DocumentResponse {

    private static final Logger logger = LoggerFactory.getLogger(DocumentResponse.class);
    private String createdByAppName;
    private String docContent;
    private String documentName;
    private String documentSize;
    private String documentType;
    private String message;
    private String statusCode;

    public String getCreatedByAppName() {
        logger.trace("Getting createdByAppName");
        return createdByAppName;
    }

    public void setCreatedByAppName(String createdByAppName) {
        logger.trace("Setting createdByAppName: {}", createdByAppName);
        logger.debug("Previous createdByAppName: {}, New createdByAppName: {}", this.createdByAppName, createdByAppName);
        this.createdByAppName = createdByAppName;
    }

    public String getDocContent() {
        logger.trace("Getting docContent");
        logger.debug("DocContent length: {}", docContent != null ? docContent.length() : 0);
        return docContent;
    }

    public void setDocContent(String docContent) {
        logger.trace("Setting docContent");
        logger.debug("Previous docContent length: {}, New docContent length: {}", 
                this.docContent != null ? this.docContent.length() : 0,
                docContent != null ? docContent.length() : 0);
        this.docContent = docContent;
    }

    public String getDocumentName() {
        logger.trace("Getting documentName");
        return documentName;
    }

    public void setDocumentName(String documentName) {
        logger.trace("Setting documentName: {}", documentName);
        logger.debug("Previous documentName: {}, New documentName: {}", this.documentName, documentName);
        this.documentName = documentName;
    }

    public String getDocumentSize() {
        logger.trace("Getting documentSize");
        return documentSize;
    }

    public void setDocumentSize(String documentSize) {
        logger.trace("Setting documentSize: {}", documentSize);
        logger.debug("Previous documentSize: {}, New documentSize: {}", this.documentSize, documentSize);
        this.documentSize = documentSize;
    }

    public String getDocumentType() {
        logger.trace("Getting documentType");
        return documentType;
    }

    public void setDocumentType(String documentType) {
        logger.trace("Setting documentType: {}", documentType);
        logger.debug("Previous documentType: {}, New documentType: {}", this.documentType, documentType);
        this.documentType = documentType;
    }

    public String getMessage() {
        logger.trace("Getting message");
        return message;
    }

    public void setMessage(String message) {
        logger.trace("Setting message: {}", message);
        logger.debug("Previous message: {}, New message: {}", this.message, message);
        this.message = message;
    }

    public String getStatusCode() {
        logger.trace("Getting statusCode");
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        logger.trace("Setting statusCode: {}", statusCode);
        logger.debug("Previous statusCode: {}, New statusCode: {}", this.statusCode, statusCode);
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "DocumentResponse{" +
                "createdByAppName='" + createdByAppName + '\'' +
                ", docContent='" + docContent + '\'' +
                ", documentName='" + documentName + '\'' +
                ", documentSize='" + documentSize + '\'' +
                ", documentType='" + documentType + '\'' +
                ", message='" + message + '\'' +
                ", statusCode='" + statusCode + '\'' +
                '}';
    }
}
