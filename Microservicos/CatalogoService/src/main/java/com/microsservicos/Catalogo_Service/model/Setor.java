package com.microsservicos.Catalogo_Service.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(value = "setor")
public class Setor {

    @Id
    private UUID id;
    private String nome;
    private String descricao;
    private boolean isAtivo;
    private int prioridade; // 1 -> alta;
    private Integer tempoMedioMinutos;
    private List<UUID> documentosObrigatoriosIds = new ArrayList<>(); // Initialize to avoid NullPointerException


    public Setor() {
    }

    public Setor(UUID id, String nome, String descricao, boolean isAtivo, int prioridade, List<UUID> documentosObrigatoriosIds) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.isAtivo = isAtivo;
        this.prioridade = prioridade;
        this.documentosObrigatoriosIds = documentosObrigatoriosIds;
    }
    public Setor(UUID id, String nome, String descricao, boolean isAtivo, int prioridade) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.isAtivo = isAtivo;
        this.prioridade = prioridade;
    }

    // Getters e Setters

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Integer getTempoMedioMinutos() {
        return tempoMedioMinutos;
    }

    public void setTempoMedioMinutos(Integer tempoMedioMinutos) {
        this.tempoMedioMinutos = tempoMedioMinutos;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isAtivo() {
        return isAtivo;
    }

    public void setAtivo(boolean ativo) {
        isAtivo = ativo;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public List<UUID> getDocumentosObrigatoriosIds() {
        return documentosObrigatoriosIds;
    }

    public void setDocumentosObrigatoriosIds(List<UUID> documentosObrigatoriosIds) {
        this.documentosObrigatoriosIds = documentosObrigatoriosIds != null ? new ArrayList<>(documentosObrigatoriosIds) : new ArrayList<>();
    }
}
