package com.newgen.cig.cayman.document.service;

import com.newgen.cig.cayman.document.implementation.Operations;
import com.newgen.cig.cayman.document.interfaces.DocumentInterface;
import com.newgen.cig.cayman.document.model.dao.GlobalSessionService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private static final Logger LOG = Logger.getLogger(DocumentService.class);

    @Autowired
    private DocumentInterface doc;

    @Autowired
    private GlobalSessionService globalSessionService;  // Inject GlobalSessionService

    @Autowired
    private Operations operations;

    public String getSessionId() throws Exception {
        String response = doc.connectCabinet(); // Connect to the cabinet
        String sessionId = operations.getValueFromXML(response,"UserDBId");
        globalSessionService.setSessionId(sessionId);
        LOG.debug("SessionID/UserDBId in " + this.getClass().getName() + " : " + sessionId);
        return sessionId;
    }

    public String downloadDocument(String docIndex) throws Exception {
        return doc.download(docIndex);
    }

    public String fetchDocument(String docIndex) throws Exception {
        return doc.fetchDoc(docIndex);
    }
}