package com.skpijtk.springboot_boilerplate.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // Untuk mencocokkan nama field di JSON
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteStudentResponseData {
    @JsonProperty("student_id") // Sesuai kontrak JSON
    private Long studentId;

    @JsonProperty("student_name") // Sesuai kontrak JSON
    private String studentName;

    @JsonProperty("nim") // Sesuai kontrak JSON
    private String nim;
}