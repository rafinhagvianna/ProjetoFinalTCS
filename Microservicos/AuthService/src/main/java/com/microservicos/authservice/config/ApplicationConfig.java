package com.microservicos.authservice.config;

import com.microservicos.authservice.security.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User; // Importe User do Spring Security
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList; // Para Collections.emptyList() ou new ArrayList<>()
import java.util.UUID;

@Configuration
public class ApplicationConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // Este método é chamado pelo JwtAuthenticationFilter com o 'subject' do token.
            // Aqui, você não busca no DB, mas constrói um UserDetails para o Spring Security.
            // IMPORTANTE: Para obter os claims (email, fullName, role), o JwtAuthenticationFilter
            // precisa passar essas informações para cá ou, de forma mais direta, criar o CustomUserDetails
            // diretamente no JwtAuthenticationFilter e injetar no SecurityContextHolder.

            // Por enquanto, vamos retornar um CustomUserDetails básico.
            // A extração real dos claims ocorrerá no JwtAuthenticationFilter,
            // que é onde o token é lido.

            // Retorne um CustomUserDetails dummy por enquanto.
            // Ele será substituído pela lógica real no JwtAuthenticationFilter.
            // O username aqui é o subject do token (o userId.toString()).
            // Precisamos que o JwtAuthenticationFilter passe os claims adicionais.
            return new CustomUserDetails(
                    UUID.fromString(username), // Supondo que o subject é o UUID
                    "placeholder@example.com", // Placeholder
                    "Placeholder User",        // Placeholder
                    "UNKNOWN_ROLE"             // Placeholder
            );
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder()); // PasswordEncoder é usado aqui, mas para validação de senha, não para login JWT
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}