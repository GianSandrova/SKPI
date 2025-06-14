package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.*;
import com.skpijtk.springboot_boilerplate.service.AuthService;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminAuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint untuk registrasi admin baru.
     * Validasi otomatis dilakukan via anotasi @Valid.
     */
    @PostMapping("/signup")
    public ResponseEntity<GlobalResponse<SignUpResponseData>> registerAdmin(@Valid @RequestBody AdminSignUpRequest signUpRequest) {
        SignUpResponseData responseData = authService.registerAdmin(signUpRequest);
        GlobalResponse<SignUpResponseData> response = GlobalResponse.success(
                responseData,
                ResponseMessage.T_SUCC_001,
                HttpStatus.OK
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint untuk login admin.
     * Mengembalikan JWT token jika autentikasi berhasil.
     */
    @PostMapping("/login")
    public ResponseEntity<GlobalResponse<LoginResponseData>> loginAdmin(@Valid @RequestBody LoginRequestDto loginRequest) {
        LoginResponseData responseData = authService.loginAdmin(loginRequest);
        GlobalResponse<LoginResponseData> response = GlobalResponse.success(
                responseData,
                ResponseMessage.T_SUCC_002,
                HttpStatus.OK
        );
        return ResponseEntity.ok(response);
    }
}
