package com.microsservicos.triagem.client;

import java.util.UUID;

public record ServicoResponse(UUID id, String nome, Integer duracaoEstimadaMinutos) {} // Adicione campos relevantes