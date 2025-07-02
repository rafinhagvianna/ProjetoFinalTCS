package com.microsservicos.triagem.dto;

public record TriagemRequestDTO(
        Long clienteId,
        Long servicoId,
        Integer prioridade
) { }
