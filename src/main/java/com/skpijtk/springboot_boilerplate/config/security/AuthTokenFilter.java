package com.skpijtk.springboot_boilerplate.config.security; // Sesuaikan dengan package Anda

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
// Tidak perlu @Component jika Anda membuatnya sebagai @Bean di SecurityConfig

import java.io.IOException;

// Tidak perlu @Component di sini jika Anda sudah mendefinisikannya sebagai @Bean di SecurityConfig
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider; // Pastikan JwtTokenProvider sudah ada dan di-inject

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl; // Atau nama UserDetailsService Anda

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null) { // Validasi dasar token dulu
                String username = jwtTokenProvider.getUsernameFromToken(jwt); // Ambil username
                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username); // Load UserDetails

                // Baru panggil validateToken dengan kedua argumen
                if (jwtTokenProvider.validateToken(jwt, userDetails)) { 
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
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Ambil token setelah "Bearer "
        }
        return null;
    }
}