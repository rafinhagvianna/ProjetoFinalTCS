package com.microsservicos.Catalogo_Service.dto;

import java.util.UUID;

public record DocumentoCatalogoResponse(
    UUID id,
    String nome,
    String descricao,
    boolean isObrigatorioPorPadrao,
    boolean isAtivo
) {}