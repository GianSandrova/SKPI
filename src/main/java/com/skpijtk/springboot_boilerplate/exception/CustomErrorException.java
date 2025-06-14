package com.skpijtk.springboot_boilerplate.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomErrorException extends RuntimeException {
    private final String messageCode;       
    private final String displayMessage;   
    private final HttpStatus httpStatus;
    private final String statusText;

    public CustomErrorException(String messageCode, String displayMessage, HttpStatus httpStatus) {
        super(displayMessage); 
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