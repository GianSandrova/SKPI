package com.skpijtk.springboot_boilerplate.exception;

import com.skpijtk.springboot_boilerplate.dto.GlobalResponse;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomErrorException.class)
    public ResponseEntity<GlobalResponse<Object>> handleCustomErrorException(CustomErrorException ex) {
        logger.error("Custom Error Exception: Code - {}, Display Message - {}", ex.getMessageCode(), ex.getDisplayMessage(), ex);
        
        GlobalResponse<Object> response = GlobalResponse.error(
                ex.getDisplayMessage(), // <-- PERUBAHAN: Gunakan display message dari exception
                ex.getHttpStatus().value(),
                ex.getStatusText()
        );
        return new ResponseEntity<>(response, ex.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        
        String detailedErrorMessage = "Validation failed: " + errors.entrySet().stream()
            .map(entry -> entry.getKey() + ": " + entry.getValue())
            .collect(Collectors.joining(", "));

        logger.error("Validation Error Details: {}", detailedErrorMessage, ex);

        // Untuk pesan validasi, kita bisa langsung menampilkan detail errornya
        // atau menggunakan pesan dari ResponseMessage yang memiliki placeholder,
        // tapi menampilkan detail langsung lebih informatif untuk developer.
        // Kita akan menampilkan detail error langsung ke user.
        GlobalResponse<Object> response = GlobalResponse.error(
                detailedErrorMessage, // <-- PERUBAHAN: Tampilkan detail error validasi
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GlobalResponse<Object>> handleBadCredentialsException(BadCredentialsException ex) {
        logger.error("Bad Credentials Error: {}", ResponseMessage.T_ERR_001, ex);
        
        GlobalResponse<Object> response = GlobalResponse.error(
                ResponseMessage.T_ERR_001, // <-- PERUBAHAN: Gunakan pesan dari ResponseMessage
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized"
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<GlobalResponse<Object>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        logger.error("Username Not Found Error (treated as Bad Credentials): {}", ResponseMessage.T_ERR_001, ex);
        
        GlobalResponse<Object> response = GlobalResponse.error(
                ResponseMessage.T_ERR_001, // <-- PERUBAHAN: Gunakan pesan dari ResponseMessage
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized"
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalResponse<Object>> handleGlobalException(Exception ex) {
        logger.error("Generic Uncaught Exception: {}", ResponseMessage.T_ERR_002, ex);
        
        GlobalResponse<Object> response = GlobalResponse.error(
                ResponseMessage.T_ERR_002, // <-- PERUBAHAN: Gunakan pesan dari ResponseMessage
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}