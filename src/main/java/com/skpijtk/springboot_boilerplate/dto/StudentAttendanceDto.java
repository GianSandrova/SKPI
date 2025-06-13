package com.skpijtk.springboot_boilerplate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttendanceDto {
    private Long studentId;
    private Long userId;
    private String studentName;
    private String nim;
    private String email;
    private List<AttendanceRecordDto> attendanceData; // Nama field di Java dan JSON: attendanceData
}