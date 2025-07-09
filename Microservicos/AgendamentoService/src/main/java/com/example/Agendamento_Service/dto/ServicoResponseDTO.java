package com.example.Agendamento_Service.dto;

import java.util.UUID;

public class ServicoResponseDTO {
    private UUID id;
    private String nome; // Opcional, se você precisar do nome para logs ou mensagens
    private Integer tempoEstimadoEmMinutos;

    public ServicoResponseDTO() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getTempoEstimadoEmMinutos() {
        return tempoEstimadoEmMinutos;
    }

    public void setTempoEstimadoEmMinutos(Integer tempoEstimadoEmMinutos) {
        this.tempoEstimadoEmMinutos = tempoEstimadoEmMinutos;
    }
}