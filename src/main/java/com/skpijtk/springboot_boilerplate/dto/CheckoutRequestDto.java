package com.skpijtk.springboot_boilerplate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckoutRequestDto {
    @NotBlank(message = "Check-out notes cannot be empty") // Menggunakan T-ERR-009
    private String notesCheckout;
}