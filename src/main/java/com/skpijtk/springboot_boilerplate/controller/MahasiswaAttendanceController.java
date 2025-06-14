package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.AttendanceResponseDto;
import com.skpijtk.springboot_boilerplate.dto.CheckinRequestDto;
import com.skpijtk.springboot_boilerplate.dto.CheckoutRequestDto;
import com.skpijtk.springboot_boilerplate.dto.GlobalResponse;
import com.skpijtk.springboot_boilerplate.service.AttendanceService;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller untuk mahasiswa dalam melakukan check-in dan check-out kehadiran.
 * Endpoint ini hanya dapat diakses oleh user dengan role mahasiswa.
 */
@RestController
@RequestMapping("/mahasiswa")
public class MahasiswaAttendanceController {

    private static final Logger logger = LoggerFactory.getLogger(MahasiswaAttendanceController.class);

    @Autowired
    private AttendanceService attendanceService;

    /**
     * Endpoint untuk melakukan check-in kehadiran mahasiswa.
     * Validasi otomatis diterapkan melalui anotasi @Valid.
     *
     * @param authentication objek autentikasi dari Spring Security
     * @param request data check-in dari body request
     * @return response berisi data kehadiran setelah berhasil check-in
     */
    @PostMapping("/checkin")
    public ResponseEntity<GlobalResponse<AttendanceResponseDto>> checkin(
            Authentication authentication,
            @Valid @RequestBody CheckinRequestDto request) {

        String username = authentication.getName();
        logger.info("Check-in attempt by user: {}", username);

        AttendanceResponseDto data = attendanceService.performCheckIn(authentication, request);

        GlobalResponse<AttendanceResponseDto> response = GlobalResponse.success(
                data,
                ResponseMessage.T_SUCC_009_CHECKIN,
                HttpStatus.OK
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint untuk melakukan check-out kehadiran mahasiswa.
     *
     * @param authentication objek autentikasi dari Spring Security
     * @param request data check-out dari body request
     * @return response berisi data kehadiran setelah berhasil check-out
     */
    @PostMapping("/checkout")
    public ResponseEntity<GlobalResponse<AttendanceResponseDto>> checkout(
            Authentication authentication,
            @Valid @RequestBody CheckoutRequestDto request) {

        String username = authentication.getName();
        logger.info("Check-out attempt by user: {}", username);

        AttendanceResponseDto data = attendanceService.performCheckOut(authentication, request);

        GlobalResponse<AttendanceResponseDto> response = GlobalResponse.success(
                data,
                ResponseMessage.T_SUCC_010_CHECKOUT,
                HttpStatus.OK
        );

        return ResponseEntity.ok(response);
    }
}
