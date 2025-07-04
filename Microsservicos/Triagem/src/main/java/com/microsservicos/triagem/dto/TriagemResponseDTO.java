package com.microsservicos.triagem.dto;

import com.microsservicos.triagem.enums.StatusTriagem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TriagemResponseDTO(
        UUID id,
        UUID clienteId,
        UUID servicoId,
        String nomeClienteSnapshot,
        String nomeServicoSnapshot,
        StatusTriagem status,
        LocalDateTime horarioSolicitacao,
        LocalDateTime horarioEstimadoAtendimento,
        Integer tempoEstimadoMinutos,
        Integer prioridade,
        List<DocumentoPendenteResponseDTO> documentosPendentes
) { }