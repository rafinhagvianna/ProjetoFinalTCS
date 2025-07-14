package com.microsservicos.TarefasFuncionario_Service.dto;

import java.util.UUID;

public record TarefaResponse(
    UUID id,
    String nome,
    String descricao
)
{}
