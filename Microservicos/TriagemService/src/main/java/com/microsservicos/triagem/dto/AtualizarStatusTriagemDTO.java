package com.microsservicos.triagem.dto;

import com.microsservicos.triagem.enums.StatusTriagem;

public record AtualizarStatusTriagemDTO(
        StatusTriagem novoStatus
) { }
