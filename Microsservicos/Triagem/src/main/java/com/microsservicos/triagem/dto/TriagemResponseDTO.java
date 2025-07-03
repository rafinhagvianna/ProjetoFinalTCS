package com.microsservicos.triagem.dto;

import com.microsservicos.triagem.enums.StatusTriagem;

import java.time.LocalDateTime;
import java.util.List;

public record TriagemResponseDTO(
        Long id,
        Long clienteId,
        Long servicoId,
        String nomeClienteSnapshot,
        String nomeServicoSnapshot,
        StatusTriagem status,
        LocalDateTime horarioSolicitacao,
        LocalDateTime horarioEstimadoAtendimento,
        Integer tempoEstimadoMinutos,
        Integer prioridade,
        List<DocumentoPendenteResponseDTO> documentosPendentes
) { }