
package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.enums.ErrorCode;

/**
 * Exception thrown when connection to the OmniDocs cabinet fails.
 * 
 * <p>This exception is raised when the application cannot establish or maintain
 * a connection to the Newgen OmniDocs cabinet system.</p>
 * 
 * <h3>HTTP Status:</h3>
 * <p>Results in HTTP 503 (Service Unavailable) response</p>
 * 
 * <h3>Common Causes:</h3>
 * <ul>
 *   <li>OmniDocs cabinet service is down or unreachable</li>
 *   <li>Network connectivity issues</li>
 *   <li>Invalid cabinet credentials</li>
 *   <li>Incorrect cabinet configuration (IP, port, cabinet name)</li>
 *   <li>Firewall blocking connections</li>
 *   <li>Session timeout or expired authentication</li>
 * </ul>
 * 
 * <h3>Resolution Steps:</h3>
 * <ol>
 *   <li>Verify OmniDocs service is running</li>
 *   <li>Check network connectivity to cabinet server</li>
 *   <li>Validate credentials in application.yml</li>
 *   <li>Confirm cabinet name, IP, and port settings</li>
 *   <li>Review firewall rules</li>
 * </ol>
 * 
 * @author Tarun Vishwakarma
 * @version 1.0
 * @since 2025
 * @see ErrorCode#CABINET_CONNECTION_ERROR
 */
public class CabinetConnectionException extends BaseException {

    /**
     * Constructs a new CabinetConnectionException with the specified details.
     * 
     * @param details descriptive message about the connection failure
     */
    public CabinetConnectionException(String details) {
        super(ErrorCode.CABINET_CONNECTION_ERROR, details);
    }

    /**
     * Constructs a new CabinetConnectionException with details and cause.
     * 
     * @param details descriptive message about the connection failure
     * @param cause the underlying cause of the exception
     */
    public CabinetConnectionException(String details, Throwable cause) {
        super(ErrorCode.CABINET_CONNECTION_ERROR, details, cause);
    }
}
