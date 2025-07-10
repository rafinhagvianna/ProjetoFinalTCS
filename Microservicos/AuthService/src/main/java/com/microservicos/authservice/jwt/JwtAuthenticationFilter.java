package com.microservicos.authservice.jwt;

import com.microservicos.authservice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component; // Importe esta anotação
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Esta anotação é crucial para o Spring reconhecer o filtro como um Bean
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Para carregar detalhes do usuário

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
        final String userSubject; // ID ou nome de usuário do token

        // 1. Verifica se o cabeçalho Authorization está presente e no formato "Bearer token"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Continua a cadeia de filtros
            return;
        }

        // 2. Extrai o token JWT (removendo "Bearer ")
        jwt = authHeader.substring(7);

        // 3. Extrai o "subject" (geralmente o ID ou username do usuário) do token
        userSubject = jwtService.extractSubject(jwt);

        // 4. Se o subject foi extraído e não há autenticação no contexto de segurança atual
        if (userSubject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 5. Carrega os detalhes do usuário usando o UserDetailsService
            // No auth-service, o UserDetailsService pode precisar de uma implementação que
            // constrói um UserDetails com base no 'userSubject' do token,
            // já que você não deve ter um banco de dados de usuários local.
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userSubject);

            // 6. Valida o token
            if (jwtService.isTokenValid(jwt, userSubject)) {
                // Se o token é válido, cria um objeto de autenticação
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Credenciais nulas, pois já autenticamos via token
                        userDetails.getAuthorities() // Permissões do usuário
                );
                // Define detalhes da autenticação a partir da requisição
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Define o objeto de autenticação no contexto de segurança do Spring
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // 7. Continua a cadeia de filtros (para o próximo filtro ou para o DispatcherServlet)
        filterChain.doFilter(request, response);
    }
}