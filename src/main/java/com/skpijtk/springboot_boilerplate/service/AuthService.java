package com.skpijtk.springboot_boilerplate.service;

import com.skpijtk.springboot_boilerplate.config.security.JwtTokenProvider;
import com.skpijtk.springboot_boilerplate.dto.*;
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
            throw new CustomErrorException(
                "T-ERR-008",
                ResponseMessage.T_ERR_008,
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
        authenticate(loginRequest);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomErrorException(
                        "T-ERR-001",
                        ResponseMessage.T_ERR_001,
                        HttpStatus.UNAUTHORIZED
                ));

        if (user.getRole() != UserRole.ADMIN) {
            throw new CustomErrorException(
                "T-ERR-001",
                "Unauthorized access: Admin only.",
                HttpStatus.UNAUTHORIZED,
                "Unauthorized"
            );
        }

        return buildLoginResponse(user);
    }

    public LoginResponseData loginMahasiswa(LoginRequestDto loginRequest) {
        authenticate(loginRequest);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomErrorException(
                        "T-ERR-001",
                        ResponseMessage.T_ERR_001,
                        HttpStatus.UNAUTHORIZED
                ));

        if (user.getRole() != UserRole.MAHASISWA) {
            throw new CustomErrorException(
                "T-ERR-001",
                "Authentication failed. Not a student account.",
                HttpStatus.UNAUTHORIZED
            );
        }

        return buildLoginResponse(user);
    }

    // === PRIVATE METHODS ===

    private void authenticate(LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private LoginResponseData buildLoginResponse(User user) {
        String jwt = jwtTokenProvider.generateToken(user);

        return LoginResponseData.builder()
                .idUser(user.getId())
                .token(jwt)
                .name(user.getName())
                .role(user.getRole())
                .build();
    }
}
