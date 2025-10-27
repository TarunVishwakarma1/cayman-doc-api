package com.newgen.cig.cayman.document.model;

public class DocumentResponse {
    private NGOGetDocumentBDOResponse ngoGetDocumentBDOResponse;

    public NGOGetDocumentBDOResponse getNgoGetDocumentBDOResponse() {
        return ngoGetDocumentBDOResponse;
    }

    public void setNgoGetDocumentBDOResponse(NGOGetDocumentBDOResponse ngoGetDocumentBDOResponse) {
        this.ngoGetDocumentBDOResponse = ngoGetDocumentBDOResponse;
    }

    @Override
    public String toString() {
        return "DocumentResponse{" +
                "ngoGetDocumentBDOResponse=" + ngoGetDocumentBDOResponse +
                '}';
    }
}
