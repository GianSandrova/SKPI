package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.GlobalResponse;
import com.skpijtk.springboot_boilerplate.dto.MahasiswaProfileResponseDto;
import com.skpijtk.springboot_boilerplate.service.StudentService;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller untuk menangani permintaan profil mahasiswa.
 * Menyediakan informasi pribadi serta riwayat kehadiran dalam rentang tanggal tertentu.
 */
@RestController
@RequestMapping("/mahasiswa")
public class MahasiswaProfileController {

    private static final Logger logger = LoggerFactory.getLogger(MahasiswaProfileController.class);

    @Autowired
    private StudentService studentService;

    /**
     * Endpoint untuk mengambil profil mahasiswa beserta riwayat absensi.
     *
     * @param authentication objek autentikasi dari Spring Security
     * @param startDate batas awal pencarian absensi (opsional)
     * @param endDate batas akhir pencarian absensi (opsional)
     * @param page indeks halaman untuk pagination (default: 0)
     * @param size jumlah item per halaman (default: 10)
     * @return response berisi informasi profil dan absensi mahasiswa
     */
    @GetMapping("/profile")
    public ResponseEntity<GlobalResponse<MahasiswaProfileResponseDto>> getProfile(
            Authentication authentication,
            @RequestParam(name = "startdate", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(name = "enddate", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        String username = authentication.getName();
        logger.info("Mahasiswa '{}' mengakses profil dengan range tanggal {} - {}", 
                    username, startDate, endDate);

        Pageable pageable = PageRequest.of(page, size, Sort.by("attendanceDate").descending());

        MahasiswaProfileResponseDto profileData = studentService.getMahasiswaProfile(
                authentication, startDate, endDate, pageable);

        GlobalResponse<MahasiswaProfileResponseDto> response = GlobalResponse.success(
                profileData,
                ResponseMessage.T_SUCC_004,
                HttpStatus.OK
        );

        return ResponseEntity.ok(response);
    }
}
