package com.microsservicos.Catalogo_Service.dto;

import java.util.UUID;

public record SetorRequest(UUID id, String nome, String descricao, boolean isAtivo, int prioridade, Integer tempoMedioMinutos) {

}
