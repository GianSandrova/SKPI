package com.skpijtk.springboot_boilerplate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalResponse<T> {
    private T data;
    private String message;
    private int statusCode;
    private String status;

    public static <T> GlobalResponse<T> success(T data, String message, HttpStatus httpStatus) {
        return GlobalResponse.<T>builder()
                .data(data)
                .message(message)
                .statusCode(httpStatus.value())
                .status(httpStatus.getReasonPhrase())
                .build();
    }

    public static <T> GlobalResponse<T> error(String message, HttpStatus httpStatus) {
        return GlobalResponse.<T>builder()
                .data(null)
                .message(message)
                .statusCode(httpStatus.value())
                .status(httpStatus.getReasonPhrase())
                .build();
    }
     public static <T> GlobalResponse<T> error(String message, int statusCode, String status) {
        return GlobalResponse.<T>builder()
                .data(null)
                .message(message)
                .statusCode(statusCode)
                .status(status)
                .build();
    }
}