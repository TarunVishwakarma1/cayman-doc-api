package com.newgen.cig.cayman.document.controller;

import com.newgen.cig.cayman.document.model.dao.DocumentResponse;
import com.newgen.cig.cayman.document.model.enums.DocumentType;
import com.newgen.cig.cayman.document.service.DocumentService;
import jakarta.annotation.PostConstruct;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/api/v1")
public class DocumentController {

    private static final Logger LOG = Logger.getLogger(DocumentController.class);

    @Autowired
    private DocumentResponse documentResponse;

    @Autowired
    private DocumentService documentService;

    @GetMapping("")
    public String hello(){
        return "<h1>Hello, World!<h1>";
    }

    @GetMapping("/sessionId")
    public ResponseEntity<?> sessionId() {
        try{
            String sessionId = documentService.getSessionId();
            return new ResponseEntity<>(sessionId, HttpStatus.OK);
        }catch(Exception e){
            LOG.error(e);
            return new ResponseEntity<>("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{docIndex}")
    public ResponseEntity<?> downloadDocument(@PathVariable String docIndex){
        try{
            byte[] body = documentService.fetchDocBytes(docIndex);
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentResponse.getDocumentName()+"."+documentResponse.getCreatedByAppName() + "\"")
                    .contentType(MediaType.valueOf(
                            DocumentType.fromExtension(
                                            documentResponse.getCreatedByAppName())
                                    .getContentType())
                    )
                    .body(body);
        }catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/fetchDoc/{base64}/{docIndex}")
    public ResponseEntity<?> fetchDocument(@PathVariable String base64, @PathVariable String docIndex){
        Object body = new Object();
        try{



            if("base64".equals(base64)) {
                body = documentService.fetchDocumentBase64(docIndex);
            }else{
                body = documentService.fetchDocBytes(docIndex);
            }
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + documentResponse.getDocumentName()+"."+documentResponse.getCreatedByAppName() + "\"")
                    .contentType(MediaType.valueOf(
                            DocumentType.fromExtension(
                                    documentResponse.getCreatedByAppName())
                                    .getContentType())
                    )
                    .body(body);
        }catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
