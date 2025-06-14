package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.GlobalResponse;
import com.skpijtk.springboot_boilerplate.dto.ResumeCheckinResponseData;
import com.skpijtk.springboot_boilerplate.dto.TotalMahasiswaResponseData;
import com.skpijtk.springboot_boilerplate.service.AttendanceService;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller untuk endpoint dashboard admin seperti statistik total mahasiswa
 * dan rekap absensi harian.
 */
@RestController
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private AttendanceService attendanceService;

    /**
     * Mengambil total mahasiswa yang terdaftar dalam sistem.
     *
     * @return response berisi total mahasiswa
     */
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

    /**
     * Mengambil resume check-in mahasiswa pada tanggal tertentu.
     * Jika tidak disediakan, akan menggunakan tanggal hari ini secara default.
     *
     * @param date tanggal yang ingin dicari (opsional)
     * @return response berisi data rekap check-in
     */
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
