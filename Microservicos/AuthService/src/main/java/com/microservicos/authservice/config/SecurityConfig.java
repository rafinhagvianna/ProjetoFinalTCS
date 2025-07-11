package com.microservicos.authservice.config;

import com.microservicos.authservice.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider; // Mantenha o import
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    // Remova a injeção direta do AuthenticationProvider aqui se ele não for usado diretamente no http.authenticationProvider()
    // ou se você confiar que o filtro JWT o usará implicitamente.
    // Para simplificar, vamos injetar apenas o filtro JWT e deixar o AuthenticationManager
    // ser configurado separadamente via ApplicationConfig.

    // Remova o construtor gerado pelo Lombok e use um construtor manual
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll() 
                        .requestMatchers("/api/cliente").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll() 
                        .anyRequest().authenticated() // Qualquer outra requisição requer autenticação (via JWT)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Remova .authenticationProvider(authenticationProvider) daqui,
                // pois o JwtAuthenticationFilter e o AuthenticationManager (via ApplicationConfig)
                // já cuidarão da autenticação quando um token estiver presente.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}