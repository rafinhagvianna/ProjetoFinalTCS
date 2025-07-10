package com.microservicos.authservice.dto;

import jakarta.validation.constraints.NotBlank;
public record LoginRequestDTO(
        @NotBlank(message = "O nome de usuário ou email é obrigatório.")
        String usernameOrEmail,
        @NotBlank(message = "A senha é obrigatória.")
        String password
) {}