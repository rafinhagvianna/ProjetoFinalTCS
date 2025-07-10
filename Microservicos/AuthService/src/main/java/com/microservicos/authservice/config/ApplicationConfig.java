package com.microservicos.authservice.config;

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

@Configuration
public class ApplicationConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        // No Auth Service, este UserDetailsService pode ser "simplificado"
        // para propósitos de segurança interna após o login inicial.
        // Ele não é para buscar usuários do seu DB local, mas para o Spring Security
        // poder construir um UserDetails a partir do Subject do token JWT
        // (que é o ID do usuário, por exemplo).
        return username -> {
            // Este é um placeholder. Você pode adaptar isso para buscar UserDetails de um cache
            // ou de alguma forma de armazenamento temporário após a autenticação via Feign,
            // ou simplesmente retornar um UserDetails com base nas roles extraídas do token.
            // Para a validação do token, se o subject for um UUID, adapte a lógica.
            // Por enquanto, para evitar erros de compilação, vamos retornar um UserDetails mínimo.
            // A autenticação real para login ainda acontece no AuthService chamando outros microserviços.
            throw new UsernameNotFoundException("User not found via UserDetailsService for Auth Service. This is normal if authentication happens externally.");
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
        return new BCryptPasswordEncoder();
    }
}