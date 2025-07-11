package com.microservicos.authservice.dto;

import jakarta.validation.constraints.NotBlank;
public record LoginRequestDTO(
        @NotBlank(message = "O name de usuário ou email é obrigatório.")
        String email,
        @NotBlank(message = "A senha é obrigatória.")
        String senha
) {}