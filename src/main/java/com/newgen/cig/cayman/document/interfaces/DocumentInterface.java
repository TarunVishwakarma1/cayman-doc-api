package com.newgen.cig.cayman.document.interfaces;

/**
 * Abstraction for interacting with Newgen OmniDocs cabinet.
 *
 * <p>Implementations connect to OmniDocs and fetch document content
 * by document index.</p>
 *
 * @author Tarun Vishwakarma
 * @since 2025
 */
public interface DocumentInterface {
    /**
     * Connects to the OmniDocs cabinet and returns the raw XML response.
     *
     * @return XML response containing connection details and session id
     */
    String connectCabinet();
    /**
     * Fetches a document as base64 using the given document index.
     *
     * @param docIndex unique document identifier in OmniDocs
     * @return base64 encoded document content
     */
    String fetchDoc(String docIndex);
}
