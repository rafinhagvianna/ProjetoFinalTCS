package com.microsservicos.triagem.dto;

import com.microsservicos.triagem.enums.StatusDocumento;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DocumentoStatusUpdateRequestDTO(
    @NotNull UUID documentoId, // O ID do documento no documentacao-service
    @NotNull StatusDocumento status, // O novo status do documento
    String urlVisualizacao, // A URL para acessar o documento
    String observacaoValidacao // Qualquer observação, especialmente para status de rejeição
) {}