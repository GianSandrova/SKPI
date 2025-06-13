package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.GlobalResponse;
import com.skpijtk.springboot_boilerplate.dto.PagedStudentAttendanceResponseData;
import com.skpijtk.springboot_boilerplate.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest; // IMPORT BARU
import org.springframework.data.domain.Pageable; // IMPORT BARU
import org.springframework.data.domain.Sort; // IMPORT BARU
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;


import java.time.LocalDate;

@RestController
@RequestMapping("/admin")
public class AdminAttendanceController { // Atau nama lain sesuai struktur Anda

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/list_checkin_mahasiswa")
    public ResponseEntity<GlobalResponse<PagedStudentAttendanceResponseData>> getListCheckinMahasiswa(
            @RequestParam(name = "student_name", required = false) String studentName,
            @RequestParam(name = "startdate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "enddate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "sortBy", required = false, defaultValue = "nim_asc") String sortBy, // Default sorting NIM asc
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        Pageable pageable;
        // Logika sorting berdasarkan business rule
        if ("status_priority".equalsIgnoreCase(sortBy)) {
            // Sorting berdasarkan status memerlukan logika custom atau query yang lebih kompleks
            // Untuk contoh ini, kita bisa menggunakan default atau sorting sederhana via Pageable
            // Jika Anda ingin prioritas status: Tepat Waktu -> Terlambat -> (Belum Checkin/Belum Checkout)
            // Ini mungkin lebih baik dihandle di query/specification atau dengan multi-level sort
            // Contoh Sort sederhana (bisa lebih kompleks):
            // Sort sort = Sort.by("checkInStatus").ascending().and(Sort.by("student.nim").ascending());
            // Untuk prioritas status spesifik (TEPAT_WAKTU dulu), Anda perlu CASE WHEN di SQL atau logic di Java.
            // Untuk sekarang, kita gunakan sorting berdasarkan field yang ada langsung.
            // Misalnya, jika sortBy adalah "status_asc" atau "status_desc":
            pageable = PageRequest.of(page, size, Sort.by("checkInStatus").ascending()); // Placeholder, sesuaikan!
        } else if ("nim_desc".equalsIgnoreCase(sortBy)) {
            pageable = PageRequest.of(page, size, Sort.by("student.nim").descending());
        } else { // Default nim_asc atau jika sortBy tidak dikenal
            pageable = PageRequest.of(page, size, Sort.by("student.nim").ascending());
        }
        // Perhatian: Sort.by("student.nim") atau Sort.by("checkInStatus") mengasumsikan field tersebut bisa diakses langsung
        // dari entity Attendance melalui path join untuk sorting di level database.

        PagedStudentAttendanceResponseData data = attendanceService.getListCheckinMahasiswa(
                studentName, startDate, endDate, sortBy, pageable);

        // Kontrak: T-SUCC-005/T-SUCC-004. T-SUCC-004 lebih pas "Student data successfully found."
        GlobalResponse<PagedStudentAttendanceResponseData> response = GlobalResponse.success(
                data,
                ResponseMessage.T_SUCC_004,
                HttpStatus.OK);
        return ResponseEntity.ok(response);
    }
}