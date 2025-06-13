package com.skpijtk.springboot_boilerplate.service;

import com.skpijtk.springboot_boilerplate.dto.AdminProfileResponseData;
import com.skpijtk.springboot_boilerplate.exception.CustomErrorException;
import com.skpijtk.springboot_boilerplate.model.User;
import com.skpijtk.springboot_boilerplate.model.UserRole;
import com.skpijtk.springboot_boilerplate.repository.UserRepository;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public AdminProfileResponseData getAdminProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // Sesuai API Contract "Gagal Get Profile Admin" -> T-ERR-006, atau T-ERR-001 untuk unauthorized
            // Untuk kasus token tidak valid/tidak ada, AuthEntryPointJwt akan handle dengan "T-ERR-001"
            // Jika logika ini tetap diperlukan di service:
            throw new CustomErrorException(
                "T-ERR-001", // Mengikuti standar untuk unauthorized
                ResponseMessage.T_ERR_001, // Display message untuk logging
                HttpStatus.UNAUTHORIZED
            );
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomErrorException(
                        "T-ERR-006", // Sesuai API Contract "Gagal Get Profile Admin"
                        ResponseMessage.T_ERR_006, // Display message "Data failed to display." (meskipun kurang pas untuk user not found)
                        HttpStatus.NOT_FOUND // Atau sesuaikan status code jika T-ERR-006 punya status code spesifik di kontrak
                ));

        if (user.getRole() != UserRole.ADMIN) {
            throw new CustomErrorException(
                    "T-ERR-006", // Sesuai API Contract "Gagal Get Profile Admin"
                    ResponseMessage.T_ERR_006, // Display message (kurang pas, tapi mengikuti kode)
                    HttpStatus.FORBIDDEN // Atau status code lain jika T-ERR-006 punya mapping spesifik
            );
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
        String formattedTime = now.format(formatter);

        return AdminProfileResponseData.builder()
                .name(user.getName())
                .role(user.getRole())
                .time(formattedTime)
                .build();
    }

    // Anda bisa menambahkan method lain khusus admin di sini nantinya
}