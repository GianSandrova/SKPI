package com.skpijtk.springboot_boilerplate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateStudentRequest {

    // Rule #1: Nama lengkap, maks 50 char, hanya huruf dan spasi (tidak di awal/akhir)
    @NotBlank(message = "studentName cannot be empty")
    @Size(min = 2, max = 50, message = "studentName must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z]+(?:\\s[a-zA-Z]+)*$", message = "studentName must only contain letters and single spaces between words")
    private String studentName;

    // Rule #2: NIM harus 9 digit angka
    @NotBlank(message = "nim cannot be empty")
    @Pattern(regexp = "^\\d{9}$", message = "nim must be exactly 9 digits")
    private String nim;

    // Rule #3: Email valid, maks 50 char
    @NotBlank(message = "email cannot be empty")
    @Size(max = 50, message = "email must be at most 50 characters")
    @Email(message = "email format is not valid")
    private String email;

    // Rule #4: Password kombinasi huruf & angka, 6-12 char
    @NotBlank(message = "password cannot be empty")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,12}$", 
             message = "password must be 6-12 characters long and contain at least one letter and one number, with no special characters")
    private String password;
}