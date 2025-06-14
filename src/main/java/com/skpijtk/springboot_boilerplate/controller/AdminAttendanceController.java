package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.GlobalResponse;
import com.skpijtk.springboot_boilerplate.dto.PagedStudentAttendanceResponseData;
import com.skpijtk.springboot_boilerplate.service.AttendanceService;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin")
public class AdminAttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/list_checkin_mahasiswa")
    public ResponseEntity<GlobalResponse<PagedStudentAttendanceResponseData>> getListCheckinMahasiswa(
            @RequestParam(name = "student_name", required = false) String studentName,
            @RequestParam(name = "startdate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "enddate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "sortBy", required = false, defaultValue = "nim_asc") String sortBy,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        Pageable pageable;

        // Sorting sederhana berbasis field tertentu
        switch (sortBy.toLowerCase()) {
            case "nim_desc" -> pageable = PageRequest.of(page, size, Sort.by("student.nim").descending());
            case "status_priority" -> {
                // Note: Jika ingin sorting status dengan urutan prioritas kustom (misalnya "TEPAT_WAKTU" duluan),
                // perlu query khusus seperti CASE di SQL atau sorting manual setelah query.
                pageable = PageRequest.of(page, size, Sort.by("checkInStatus").ascending());
            }
            default -> pageable = PageRequest.of(page, size, Sort.by("student.nim").ascending());
        }

        PagedStudentAttendanceResponseData data = attendanceService.getListCheckinMahasiswa(
                studentName, startDate, endDate, sortBy, pageable);

        GlobalResponse<PagedStudentAttendanceResponseData> response = GlobalResponse.success(
                data,
                ResponseMessage.T_SUCC_004,
                HttpStatus.OK);

        return ResponseEntity.ok(response);
    }
}
