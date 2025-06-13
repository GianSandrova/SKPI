package com.skpijtk.springboot_boilerplate.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateAppSettingsRequestDto {

    @NotNull(message = "defaultCheckInTime cannot be null")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", message = "defaultCheckInTime must be in HH:mm:ss format")
    private String defaultCheckInTime;

    @NotNull(message = "defaultCheckOutTime cannot be null")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", message = "defaultCheckOutTime must be in HH:mm:ss format")
    private String defaultCheckOutTime;

    @NotNull(message = "checkInLateToleranceMinutes cannot be null")
    @Min(value = 0, message = "checkInLateToleranceMinutes must be 0 or greater")
    @Max(value = 1440, message = "checkInLateToleranceMinutes cannot exceed 1440")
    private Integer checkInLateToleranceMinutes;

    // Saya tambahkan field ini agar lengkap sesuai DDL database Anda
    @NotNull(message = "checkOutLateToleranceMinutes cannot be null")
    @Min(value = 0, message = "checkOutLateToleranceMinutes must be 0 or greater")
    @Max(value = 1440, message = "checkOutLateToleranceMinutes cannot exceed 1440")
    private Integer checkOutLateToleranceMinutes;
}