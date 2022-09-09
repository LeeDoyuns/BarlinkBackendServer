package com.barlink.api.exception;

import org.springframework.http.HttpStatus;

public class RestException extends Exception {
    private HttpStatus status;
    private String message;
    private int statusCode;

    public RestException(HttpStatus status, String message, int statusCode) {
        this.status = status;
        this.message = message;
        this.statusCode = statusCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public int getStatusCode() { return statusCode; }

    public String getMessage() {
        return message;
    }
}
