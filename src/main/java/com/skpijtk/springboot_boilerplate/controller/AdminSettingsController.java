package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.AppSettingsDto;
import com.skpijtk.springboot_boilerplate.dto.GlobalResponse;
import com.skpijtk.springboot_boilerplate.dto.UpdateAppSettingsRequestDto;
import com.skpijtk.springboot_boilerplate.service.SettingsService;
import jakarta.validation.Valid; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.skpijtk.springboot_boilerplate.util.ResponseMessage;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin") 
public class AdminSettingsController {

    private static final Logger logger = LoggerFactory.getLogger(AdminSettingsController.class);

    @Autowired
    private SettingsService settingsService;

    @GetMapping("/system-settings")
    public ResponseEntity<GlobalResponse<AppSettingsDto>> getSystemSettings() {
        logger.info("Request received for /system-settings");

        AppSettingsDto settingsData = settingsService.getAppSettings();

        // Kontrak: T-SUCC-005 atau T-SUCC-004. T-SUCC-005 ("Data successfully displayed.") lebih cocok.
        GlobalResponse<AppSettingsDto> response = GlobalResponse.success(
                settingsData,
                ResponseMessage.T_SUCC_005,
                HttpStatus.OK
        );
        return ResponseEntity.ok(response);
    }

        @PutMapping("/system-settings")
    public ResponseEntity<GlobalResponse<AppSettingsDto>> updateSystemSettings(
            @Valid @RequestBody UpdateAppSettingsRequestDto request) {
        
        logger.info("Request received to update /system-settings");

        AppSettingsDto updatedSettingsData = settingsService.updateAppSettings(request);

        GlobalResponse<AppSettingsDto> response = GlobalResponse.success(
                updatedSettingsData,
                ResponseMessage.T_SUCC_008,
                HttpStatus.OK 
        );
        return ResponseEntity.ok(response);
    }
}