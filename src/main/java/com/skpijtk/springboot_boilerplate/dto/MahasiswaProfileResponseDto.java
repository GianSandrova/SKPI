package com.skpijtk.springboot_boilerplate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MahasiswaProfileResponseDto {
    // Profil Mahasiswa
    private Long studentId;
    private String studentName;
    private String nim;

    // Riwayat Kehadiran (Paginated)
    @JsonProperty("attendanceData")
    private List<AttendanceRecordDto> attendanceRecords;
    
    // Informasi Paginasi
    private long totalData;
    private int totalPage;
    private int currentPage;
    private int pageSize;
}