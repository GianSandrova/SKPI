package com.skpijtk.springboot_boilerplate.service;

import com.skpijtk.springboot_boilerplate.config.security.JwtTokenProvider;
import com.skpijtk.springboot_boilerplate.dto.AdminSignUpRequest;
import com.skpijtk.springboot_boilerplate.dto.LoginResponseData;
import com.skpijtk.springboot_boilerplate.dto.SignUpResponseData;
import com.skpijtk.springboot_boilerplate.exception.CustomErrorException;
import com.skpijtk.springboot_boilerplate.model.User;
import com.skpijtk.springboot_boilerplate.model.UserRole;
import com.skpijtk.springboot_boilerplate.repository.UserRepository;
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.skpijtk.springboot_boilerplate.dto.LoginRequestDto; 
import com.skpijtk.springboot_boilerplate.util.ResponseMessage;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignUpResponseData registerAdmin(AdminSignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            // Sesuai API Contract "Gagal signup" -> T-ERR-008 untuk email sudah dipakai
            throw new CustomErrorException(
                    "T-ERR-008",                         // Kode Pesan
                    ResponseMessage.T_ERR_008,           // Display Message untuk logging
                    HttpStatus.BAD_REQUEST
            );
        }

        User user = User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .role(UserRole.ADMIN)
                .build();

        userRepository.save(user);
        return new SignUpResponseData(user.getEmail());
    }

    public LoginResponseData loginAdmin(LoginRequestDto loginRequest) {
        // AuthenticationManager akan melempar BadCredentialsException atau UsernameNotFoundException
        // yang sudah di-handle di GlobalExceptionHandler dengan kode "T-ERR-001"
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Pengecekan ini mungkin redundan jika AuthenticationManager sudah memastikan user ada,
        // tapi jika user ditemukan namun tidak ada di DB (kasus aneh), ini akan melempar.
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomErrorException(
                        "T-ERR-001", // Sesuai API Contract "Gagal Login"
                        ResponseMessage.T_ERR_001, // Display Message untuk logging
                        HttpStatus.UNAUTHORIZED
                ));

        if (user.getRole() != UserRole.ADMIN) {
            // Jika user bukan admin, ini juga termasuk "Gagal Login" dengan kredensial tidak valid untuk konteks admin
             throw new CustomErrorException(
                "T-ERR-001", // Sesuai API Contract "Gagal Login"
                ResponseMessage.T_ERR_001, // Display Message untuk logging (atau bisa buat pesan spesifik "Akun bukan admin")
                HttpStatus.UNAUTHORIZED, 
                "Unauthorized" // statusText
            );
        }

        String jwt = jwtTokenProvider.generateToken(user); // Menggunakan user object untuk claim tambahan

        return LoginResponseData.builder()
                .idUser(user.getId())
                .token(jwt)
                .name(user.getName())
                .role(user.getRole())
                .build();
    }

        public LoginResponseData loginMahasiswa(LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomErrorException(
                    "T-ERR-001", 
                    ResponseMessage.T_ERR_001, // "Invalid username or password"
                    HttpStatus.UNAUTHORIZED
                ));

        // PENTING: Validasi bahwa role-nya adalah MAHASISWA
        if (user.getRole() != UserRole.MAHASISWA) {
             throw new CustomErrorException(
                "T-ERR-001", // Gagal login jika admin mencoba login di sini
                "Authentication failed. Not a student account.", // Pesan display untuk log
                HttpStatus.UNAUTHORIZED
            );
        }

        String jwt = jwtTokenProvider.generateToken(user);

        return LoginResponseData.builder()
                .idUser(user.getId())
                .token(jwt)
                .name(user.getName())
                .role(user.getRole())
                .build();
    }
}