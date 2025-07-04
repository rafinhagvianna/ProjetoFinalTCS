package com.example.Agendamento_Service.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class AgendamentoRequestDTO {

    @NotNull(message = "O ID do usuário é obrigatório.")
    private UUID usuarioId;

    @NotNull(message = "O ID do atendente é obrigatório.")
    private UUID atendenteId;

    @NotNull(message = "O ID do serviço é obrigatório.")
    private UUID servicoId;

    @NotNull(message = "A data e hora do agendamento são obrigatórias.")
    @FutureOrPresent(message = "A data e hora do agendamento devem ser de hoje para frente")
    private LocalDateTime dataHora;

    public AgendamentoRequestDTO() {
    }

    public AgendamentoRequestDTO(UUID usuarioId, UUID atendenteId, UUID servicoId, LocalDateTime dataHora) {
        this.usuarioId = usuarioId;
        this.atendenteId = atendenteId;
        this.servicoId = servicoId;
        this.dataHora = dataHora;
    }

    // Getters e Setters
    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public UUID getAtendenteId() {
        return atendenteId;
    }

    public void setAtendenteId(UUID atendenteId) {
        this.atendenteId = atendenteId;
    }

    public UUID getServicoId() {
        return servicoId;
    }

    public void setServicoId(UUID servicoId) {
        this.servicoId = servicoId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}
