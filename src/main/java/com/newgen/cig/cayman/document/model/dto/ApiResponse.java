
package com.newgen.cig.cayman.document.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Standard API response wrapper used by controllers.
 *
 * <p>Encapsulates timestamp, status, message, data payload and error details
 * for consistent client responses.</p>
 *
 * @param <T> type of the data payload
 * @author Tarun Vishwakarma
 * @since 2025
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private String timestamp;
    private int status;
    private String message;
    private T data;
    private ErrorResponse error;

    public ApiResponse() {
        this.timestamp = LocalDateTime.now().toString();
    }

    public ApiResponse(int status, String message, T data) {
        this();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /**
     * Creates a 200 OK success response with default message.
     *
     * @param data payload to return
     * @return success response
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data);
    }

    /**
     * Creates a 200 OK success response with custom message.
     *
     * @param message custom success message
     * @param data payload to return
     * @return success response
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * Creates an error response with status, message and error details.
     *
     * @param status HTTP status code
     * @param message error message
     * @param error structured error details
     * @return error response
     */
    public static <T> ApiResponse<T> error(int status, String message, ErrorResponse error) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus(status);
        response.setMessage(message);
        response.setError(error);
        return response;
    }

    // Getters and setters
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }
}
