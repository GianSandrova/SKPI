package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.AppSettingsDto;
import com.skpijtk.springboot_boilerplate.dto.GlobalResponse;
import com.skpijtk.springboot_boilerplate.dto.UpdateAppSettingsRequestDto;
import com.skpijtk.springboot_boilerplate.service.SettingsService;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller untuk menangani permintaan terkait pengaturan sistem (system settings)
 * yang hanya dapat diakses oleh pengguna dengan peran admin.
 */
@RestController
@RequestMapping("/admin")
public class AdminSettingsController {

    private static final Logger logger = LoggerFactory.getLogger(AdminSettingsController.class);

    @Autowired
    private SettingsService settingsService;

    /**
     * Endpoint untuk mengambil data pengaturan aplikasi yang sedang aktif.
     *
     * @return response yang berisi data pengaturan aplikasi
     */
    @GetMapping("/system-settings")
    public ResponseEntity<GlobalResponse<AppSettingsDto>> getSystemSettings() {
        logger.info("Request received for GET /system-settings");

        AppSettingsDto settingsData = settingsService.getAppSettings();

        GlobalResponse<AppSettingsDto> response = GlobalResponse.success(
                settingsData,
                ResponseMessage.T_SUCC_005,
                HttpStatus.OK
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint untuk memperbarui pengaturan aplikasi berdasarkan request yang dikirimkan admin.
     * Validasi dijalankan secara otomatis berdasarkan anotasi @Valid.
     *
     * @param request objek DTO yang berisi data pengaturan baru
     * @return response dengan data pengaturan terbaru setelah diperbarui
     */
    @PutMapping("/system-settings")
    public ResponseEntity<GlobalResponse<AppSettingsDto>> updateSystemSettings(
            @Valid @RequestBody UpdateAppSettingsRequestDto request) {

        logger.info("Request received for PUT /system-settings");

        AppSettingsDto updatedSettingsData = settingsService.updateAppSettings(request);

        GlobalResponse<AppSettingsDto> response = GlobalResponse.success(
                updatedSettingsData,
                ResponseMessage.T_SUCC_008,
                HttpStatus.OK
        );
        return ResponseEntity.ok(response);
    }
}
