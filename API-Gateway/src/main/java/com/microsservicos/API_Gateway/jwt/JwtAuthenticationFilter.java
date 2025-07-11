// api-gateway/src/main/java/com/microsservicos/API_Gateway/jwt/JwtAuthenticationFilter.java
package com.microsservicos.API_Gateway.jwt;

import com.microsservicos.API_Gateway.service.JwtService;
import com.microsservicos.API_Gateway.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userSubject; // userId.toString()
        final String userEmail;
        final String userFullName;
        final String userRole;

        // Se não há token ou não está no formato Bearer, prossegue para o próximo filtro
        // O Spring Security decidirá se o endpoint é público ou protegido
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Extrai o token
        userSubject = jwtService.extractSubject(jwt); // Extrai o subject (ID do usuário)

        // Se o token tem um subject e o usuário ainda não está autenticado no contexto de segurança
        if (userSubject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Extrai os claims adicionais do token
            userEmail = jwtService.extractClaim(jwt, claims -> claims.get("email", String.class));
            userFullName = jwtService.extractClaim(jwt, claims -> claims.get("fullName", String.class));
            userRole = jwtService.extractClaim(jwt, claims -> claims.get("role", String.class));

            // Valida o token
            if (jwtService.isTokenValid(jwt, userSubject)) {
                // Converte o subject para UUID
                UUID userId = UUID.fromString(userSubject);

                // Cria um CustomUserDetails com os dados do token
                CustomUserDetails customUserDetails = new CustomUserDetails(userId, userEmail, userFullName, userRole);

                // Cria o objeto de autenticação e o define no SecurityContextHolder
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        null,
                        customUserDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    // No Gateway, este filtro deve ser aplicado a TODAS as rotas, exceto /api/auth/login
    // A exclusão de /api/auth/login será feita na SecurityConfig.
    // Portanto, shouldNotFilter() não é necessário aqui, pois a SecurityConfig fará o trabalho.
}