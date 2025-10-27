package com.newgen.cig.cayman.document.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NGOGetDocumentBDOResponse {

    @JsonProperty("createdByAppName")
    private String createdByAppName;

    @JsonProperty("docContent")
    private String docContent;

    @JsonProperty("documentName")
    private String documentName;

    @JsonProperty("documentSize")
    private String documentSize;

    @JsonProperty("documentType")
    private String documentType;

    @JsonProperty("message")
    private String message;

    @JsonProperty("statusCode")
    private String statusCode;

    // Getters and Setters
    public String getCreatedByAppName() {
        return createdByAppName;
    }

    public void setCreatedByAppName(String createdByAppName) {
        this.createdByAppName = createdByAppName;
    }

    public String getDocContent() {
        return docContent;
    }

    public void setDocContent(String docContent) {
        this.docContent = docContent;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentSize() {
        return documentSize;
    }

    public void setDocumentSize(String documentSize) {
        this.documentSize = documentSize;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "NGOGetDocumentBDOResponse{" +
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
