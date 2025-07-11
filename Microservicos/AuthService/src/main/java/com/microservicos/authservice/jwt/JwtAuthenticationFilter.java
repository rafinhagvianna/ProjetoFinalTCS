package com.microservicos.authservice.jwt;

import com.microservicos.authservice.service.JwtService;
import com.microservicos.authservice.security.CustomUserDetails; // Importe seu CustomUserDetails
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails; // Ainda usado
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID; // Para converter o subject

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Ainda injetado, mas usado de forma diferente

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userSubject; // Este será o userId.toString()
        final String userEmail;
        final String userFullName;
        final String userRole;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        userSubject = jwtService.extractSubject(jwt); // userId.toString()
        userEmail = jwtService.extractClaim(jwt, claims -> claims.get("email", String.class));
        userFullName = jwtService.extractClaim(jwt, claims -> claims.get("fullName", String.class));
        userRole = jwtService.extractClaim(jwt, claims -> claims.get("role", String.class));


        if (userSubject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Valida o token e cria um CustomUserDetails com os claims
            if (jwtService.isTokenValid(jwt, userSubject)) {
                // Converta userSubject para UUID
                UUID userId = UUID.fromString(userSubject);

                // Crie o CustomUserDetails com os dados extraídos do token
                CustomUserDetails customUserDetails = new CustomUserDetails(userId, userEmail, userFullName, userRole);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        customUserDetails, // Agora o principal é o nosso CustomUserDetails
                        null,
                        customUserDetails.getAuthorities() // As autoridades vêm do CustomUserDetails
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
    return path.equals("/api/auth/login") ||
           path.equals("/api/cliente") ||
           path.startsWith("/swagger-ui");

    }
}