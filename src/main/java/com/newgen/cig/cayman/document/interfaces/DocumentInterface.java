package com.newgen.cig.cayman.document.interfaces;

public interface DocumentInterface {
    String connectCabinet() throws Exception;
    String fetchDoc(String docIndex) throws Exception;
}
