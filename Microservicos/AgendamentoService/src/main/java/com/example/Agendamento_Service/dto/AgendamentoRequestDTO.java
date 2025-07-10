package com.example.Agendamento_Service.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

// Using Java Record for conciseness and immutability
public record AgendamentoRequestDTO(
    @NotNull(message = "O ID do usuário é obrigatório.")
    UUID usuarioId,

    // Remove @NotNull if 'atendenteId' can be null initially, aligning with the model
    // @NotNull(message = "O ID do atendente é obrigatório.")
    UUID atendenteId,

    @NotNull(message = "O ID do serviço é obrigatório.")
    UUID servicoId,

    @NotNull(message = "A data e hora do agendamento são obrigatórias.")
    @FutureOrPresent(message = "A data e hora do agendamento devem ser de hoje para frente")
    LocalDateTime dataHora
) {
    // Records automatically provide constructor, getters, equals, hashCode, toString.
    // No need for manual getters/setters or default constructor.
}