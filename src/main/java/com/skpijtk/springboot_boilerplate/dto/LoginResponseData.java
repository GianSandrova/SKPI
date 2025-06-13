package com.skpijtk.springboot_boilerplate.dto;

import com.skpijtk.springboot_boilerplate.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseData {
    private Long idUser;
    private String token;
    private String name;
    private UserRole role;
}