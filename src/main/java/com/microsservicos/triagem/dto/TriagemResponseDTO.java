package com.microsservicos.triagem.dto;

import com.microsservicos.triagem.enums.StatusTriagem;

import java.time.LocalDateTime;

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
        Integer prioridade
) { }
