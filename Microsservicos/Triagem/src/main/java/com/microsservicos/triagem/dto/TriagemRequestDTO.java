package com.microsservicos.triagem.dto;

import java.util.List;

public record TriagemRequestDTO(
        Long clienteId,
        Long servicoId,
        Integer prioridade,
        List<DocumentoPendenteRequestDTO> documentosPendentes
) { }