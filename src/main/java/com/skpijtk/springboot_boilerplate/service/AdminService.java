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

/**
 * Service yang menangani fitur-fitur khusus untuk Admin seperti pengambilan profil.
 */
@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Mengambil profil admin yang sedang login berdasarkan data dalam Authentication.
     *
     * @param authentication objek Spring Security Authentication.
     * @return response data berisi nama admin, role, dan waktu saat ini (format lokal Indonesia).
     * @throws CustomErrorException jika user tidak ditemukan, bukan admin, atau token tidak valid.
     */
    @Transactional(readOnly = true)
    public AdminProfileResponseData getAdminProfile(Authentication authentication) {
        // Validasi autentikasi
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomErrorException(
                "T-ERR-001",
                ResponseMessage.T_ERR_001,
                HttpStatus.UNAUTHORIZED
            );
        }

        // Ambil email dari token JWT
        String email = authentication.getName();

        // Ambil user berdasarkan email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomErrorException(
                        "T-ERR-006",
                        ResponseMessage.T_ERR_006,
                        HttpStatus.NOT_FOUND
                ));

        // Pastikan role user adalah ADMIN
        if (user.getRole() != UserRole.ADMIN) {
            throw new CustomErrorException(
                    "T-ERR-006",
                    ResponseMessage.T_ERR_006,
                    HttpStatus.FORBIDDEN
            );
        }

        // Format waktu saat ini dengan locale Indonesia
        String formattedTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("id", "ID")));

        // Bangun dan return response
        return AdminProfileResponseData.builder()
                .name(user.getName())
                .role(user.getRole())
                .time(formattedTime)
                .build();
    }
}
