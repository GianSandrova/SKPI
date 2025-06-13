package com.skpijtk.springboot_boilerplate.controller;

import com.skpijtk.springboot_boilerplate.dto.AdminProfileResponseData;
import com.skpijtk.springboot_boilerplate.dto.GlobalResponse;
import com.skpijtk.springboot_boilerplate.service.AdminService;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin") // Base path untuk admin-specific (non-auth) endpoints
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/profile")
    public ResponseEntity<GlobalResponse<AdminProfileResponseData>> getAdminProfile(Authentication authentication) {
        AdminProfileResponseData profileData = adminService.getAdminProfile(authentication);
        GlobalResponse<AdminProfileResponseData> response = GlobalResponse.success(
                profileData,
                ResponseMessage.T_SUCC_005,
                HttpStatus.OK
        );
        return ResponseEntity.ok(response);
    }

}