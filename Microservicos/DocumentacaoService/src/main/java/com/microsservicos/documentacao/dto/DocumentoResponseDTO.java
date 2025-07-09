package com.microsservicos.documentacao.dto;

import com.microsservicos.documentacao.enums.StatusDocumento;
import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentoResponseDTO(
        UUID id,
        UUID triagemId,
        UUID agendamentoId, // NOVO CAMPO
        UUID documentoCatalogoId,
        String nomeOriginalArquivo,
        String caminhoArmazenamento,
        StatusDocumento status,
        String urlVisualizacao,
        String observacaoValidacao,
        LocalDateTime dataUpload,
        LocalDateTime dataValidacao
) {}