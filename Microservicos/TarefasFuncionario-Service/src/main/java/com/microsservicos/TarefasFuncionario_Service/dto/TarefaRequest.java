package com.microsservicos.TarefasFuncionario_Service.dto;

import java.util.UUID;

public record TarefaRequest(
        UUID id,
        String nome,
        String descricao
) {
}
