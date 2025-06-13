package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.GlobalResponse;
import com.skpijtk.springboot_boilerplate.dto.ResumeCheckinResponseData; // IMPORT BARU
import com.skpijtk.springboot_boilerplate.dto.TotalMahasiswaResponseData;
import com.skpijtk.springboot_boilerplate.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat; // IMPORT BARU
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // IMPORT BARU
import org.springframework.web.bind.annotation.RestController;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;

import java.time.LocalDate; // IMPORT BARU

@RestController
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/total_mahasiswa")
    public ResponseEntity<GlobalResponse<TotalMahasiswaResponseData>> getTotalMahasiswa() {
        TotalMahasiswaResponseData data = attendanceService.getTotalMahasiswa();
        GlobalResponse<TotalMahasiswaResponseData> response = GlobalResponse.success(
                data,
                ResponseMessage.T_SUCC_005, 
                HttpStatus.OK
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resume_checkin")
    public ResponseEntity<GlobalResponse<ResumeCheckinResponseData>> getResumeCheckin(
            @RequestParam(name = "date", required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        LocalDate dateToQuery = (date == null) ? LocalDate.now() : date;
        
        ResumeCheckinResponseData data = attendanceService.getResumeCheckin(dateToQuery);
        GlobalResponse<ResumeCheckinResponseData> response = GlobalResponse.success(
                data,
                ResponseMessage.T_SUCC_005, 
                HttpStatus.OK
        );
        return ResponseEntity.ok(response);
    }
}