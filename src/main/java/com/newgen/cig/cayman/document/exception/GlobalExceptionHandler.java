package com.newgen.cig.cayman.document.exception;

import com.newgen.cig.cayman.document.model.dto.ErrorResponse;
import com.newgen.cig.cayman.document.model.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle all custom base exceptions
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex, HttpServletRequest request) {
        logger.error("BaseException caught - Type: {}, Code: {}, Message: {}, Path: {}, Method: {}", 
                ex.getClass().getSimpleName(),
                ex.getErrorCode().getCode(),
                ex.getMessage(),
                request.getRequestURI(),
                request.getMethod());
        logger.debug("Exception details - ErrorCode: {}, Status: {}, Details: {}", 
                ex.getErrorCode().getCode(),
                ex.getErrorCode().getHttpStatus(),
                ex.getDetails());
        logger.trace("Full exception stack trace:", ex);

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getErrorCode().getHttpStatus(),
                ex.getErrorCode().getCode(),
                ex.getErrorCode().getMessage(),
                ex.getDetails(),
                request.getRequestURI()
        );

        logger.debug("Returning error response - Status: {}, ErrorCode: {}", 
                ex.getErrorCode().getHttpStatus().value(),
                ex.getErrorCode().getCode());
        return new ResponseEntity<>(errorResponse, ex.getErrorCode().getHttpStatus());
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        logger.warn("Validation exception caught - Path: {}, Method: {}, Error count: {}", 
                request.getRequestURI(), 
                request.getMethod(),
                ex.getBindingResult().getErrorCount());

        List<ErrorResponse.ValidationError> validationErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.add(new ErrorResponse.ValidationError(fieldName, errorMessage));
            logger.debug("Validation error - Field: {}, Message: {}, RejectedValue: {}", 
                    fieldName, 
                    errorMessage,
                    ((FieldError) error).getRejectedValue());
        });

        logger.info("Validation failed for {} field(s) in request to {}", 
                validationErrors.size(), request.getRequestURI());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_PARAMETER.getCode(),
                "Validation failed",
                "One or more fields have validation errors",
                request.getRequestURI()
        );
        errorResponse.setValidationErrors(validationErrors);

        logger.debug("Returning validation error response with {} validation errors", validationErrors.size());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle missing parameters
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        logger.warn("Missing parameter exception - Parameter: {}, Type: {}, Path: {}, Method: {}", 
                ex.getParameterName(),
                ex.getParameterType(),
                request.getRequestURI(),
                request.getMethod());
        logger.debug("Missing parameter details - Message: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.MISSING_PARAMETER.getCode(),
                "Missing required parameter",
                String.format("Parameter '%s' of type '%s' is required", ex.getParameterName(), ex.getParameterType()),
                request.getRequestURI()
        );

        logger.debug("Returning missing parameter error response for parameter: {}", ex.getParameterName());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle type mismatch
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        logger.warn("Type mismatch exception - Parameter: {}, RequiredType: {}, ProvidedValue: {}, Path: {}, Method: {}", 
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown",
                ex.getValue(),
                request.getRequestURI(),
                request.getMethod());
        logger.debug("Type mismatch details - Message: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_PARAMETER.getCode(),
                "Invalid parameter type",
                String.format("Parameter '%s' should be of type '%s'", ex.getName(),
                        ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"),
                request.getRequestURI()
        );

        logger.debug("Returning type mismatch error response for parameter: {}", ex.getName());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle malformed JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        logger.warn("Malformed JSON request - Path: {}, Method: {}, ContentType: {}", 
                request.getRequestURI(),
                request.getMethod(),
                request.getContentType());
        logger.error("Malformed JSON details - Message: {}", ex.getMessage());
        logger.debug("Root cause: {}", ex.getRootCause() != null ? ex.getRootCause().getMessage() : "none");

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCode.BAD_REQUEST.getCode(),
                "Malformed JSON request",
                "Please check your request body format",
                request.getRequestURI()
        );

        logger.debug("Returning malformed JSON error response");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle 404 - Not Found
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex, HttpServletRequest request) {
        logger.warn("No handler found exception - Method: {}, RequestURL: {}, Path: {}", 
                ex.getHttpMethod(),
                ex.getRequestURL(),
                request.getRequestURI());
        logger.debug("No handler found details - Message: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                ErrorCode.RESOURCE_NOT_FOUND.getCode(),
                "Resource not found",
                String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL()),
                request.getRequestURI()
        );

        logger.debug("Returning resource not found error response");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Handle unsupported HTTP method
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        logger.warn("HTTP method not supported - Method: {}, Path: {}, SupportedMethods: {}", 
                ex.getMethod(),
                request.getRequestURI(),
                ex.getSupportedMethods() != null ? String.join(", ", ex.getSupportedMethods()) : "none");
        logger.debug("Unsupported method details - Message: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                ErrorCode.BAD_REQUEST.getCode(),
                "HTTP method not supported",
                String.format("Method '%s' is not supported for this endpoint", ex.getMethod()),
                request.getRequestURI()
        );

        logger.debug("Returning method not allowed error response for method: {}", ex.getMethod());
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // Handle HTTP client errors (4xx from external services)
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpClientError(
            HttpClientErrorException ex, HttpServletRequest request) {
        logger.error("HTTP client error from external service - Status: {}, Path: {}, Method: {}, ResponseBody: {}", 
                ex.getStatusCode(),
                request.getRequestURI(),
                request.getMethod(),
                ex.getResponseBodyAsString() != null ? ex.getResponseBodyAsString().substring(0, 
                        Math.min(200, ex.getResponseBodyAsString().length())) : "none");
        logger.error("External client error details - Message: {}", ex.getMessage());
        logger.debug("Response headers: {}", ex.getResponseHeaders());
    
        // Convert HttpStatusCode to HttpStatus
        HttpStatus httpStatus = HttpStatus.valueOf(ex.getStatusCode().value());
    
        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus,
                ErrorCode.BAD_REQUEST.getCode(),
                "External service returned client error",
                sanitizeMessage(ex.getMessage()),
                request.getRequestURI()
        );

        logger.debug("Returning external client error response with status: {}", httpStatus.value());
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    private String sanitizeMessage(String message) {
        logger.trace("Sanitizing error message");
        // Implement logic to remove sensitive information
        if (message == null || message.trim().isEmpty()) {
            logger.debug("Message is null or empty, returning default");
            return "External service error";
        }
        // Remove potential sensitive patterns (customize as needed)
        String sanitized = message.replaceAll("(?i)(password|pwd|secret|token|key)\\s*[:=]\\s*[^,\\s]+", "$1=***");
        logger.debug("Message sanitized. Original length: {}, Sanitized length: {}", 
                message.length(), sanitized.length());
        return sanitized;
    }

    // Handle HTTP server errors (5xx from external services)
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpServerError(
            HttpServerErrorException ex, HttpServletRequest request) {
        logger.error("HTTP server error from external service - Status: {}, Path: {}, Method: {}, ResponseBody: {}", 
                ex.getStatusCode(),
                request.getRequestURI(),
                request.getMethod(),
                ex.getResponseBodyAsString() != null ? ex.getResponseBodyAsString().substring(0, 
                        Math.min(200, ex.getResponseBodyAsString().length())) : "none");
        logger.error("External service error details - Message: {}", ex.getMessage());
        logger.debug("Response headers: {}", ex.getResponseHeaders());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                ErrorCode.EXTERNAL_SERVICE_ERROR.getCode(),
                "External service unavailable",
                "External service returned server error: " + ex.getStatusCode().value(),
                request.getRequestURI()
        );

        logger.debug("Returning external service error response");
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    // Handle connection timeout/refused
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorResponse> handleResourceAccessException(
            ResourceAccessException ex, HttpServletRequest request) {
        logger.error("Resource access exception (timeout/connection refused) - Path: {}, Method: {}, Cause: {}", 
                request.getRequestURI(),
                request.getMethod(),
                ex.getCause() != null ? ex.getCause().getClass().getSimpleName() : "unknown");
        logger.error("Resource access error details - Message: {}", ex.getMessage(), ex);
        logger.debug("Full exception stack trace:", ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                ErrorCode.EXTERNAL_SERVICE_ERROR.getCode(),
                "Service unavailable",
                "Unable to connect to external service. This may be due to network timeout or connection refused.",
                request.getRequestURI()
        );

        logger.debug("Returning resource access error response");
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        logger.error("Unexpected exception caught - Type: {}, Message: {}, Path: {}, Method: {}, RemoteAddr: {}", 
                ex.getClass().getName(),
                ex.getMessage(),
                request.getRequestURI(),
                request.getMethod(),
                request.getRemoteAddr());
        logger.error("Unexpected exception stack trace:", ex);
        logger.warn("Unexpected exception details - Cause: {}, LocalizedMessage: {}", 
                ex.getCause() != null ? ex.getCause().getClass().getName() : "none",
                ex.getLocalizedMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                "Internal server error",
                "An unexpected error occurred. Please contact support if the problem persists",
                request.getRequestURI()
        );

        logger.debug("Returning generic error response - Status: 500, ErrorCode: ERR_500");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}