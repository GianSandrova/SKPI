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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mahasiswa") 
public class MahasiswaAuthController {

    private static final Logger logger = LoggerFactory.getLogger(MahasiswaAuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<GlobalResponse<LoginResponseData>> loginMahasiswa(@Valid @RequestBody LoginRequestDto loginRequest) {
        logger.info("Login attempt for mahasiswa with email: {}", loginRequest.getEmail());

        LoginResponseData loginData = authService.loginMahasiswa(loginRequest);
        
        GlobalResponse<LoginResponseData> response = GlobalResponse.success(
                loginData,
                ResponseMessage.T_SUCC_002,
                HttpStatus.OK
        );
        return ResponseEntity.ok(response);
    }
}