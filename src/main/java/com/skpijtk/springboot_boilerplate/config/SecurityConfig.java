package com.skpijtk.springboot_boilerplate.config;

import com.skpijtk.springboot_boilerplate.config.security.AuthEntryPointJwt;
import com.skpijtk.springboot_boilerplate.config.security.AuthTokenFilter;
import com.skpijtk.springboot_boilerplate.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Jika menggunakan JWT Filter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Untuk @PreAuthorize jika diperlukan
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // Jika menggunakan JWT Filter, Anda perlu AuthEntryPointJwt dan AuthTokenFilter
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler)) // Pastikan ini ada untuk handling JWT
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth ->
                    auth.requestMatchers("api/v1/admin/signup", "api/v1/admin/login", "api/v1/mahasiswa/login").permitAll()
                        // .requestMatchers("/admin/**").hasRole("ADMIN") // Opsi: Lebih spesifik untuk role
                        .anyRequest().authenticated() 
            );

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class); // Pastikan JWT Filter aktif
        return http.build();
    }
}