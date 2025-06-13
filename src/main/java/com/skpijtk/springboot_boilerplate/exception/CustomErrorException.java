package com.skpijtk.springboot_boilerplate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomErrorException extends RuntimeException {
    private final String messageCode;       // Kode pesan, contoh: "T-ERR-001"
    private final String displayMessage;    // Pesan deskriptif dari ResponseMessage.java
    private final HttpStatus httpStatus;
    private final String statusText;

    public CustomErrorException(String messageCode, String displayMessage, HttpStatus httpStatus) {
        super(displayMessage); // Pesan exception bisa tetap display message untuk logging internal
        this.messageCode = messageCode;
        this.displayMessage = displayMessage;
        this.httpStatus = httpStatus;
        this.statusText = httpStatus.getReasonPhrase();
    }

    public CustomErrorException(String messageCode, String displayMessage, HttpStatus httpStatus, String statusText) {
        super(displayMessage);
        this.messageCode = messageCode;
        this.displayMessage = displayMessage;
        this.httpStatus = httpStatus;
        this.statusText = statusText;
    }
}