package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.GlobalResponse;
import com.skpijtk.springboot_boilerplate.dto.MahasiswaProfileResponseDto;
import com.skpijtk.springboot_boilerplate.service.StudentService;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/mahasiswa")
public class MahasiswaProfileController {
    
    private static final Logger logger = LoggerFactory.getLogger(MahasiswaProfileController.class);

    @Autowired
    private StudentService studentService;

    @GetMapping("/profile")
    public ResponseEntity<GlobalResponse<MahasiswaProfileResponseDto>> getProfile(
            Authentication authentication, // Diambil otomatis oleh Spring Security
            @RequestParam(name = "startdate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "enddate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        
        logger.info("Request received for /mahasiswa/profile");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("attendanceDate").descending());

        MahasiswaProfileResponseDto profileData = studentService.getMahasiswaProfile(authentication, startDate, endDate, pageable);
        
        GlobalResponse<MahasiswaProfileResponseDto> response = GlobalResponse.success(
                profileData,
                ResponseMessage.T_SUCC_004, // "Student data successfully found."
                HttpStatus.OK
        );
        return ResponseEntity.ok(response);
    }
}