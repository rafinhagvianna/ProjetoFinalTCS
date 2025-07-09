// Em: com.microsservicos.CadastroClienteService.config.SecurityConfig.java

package com.microsservicos.CadastroClienteService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita a proteção CSRF, que não é necessária para APIs REST stateless
                .csrf(AbstractHttpConfigurer::disable)
                // Configura o gerenciamento de sessão para ser stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configura as regras de autorização para as requisições HTTP
                .authorizeHttpRequests(authorize -> authorize
                        // Permite acesso a todos os endpoints sob /api/cliente sem autenticação
                        // (Isso inclui /api/cliente e /api/cliente/login)
                        .requestMatchers("/api/cliente/**").permitAll()
                        // Qualquer outra requisição precisa ser autenticada (vamos configurar isso depois com JWT)
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}