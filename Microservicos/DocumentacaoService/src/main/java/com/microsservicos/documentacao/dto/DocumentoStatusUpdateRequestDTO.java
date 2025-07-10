package com.microsservicos.documentacao.dto;

import com.microsservicos.documentacao.enums.StatusDocumento;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record DocumentoStatusUpdateRequestDTO(
        @NotNull UUID documentoId, // ID do documento no documentacao-service
        @NotNull StatusDocumento status,
        String urlVisualizacao, // URL para acesso ao documento
        String observacaoValidacao // Observação, ex: motivo da rejeição
) {}