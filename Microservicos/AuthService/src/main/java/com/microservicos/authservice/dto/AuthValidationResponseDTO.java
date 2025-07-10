package com.microservicos.authservice.dto; // Ficará no pacote do client Feign, pois é um DTO de resposta de outro serviço

import java.util.UUID;

// Retorno simplificado dos serviços de cadastro para o AuthService
public record AuthValidationResponseDTO(
        UUID userId,
        String username, // Para uso no JWT subject
        String email,
        String role,     // A role do usuário (ex: "CLIENTE", "FUNCIONARIO")
        // Você pode adicionar outros "claims" que queira incluir no JWT aqui
        // Ex: String nomeCompleto, List<String> permissoesAdicionais
        String fullName
) {}