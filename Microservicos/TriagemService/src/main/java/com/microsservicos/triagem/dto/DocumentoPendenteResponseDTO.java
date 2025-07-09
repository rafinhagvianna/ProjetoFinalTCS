package com.microsservicos.triagem.dto;

import com.microsservicos.triagem.enums.StatusDocumento;

import java.util.UUID;

public record DocumentoPendenteResponseDTO(
        UUID id,
        UUID documentoCatalogoId,
        String nomeDocumentoSnapshot,
        StatusDocumento status,
        String observacao,
        String urlDocumento) { }