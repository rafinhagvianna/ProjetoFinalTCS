package com.microsservicos.documentacao.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record UploadDocumentoRequestDTO(
        UUID triagemId,
        UUID agendamentoId, // NOVO CAMPO: Pode ser null
        @NotNull UUID documentoCatalogoId,
        @NotNull MultipartFile arquivo
) {}