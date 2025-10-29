package com.newgen.cig.cayman.document.model.enums;

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
        return Arrays.stream(values())
                .filter(type -> type.extension.equalsIgnoreCase(extension))
                .findFirst()
                .orElseThrow(()-> new IllegalArgumentException("Unsupported file Type: "+ extension));
    }

}
