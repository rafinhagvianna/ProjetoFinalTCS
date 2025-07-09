package com.microsservicos.triagem.client;


import java.util.List;
import java.util.UUID;

// Esta Record deve ser EXATAMENTE igual à SetorResponse do seu Catalogo-Service
public record SetorResponse(
    UUID id,
    String nome,
    String descricao,
    boolean isAtivo,
    int prioridade,
    Integer tempoMedioMinutos,
    List<UUID> documentosObrigatoriosIds 
) {}