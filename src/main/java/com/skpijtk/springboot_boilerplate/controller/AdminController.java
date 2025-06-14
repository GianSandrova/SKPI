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

/**
 * Controller untuk menangani permintaan terkait data profil admin.
 * Endpoint dalam kelas ini diasumsikan hanya bisa diakses oleh admin yang sudah login.
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Mengambil informasi profil admin berdasarkan data autentikasi yang sedang aktif.
     * 
     * @param authentication objek autentikasi dari Spring Security
     * @return response dengan data profil admin dan metadata status
     */
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
