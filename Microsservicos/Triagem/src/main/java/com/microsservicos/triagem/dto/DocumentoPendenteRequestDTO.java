package com.microsservicos.triagem.dto;

import com.microsservicos.triagem.enums.StatusDocumento;

public record DocumentoPendenteRequestDTO(
        Long documentoCatalogoId,
        String nomeDocumentoSnapshot,
        StatusDocumento status,
        String observacao
) { }