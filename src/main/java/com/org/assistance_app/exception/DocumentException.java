package com.org.assistance_app.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class DocumentException extends RuntimeException {

    private final HttpStatus status;

    public DocumentException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public DocumentException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}