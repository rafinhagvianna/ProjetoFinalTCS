package com.example.Agendamento_Service.dto;

import com.example.Agendamento_Service.enums.StatusDocumento;

import java.util.UUID;

public record DocumentoPendenteResponseDTO(
        UUID id,
        UUID documentoCatalogoId,
        String nomeDocumentoSnapshot,
        StatusDocumento status,
        String observacao,
        String urlDocumento) { }