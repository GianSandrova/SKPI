package com.skpijtk.springboot_boilerplate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skpijtk.springboot_boilerplate.model.CheckInStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponseDto {
    private Long studentId;
    private String studentName;
    private String nim;
    private Long attendanceId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkinTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkoutTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate attendanceDate;
    
    private String notesCheckin;
    private String notesCheckout;
    private CheckInStatus statusCheckin;
}