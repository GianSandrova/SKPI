package com.skpijtk.springboot_boilerplate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResumeCheckinResponseData {
    private long totalMahasiswa;
    private long totalCheckin;
    private long totalBelumCheckin;
    private long totalTelatCheckin;
}