package com.example.Agendamento_Service.client;

import java.util.UUID;

public record DocumentoCatalogoResponse(
    UUID id,
    String nome,
    String descricao,
    boolean isObrigatorioPorPadrao,
    boolean isAtivo
) {}
