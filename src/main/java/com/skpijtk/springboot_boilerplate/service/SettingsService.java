package com.skpijtk.springboot_boilerplate.service;

import com.skpijtk.springboot_boilerplate.dto.AppSettingsDto;
import com.skpijtk.springboot_boilerplate.exception.CustomErrorException;
import com.skpijtk.springboot_boilerplate.model.AppSettings;
import com.skpijtk.springboot_boilerplate.repository.AppSettingsRepository;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;

import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.skpijtk.springboot_boilerplate.dto.UpdateAppSettingsRequestDto; 

@Service
public class SettingsService {

    private static final Logger logger = LoggerFactory.getLogger(SettingsService.class);
    private static final int SETTINGS_ID = 1; // ID untuk baris pengaturan adalah 1

    @Autowired
    private AppSettingsRepository appSettingsRepository;

    @Transactional(readOnly = true)
    public AppSettingsDto getAppSettings() {
        logger.info("Fetching application settings with id: {}", SETTINGS_ID);

        // Pengaturan aplikasi selalu ada di baris dengan id = 1
        AppSettings settings = appSettingsRepository.findById(SETTINGS_ID)
                .orElseThrow(() -> {
                    logger.error("Application settings with id {} not found in database. This is a critical configuration error.", SETTINGS_ID);
                    // Jika pengaturan tidak ada, ini adalah error server karena aplikasi tidak bisa beroperasi dengan benar.
                    // T-ERR-006 "Data failed to display." cocok untuk ini.
                    return new CustomErrorException(
                            "T-ERR-006",
                            ResponseMessage.T_ERR_006,
                            HttpStatus.INTERNAL_SERVER_ERROR
                    );
                });

        // Map dari Entity ke DTO
        return AppSettingsDto.builder()
                .defaultCheckInTime(settings.getDefaultCheckInTime())
                .defaultCheckOutTime(settings.getDefaultCheckOutTime())
                .checkInLateToleranceMinutes(settings.getCheckInLateToleranceMinutes())
                .checkOutLateToleranceMinutes(settings.getCheckOutLateToleranceMinutes())
                .build();
    }

        @Transactional
    public AppSettingsDto updateAppSettings(UpdateAppSettingsRequestDto request) {
        logger.info("Attempting to update application settings...");

        try {
            // 1. Ambil entitas pengaturan yang ada dari database
            AppSettings settingsToUpdate = appSettingsRepository.findById(SETTINGS_ID)
                    .orElseThrow(() -> new CustomErrorException(
                            "T-ERR-010",
                            ResponseMessage.T_ERR_010 + ". Reason: Settings record not found in database.",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));

            // 2. Update field entitas dengan data dari request
            settingsToUpdate.setDefaultCheckInTime(LocalTime.parse(request.getDefaultCheckInTime()));
            settingsToUpdate.setDefaultCheckOutTime(LocalTime.parse(request.getDefaultCheckOutTime()));
            settingsToUpdate.setCheckInLateToleranceMinutes(request.getCheckInLateToleranceMinutes());
            settingsToUpdate.setCheckOutLateToleranceMinutes(request.getCheckOutLateToleranceMinutes());

            // 3. Simpan entitas yang sudah di-update.
            // Di dalam @Transactional, ini tidak wajib tapi merupakan praktik yang baik.
            AppSettings updatedSettings = appSettingsRepository.save(settingsToUpdate);
            logger.info("Application settings successfully updated.");

            // 4. Map entitas yang sudah diupdate ke DTO untuk dikembalikan sebagai respons
            return AppSettingsDto.builder()
                    .defaultCheckInTime(updatedSettings.getDefaultCheckInTime())
                    .defaultCheckOutTime(updatedSettings.getDefaultCheckOutTime())
                    .checkInLateToleranceMinutes(updatedSettings.getCheckInLateToleranceMinutes())
                    .checkOutLateToleranceMinutes(updatedSettings.getCheckOutLateToleranceMinutes())
                    .build();

        } catch (Exception e) {
            logger.error("Failed to update application settings: {}", e.getMessage(), e);
            // Jika terjadi error lain, kembalikan T-ERR-010
            throw new CustomErrorException(
                    "T-ERR-010",
                    ResponseMessage.T_ERR_010,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}