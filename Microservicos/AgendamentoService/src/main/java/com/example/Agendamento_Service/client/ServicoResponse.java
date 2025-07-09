package com.example.Agendamento_Service.client;

import java.util.UUID;
import java.util.List;

public record ServicoResponse(
    UUID id,
    String nome,
    String descricao,
    boolean isAtivo,
    int prioridade,
    Integer tempoMedioMinutos,
    List<UUID> documentosObrigatoriosIds // Este campo é crucial!
) {}