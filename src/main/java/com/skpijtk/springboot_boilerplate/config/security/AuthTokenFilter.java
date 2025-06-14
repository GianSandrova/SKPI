package com.skpijtk.springboot_boilerplate.config.security;

import com.skpijtk.springboot_boilerplate.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter yang dijalankan sekali per request untuk memproses token JWT dari header Authorization.
 * Digunakan untuk menetapkan informasi autentikasi pengguna ke dalam SecurityContext.
 *
 * Catatan: Tidak perlu anotasi @Component jika bean ini sudah didefinisikan di kelas konfigurasi (SecurityConfig).
 */
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider; // Komponen util untuk mengelola parsing dan validasi token JWT

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl; // Service untuk mengambil detail user dari DB

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    /**
     * Filter utama yang menangani ekstraksi dan validasi JWT.
     * Jika token valid, maka informasi user akan disimpan di SecurityContext untuk kebutuhan otorisasi.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request); // Ambil token dari header Authorization
            if (jwt != null) {
                String username = jwtTokenProvider.getUsernameFromToken(jwt); // Extract username dari token
                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username); // Ambil data user

                // Validasi token terhadap user yang ditemukan
                if (jwtTokenProvider.validateToken(jwt, userDetails)) {
                    // Buat objek autentikasi dan simpan di SecurityContext
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            // Logging untuk debugging saat proses autentikasi gagal
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        // Lanjut ke filter berikutnya di filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Ambil token JWT dari header Authorization.
     * Format yang diharapkan: "Bearer {token}"
     *
     * @param request objek HTTP request
     * @return token JWT atau null jika tidak ditemukan atau tidak valid
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Ambil bagian token setelah "Bearer "
        }
        return null;
    }
}
