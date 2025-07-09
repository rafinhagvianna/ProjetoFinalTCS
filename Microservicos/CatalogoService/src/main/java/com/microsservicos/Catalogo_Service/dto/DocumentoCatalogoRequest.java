package com.microsservicos.Catalogo_Service.dto;

import java.util.UUID;

public record DocumentoCatalogoRequest(
    String nome,
    String descricao,
    boolean isObrigatorioPorPadrao,
    boolean isAtivo
) {}