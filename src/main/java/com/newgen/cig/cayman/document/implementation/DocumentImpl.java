package com.newgen.cig.cayman.document.implementation;

import ISPack.CImageServer;
import ISPack.CPISDocumentTxn;
import ISPack.ISUtil.JPISException;
import Jdts.DataObject.JPDBString;
import com.newgen.cig.cayman.document.interfaces.DocumentInterface;
import com.newgen.cig.cayman.document.model.CabinetProperties;
import com.newgen.cig.cayman.document.model.ConnectCabinet;
import com.newgen.cig.cayman.document.model.GlobalSessionService;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class DocumentImpl implements DocumentInterface {

    private static final Logger LOG = Logger.getLogger(DocumentImpl.class);

    @Autowired
    private ConnectCabinet cabinet;

    @Autowired
    private CabinetProperties properties;

    @Autowired
    private GlobalSessionService sessionService;

    @Autowired
    private Operations operations;

    @Override
    public String connectCabinet() throws Exception {
        return cabinet.connect();
    }

    @Override
    public String download(String docIndex) throws Exception {
        String xml = downloadDocXML(docIndex);
        return operations.executeXML(xml);
    }

    @Override
    public byte[] docByteArray() throws Exception {
        String docId = "400";

        JPDBString oSiteName = new JPDBString();
        try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
            CImageServer.IsDebugLogEnabled = true;
            CPISDocumentTxn.GetDocInFile_MT(
                    null,
                    properties.getSiteIP(),
                    Short.parseShort(properties.getSitePort()),
                    properties.getCabinetName(),
                    Short.parseShort(properties.getSiteId()),
                    Short.parseShort(properties.getVolumeId()),
                    Short.parseShort(docId),
                    "",
                    out,
                    oSiteName);
            byte[] docByte = out.toByteArray();
            LOG.debug("Document Bytes Generated: "+ Arrays.toString(docByte));
            return docByte;
        }catch(Exception | JPISException e){
            LOG.error(e);
            throw new Exception(e);
        }
    }

    private String getGetDocumentPropertyXml(String cabinetName, String userDbId, String creationDateTime, String docIndex, String dataAlsoFlag, String versionNo) {
        String inputXml = "<?xml version=\"1.0\"?><NGOGetDocumentProperty_Input><Option>NGOGetDocumentProperty</Option>";
        inputXml = inputXml + "<CabinetName>" + cabinetName + "</CabinetName>";
        inputXml = inputXml + "<UserDBId>" + userDbId + "</UserDBId>";
        if (creationDateTime != null)
            inputXml = inputXml + "<CurrentDateTime>" + creationDateTime + "</CurrentDateTime>";
        if (docIndex != null)
            inputXml = inputXml + "<DocumentIndex>" + docIndex + "</DocumentIndex>";
        if (versionNo != null)
            inputXml = inputXml + "<VersionNo>" + versionNo + "</VersionNo>";
        if (dataAlsoFlag != null)
            inputXml = inputXml + "<DataAlsoFlag>" + dataAlsoFlag + "</DataAlsoFlag>";
        inputXml = inputXml + "</NGOGetDocumentProperty_Input>";
        return inputXml;
    }

    private String downloadDocXML(String docIndex){
        String xml = "<? xml version=\"1.0\" encoding=\"UTF-8\" ?>"+
                "<NGOGetDocumentBDO>"+
                "<cabinetName>"+properties.getCabinetName()+"</cabinetName>"+
                "<docIndex>"+docIndex+"</docIndex>"+
                "<siteId>"+properties.getSiteId()+"</siteId>"+
                "<volumeId>"+properties.getVolumeId()+"</volumeId>"+
                "<userDBId>"+sessionService.getSessionId()+"</userDBId>"+
                "<locale>en_us</locale>"+
                "</NGOGetDocumentBDO>";
        LOG.info("Document Download XML: "+ xml);
        LOG.debug("Document Download XML: "+ xml);
        return xml;
    }
}
