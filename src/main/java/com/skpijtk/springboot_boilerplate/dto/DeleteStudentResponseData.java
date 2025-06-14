package com.skpijtk.springboot_boilerplate.dto;

import com.fasterxml.jackson.annotation.JsonProperty; 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteStudentResponseData {
    @JsonProperty("student_id") 
    private Long studentId;

    @JsonProperty("student_name") 
    private String studentName;

    @JsonProperty("nim") 
    private String nim;
}