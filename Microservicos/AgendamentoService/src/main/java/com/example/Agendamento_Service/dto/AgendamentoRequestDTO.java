package com.example.Agendamento_Service.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

// Using Java Record for conciseness and immutability
public record AgendamentoRequestDTO(

        @NotNull(message = "O ID do serviço é obrigatório.")
        UUID servicoId,
        UUID atendenteId, // Pode ser nulo
        @NotNull(message = "A data e hora do agendamento são obrigatórias.")
        @FutureOrPresent(message = "A data e hora do agendamento devem ser de hoje para frente")
        LocalDateTime dataHora,
        String observacoes
) {
    // Records automatically provide constructor, getters, equals, hashCode, toString.
    // No need for manual getters/setters or default constructor.
}