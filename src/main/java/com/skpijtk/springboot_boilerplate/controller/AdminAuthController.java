package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.*;
import com.skpijtk.springboot_boilerplate.service.AuthService;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminAuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<GlobalResponse<SignUpResponseData>> registerAdmin(@Valid @RequestBody AdminSignUpRequest signUpRequest) {
        SignUpResponseData responseData = authService.registerAdmin(signUpRequest);
        GlobalResponse<SignUpResponseData> response = GlobalResponse.success(
                responseData,
                ResponseMessage.T_SUCC_001,
                HttpStatus.OK // Sesuai permintaan statusCode 200
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<GlobalResponse<LoginResponseData>> loginAdmin(@Valid @RequestBody LoginRequestDto loginRequest) {
        LoginResponseData responseData = authService.loginAdmin(loginRequest);
        GlobalResponse<LoginResponseData> response = GlobalResponse.success(
                responseData,
                ResponseMessage.T_SUCC_002,
                HttpStatus.OK // Sesuai permintaan statusCode 200
        );
        return ResponseEntity.ok(response);
    }
}