package com.skpijtk.springboot_boilerplate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EditStudentRequest {

    @NotBlank(message = "studentName cannot be empty")
    @Size(min = 2, max = 50, message = "studentName must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z]+(?:\\s[a-zA-Z]+)*$", message = "studentName must only contain letters and single spaces between words")
    private String studentName;

    @NotBlank(message = "nim cannot be empty")
    @Pattern(regexp = "^\\d{9}$", message = "nim must be exactly 9 digits")
    private String nim;

    @NotBlank(message = "email cannot be empty")
    @Size(max = 50, message = "email must be at most 50 characters")
    @Email(message = "email format is not valid")
    private String email;
}