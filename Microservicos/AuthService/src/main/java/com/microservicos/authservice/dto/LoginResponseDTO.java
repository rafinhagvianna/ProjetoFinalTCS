package com.microservicos.authservice.dto;


import java.util.UUID;

public record LoginResponseDTO(
        String message,
        String accessToken,
        String refreshToken,
        String nome,
        String email
) {

}
