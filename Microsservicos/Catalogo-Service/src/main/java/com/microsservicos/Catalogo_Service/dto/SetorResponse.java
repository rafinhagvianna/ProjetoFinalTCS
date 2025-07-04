package com.microsservicos.Catalogo_Service.dto;

public record SetorResponse(java.util.UUID id, String nome, String descricao, boolean isAtivo, int prioridade, Integer tempoMedioMinutos) {
}
