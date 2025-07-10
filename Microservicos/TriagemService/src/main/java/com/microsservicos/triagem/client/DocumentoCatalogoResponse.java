package com.microsservicos.triagem.client;

import java.util.UUID;

// Esta Record deve ser EXATAMENTE igual à DocumentoCatalogoResponse do seu Catalogo-Service
public record DocumentoCatalogoResponse(
    UUID id,
    String nome,
    String descricao,
    boolean isObrigatorioPorPadrao,
    boolean isAtivo
) {}