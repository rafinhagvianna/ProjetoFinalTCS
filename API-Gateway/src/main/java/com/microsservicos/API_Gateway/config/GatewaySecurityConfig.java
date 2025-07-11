// api-gateway/src/main/java/com/microsservicos/API_Gateway/config/GatewaySecurityConfig.java
package com.microsservicos.API_Gateway.config;

import com.microsservicos.API_Gateway.jwt.JwtAuthenticationFilter;

import org.springframework.http.HttpMethod; 

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider; // Importe
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class GatewaySecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider; // Injetado para ser usado pelo Spring Security

    public GatewaySecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Desabilita CSRF para APIs REST
                .authorizeHttpRequests(auth -> auth
                        // Permite acesso público ao endpoint de login do Auth Service
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/cliente").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Todos os outros endpoints requerem autenticação (via JWT)
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Sessões sem estado (para JWT)
                )
                .authenticationProvider(authenticationProvider) // Define o provedor de autenticação
                // Adiciona o filtro JWT ANTES do filtro de autenticação de usuário/senha padrão
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}