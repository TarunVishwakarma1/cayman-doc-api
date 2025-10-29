package com.newgen.cig.cayman.document.model.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.util.Arrays;

public enum DocumentType {

    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
    GIF("gif", MediaType.IMAGE_GIF_VALUE),
    HEIC("heic", "image/heic"),
    JFIF("jfif", "image/jpeg"),
    JPEG("jpeg", MediaType.IMAGE_JPEG_VALUE),
    JPG("jpg", MediaType.IMAGE_JPEG_VALUE),
    JSP("jsp", "text/html"),
    PDF("pdf", MediaType.APPLICATION_PDF_VALUE),
    PNG("png", MediaType.IMAGE_PNG_VALUE),
    TIF("tif", "image/tiff"),
    TIFF("tiff", "image/tiff"),
    TXT("txt", MediaType.TEXT_PLAIN_VALUE),
    ZIP("zip", "application/zip");

    private static final Logger logger = LoggerFactory.getLogger(DocumentType.class);

    private final String extension;
    private final String contentType;

    DocumentType(String extension, String contentType){
        this.extension = extension;
        this.contentType = contentType;
    }

    public String getExtension() {
        return extension;
    }

    public String getContentType() {
        return contentType;
    }

    public static DocumentType fromExtension(String extension){
        logger.trace("Looking up DocumentType for extension: {}", extension);
        
        if (extension == null || extension.trim().isEmpty()) {
            logger.error("Extension is null or empty");
            throw new IllegalArgumentException("Extension cannot be null or empty");
        }
        
        String normalizedExtension = extension.toLowerCase().trim();
        logger.debug("Normalized extension: {}", normalizedExtension);
        
        DocumentType result = Arrays.stream(values())
                .filter(type -> type.extension.equalsIgnoreCase(normalizedExtension))
                .findFirst()
                .orElse(null);
        
        if (result == null) {
            logger.warn("Unsupported file type requested: {}", extension);
            logger.debug("Available extensions: {}", Arrays.toString(Arrays.stream(values())
                    .map(type -> type.extension)
                    .toArray(String[]::new)));
            throw new IllegalArgumentException("Unsupported file Type: " + extension);
        }
        
        logger.info("DocumentType found for extension '{}': {} (Content-Type: {})", 
                extension, result.name(), result.getContentType());
        logger.trace("DocumentType lookup successful");
        return result;
    }

}
