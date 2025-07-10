package com.microservicos.authservice.config;

import org.springframework.context.annotation.Configuration;

// Pode ser usado para @Bean de PasswordEncoder se fosse necessário aqui
// Mas neste modelo, PasswordEncoder estará nos serviços de cadastro.
@Configuration
public class JwtConfig {
    // A chave secreta e o tempo de expiração são injetados diretamente no JwtService via @Value.
    // Esta classe pode ficar vazia ou ter outras configurações relacionadas se necessário.
}