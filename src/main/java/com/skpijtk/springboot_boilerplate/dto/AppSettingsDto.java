package com.skpijtk.springboot_boilerplate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppSettingsDto {

    // Jam akan diformat sebagai String "HH:mm:ss" di JSON
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime defaultCheckInTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime defaultCheckOutTime;

    private Integer checkInLateToleranceMinutes;

    // Saya tambahkan field ini agar sesuai dengan DDL database Anda
    private Integer checkOutLateToleranceMinutes;
}