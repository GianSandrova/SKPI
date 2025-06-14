package com.skpijtk.springboot_boilerplate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckoutRequestDto {
    @NotBlank(message = "Check-out notes cannot be empty")
    private String notesCheckout;
}