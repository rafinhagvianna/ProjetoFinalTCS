package com.microservicos.authservice.dto;


public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        String userId,
        String role
) {}
