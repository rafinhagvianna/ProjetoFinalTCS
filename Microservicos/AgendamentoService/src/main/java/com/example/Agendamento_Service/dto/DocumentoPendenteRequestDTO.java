package com.example.Agendamento_Service.dto;

import com.example.Agendamento_Service.enums.StatusDocumento;

import java.util.UUID;

public record DocumentoPendenteRequestDTO(
        UUID documentoCatalogoId,
        String nomeDocumentoSnapshot,
        StatusDocumento status,
        String observacao
) { }