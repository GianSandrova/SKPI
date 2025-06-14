package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.GlobalResponse;
import com.skpijtk.springboot_boilerplate.dto.LoginRequestDto;
import com.skpijtk.springboot_boilerplate.dto.LoginResponseData;
import com.skpijtk.springboot_boilerplate.service.AuthService;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 * Controller otentikasi untuk mahasiswa.
 * Menyediakan endpoint login mahasiswa menggunakan email dan password.
 */
@RestController
@RequestMapping("/mahasiswa")
public class MahasiswaAuthController {

    private static final Logger logger = LoggerFactory.getLogger(MahasiswaAuthController.class);

    @Autowired
    private AuthService authService;

    /**
     * Endpoint login untuk mahasiswa.
     *
     * @param loginRequest objek DTO yang memuat email dan password mahasiswa
     * @return response berisi token autentikasi dan informasi user
     */
    @PostMapping("/login")
    public ResponseEntity<GlobalResponse<LoginResponseData>> loginMahasiswa(
            @Valid @RequestBody LoginRequestDto loginRequest) {

        logger.info("Mahasiswa login attempt - email: {}", loginRequest.getEmail());

        LoginResponseData loginData = authService.loginMahasiswa(loginRequest);

        GlobalResponse<LoginResponseData> response = GlobalResponse.success(
                loginData,
                ResponseMessage.T_SUCC_002,
                HttpStatus.OK
        );

        return ResponseEntity.ok(response);
    }
}
