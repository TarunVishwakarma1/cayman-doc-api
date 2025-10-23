package com.newgen.cig.cayman.document.interfaces;

public interface DocumentInterface {
    String connectCabinet() throws Exception;
    String download(String docIndex) throws Exception;
    String getGetDocumentPropertyXml(String cabinetName, String userDbId, String creationDateTime, String docIndex, String dataAlsoFlag, String versionNo);
}
