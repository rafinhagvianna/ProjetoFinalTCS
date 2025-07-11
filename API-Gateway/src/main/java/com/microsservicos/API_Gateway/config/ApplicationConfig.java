// api-gateway/src/main/java/com/microsservicos/API_Gateway/config/ApplicationConfig.java
package com.microsservicos.API_Gateway.config;

import com.microsservicos.API_Gateway.security.CustomUserDetails; // Importe seu CustomUserDetails
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
public class ApplicationConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        // No Gateway, o UserDetailsService é usado para carregar o UserDetails
        // a partir do 'subject' do token JWT, que já foi validado pelo JwtAuthenticationFilter.
        // Ele não faz uma busca em DB, mas constrói o objeto UserDetails.
        return username -> {
            try {
                // O subject do token é o UUID do usuário.
                UUID userId = UUID.fromString(username);
                // Retorne um CustomUserDetails com os dados que você esperaria do token.
                // Aqui, você pode ter que buscar os detalhes completos (email, fullName, role)
                // de um cache ou de um serviço de perfil se eles não estiverem no token.
                // Para o Gateway, se o JWT já tem os claims, você pode construir direto.
                // Mas o JwtAuthenticationFilter já faz isso. Então, este UserDetailsService
                // pode ser mais um placeholder ou usado para cenários de fallback.
                // Se o JwtAuthenticationFilter já preenche o SecurityContextHolder com CustomUserDetails,
                // este UserDetailsService pode não ser invocado para requisições com token válido.
                // No entanto, ele é necessário para a configuração do AuthenticationProvider.
                return new CustomUserDetails(userId, username + "@example.com", "User " + username, "USER");
            } catch (IllegalArgumentException e) {
                throw new UsernameNotFoundException("Invalid user ID format: " + username, e);
            }
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // O Gateway não precisa de um PasswordEncoder para validar senhas,
        // mas o DaoAuthenticationProvider o exige. Pode ser um BCryptPasswordEncoder dummy.
        return new BCryptPasswordEncoder();
    }
}