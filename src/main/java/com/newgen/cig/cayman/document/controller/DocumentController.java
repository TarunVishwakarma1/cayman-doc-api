package com.newgen.cig.cayman.document.controller;

import com.newgen.cig.cayman.document.service.DocumentService;
import jakarta.annotation.PostConstruct;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1")
public class DocumentController {

    private static final Logger LOG = Logger.getLogger(DocumentController.class);

    @Autowired
    private DocumentService documentService;

    @GetMapping("")
    public String hello(){
        return "<h1>Hello, World!<h1>";
    }

    @GetMapping("/sessionId")
    @PostConstruct
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
            String status = documentService.downloadDocument(docIndex);
            return new ResponseEntity<>("Document Downloaded Successfully!: "+ status, HttpStatus.OK);
        }catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>("Something went wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getDoc")
    public ResponseEntity<?> getDocBytes(){
        try{
            byte[] bArr = documentService.getDocBytes();
            LOG.info(Arrays.toString(bArr));
            return new ResponseEntity<>("Doc Stream Generated", HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>("Something Went Wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
