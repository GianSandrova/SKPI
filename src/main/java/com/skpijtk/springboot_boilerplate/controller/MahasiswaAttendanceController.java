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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mahasiswa")
public class MahasiswaAttendanceController {

    private static final Logger logger = LoggerFactory.getLogger(MahasiswaAttendanceController.class);

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/checkin")
    public ResponseEntity<GlobalResponse<AttendanceResponseDto>> checkin(
            Authentication authentication,
            @Valid @RequestBody CheckinRequestDto request) {
        
        logger.info("Check-in attempt by user: {}", authentication.getName());
        AttendanceResponseDto data = attendanceService.performCheckIn(authentication, request);
        
        GlobalResponse<AttendanceResponseDto> response = GlobalResponse.success(data, ResponseMessage.T_SUCC_009_CHECKIN, HttpStatus.OK);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkout")
    public ResponseEntity<GlobalResponse<AttendanceResponseDto>> checkout(
            Authentication authentication,
            @Valid @RequestBody CheckoutRequestDto request) {

        logger.info("Check-out attempt by user: {}", authentication.getName());
        AttendanceResponseDto data = attendanceService.performCheckOut(authentication, request);

        GlobalResponse<AttendanceResponseDto> response = GlobalResponse.success(data, ResponseMessage.T_SUCC_010_CHECKOUT, HttpStatus.OK);
        return ResponseEntity.ok(response);
    }
}