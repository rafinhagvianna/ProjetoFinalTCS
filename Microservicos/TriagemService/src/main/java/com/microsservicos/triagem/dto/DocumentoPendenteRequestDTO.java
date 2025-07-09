package com.microsservicos.triagem.dto;

import com.microsservicos.triagem.enums.StatusDocumento;

import java.util.UUID;

public record DocumentoPendenteRequestDTO(
        UUID documentoCatalogoId,
        String nomeDocumentoSnapshot,
        StatusDocumento status,
        String observacao
) { }