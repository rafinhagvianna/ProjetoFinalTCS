package com.microservicos.authservice.dto;


import java.util.UUID;

public record LoginResponseDTO(
        String message,
        String accessToken,
        String refreshToken,
        String nome,
        String email,
        UUID id,    // <-- Talvez tenha que comentar
        String role // <-- ADICIONE ESTA LINHA
) {

}
