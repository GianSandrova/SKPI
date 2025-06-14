package com.skpijtk.springboot_boilerplate.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skpijtk.springboot_boilerplate.dto.GlobalResponse;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;

/**
 * Komponen untuk menangani skenario ketika user mencoba mengakses endpoint yang memerlukan
 * otentikasi tetapi belum melakukan login atau tokennya tidak valid.
 *
 * Implementasi ini digunakan oleh Spring Security sebagai entry point untuk exception terkait
 * otentikasi. 
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * Metode ini akan dipanggil otomatis oleh Spring Security ketika terjadi AuthenticationException.
     * Di sini kita mengatur response HTTP agar mengembalikan status 401 Unauthorized dan juga body
     * dalam format JSON yang sudah kita standardisasi.
     *
     * @param request HTTP request dari client
     * @param response HTTP response yang akan dikirim ke client
     * @param authException exception yang dilempar saat autentikasi gagal
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // Log pesan error unauthorized dengan kode error yang didefinisikan di enum ResponseMessage
        logger.error("Unauthorized error: {}. Original exception message: {}", ResponseMessage.T_ERR_001, authException.getMessage());

        // Set response content type dan status code ke 401 Unauthorized
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Bangun response standar menggunakan DTO GlobalResponse
        GlobalResponse<Object> body = GlobalResponse.error(
                ResponseMessage.T_ERR_001, // Kode pesan dari sistem untuk kasus unauthorized
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized" // Bisa diganti kalau ingin pesan lebih informatif
        );

        // Tulis response JSON ke output stream
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
