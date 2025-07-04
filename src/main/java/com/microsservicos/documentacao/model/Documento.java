package com.microsservicos.documentacao.model;

import com.microsservicos.documentacao.enums.StatusDocumento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documentos")
public class Documento {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    // IDs de referência
    @Column(name = "triagem_id", nullable = true) // Pode ser null
    private UUID triagemId; // Referência à Triagem

    @Column(name = "agendamento_id", nullable = true) // NOVO CAMPO: Pode ser null
    private UUID agendamentoId; // Referência ao Agendamento

    @Column(name = "documento_catalogo_id", nullable = true) // Pode ser null
    private UUID documentoCatalogoId; // Referência ao tipo de documento no Catálogo

    @NotNull
    @Column(nullable = false, length = 255)
    private String nomeOriginalArquivo;

    @NotNull
    @Column(nullable = false, length = 500)
    private String caminhoArmazenamento;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusDocumento status;

    @Column(length = 2048)
    private String urlVisualizacao;

    @Column(length = 500)
    private String observacaoValidacao;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataUpload;

    private LocalDateTime dataValidacao;


    public Documento() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTriagemId() {
        return triagemId;
    }

    public void setTriagemId(UUID triagemId) {
        this.triagemId = triagemId;
    }

    public UUID getDocumentoCatalogoId() {
        return documentoCatalogoId;
    }

    public void setDocumentoCatalogoId(UUID documentoCatalogoId) {
        this.documentoCatalogoId = documentoCatalogoId;
    }

    public String getNomeOriginalArquivo() {
        return nomeOriginalArquivo;
    }

    public void setNomeOriginalArquivo(String nomeOriginalArquivo) {
        this.nomeOriginalArquivo = nomeOriginalArquivo;
    }

    public String getCaminhoArmazenamento() {
        return caminhoArmazenamento;
    }

    public void setCaminhoArmazenamento(String caminhoArmazenamento) {
        this.caminhoArmazenamento = caminhoArmazenamento;
    }

    public StatusDocumento getStatus() {
        return status;
    }

    public void setStatus(StatusDocumento status) {
        this.status = status;
    }

    public String getUrlVisualizacao() {
        return urlVisualizacao;
    }

    public void setUrlVisualizacao(String urlVisualizacao) {
        this.urlVisualizacao = urlVisualizacao;
    }

    public String getObservacaoValidacao() {
        return observacaoValidacao;
    }

    public void setObservacaoValidacao(String observacaoValidacao) {
        this.observacaoValidacao = observacaoValidacao;
    }

    public LocalDateTime getDataUpload() {
        return dataUpload;
    }

    public void setDataUpload(LocalDateTime dataUpload) {
        this.dataUpload = dataUpload;
    }

    public LocalDateTime getDataValidacao() {
        return dataValidacao;
    }

    public void setDataValidacao(LocalDateTime dataValidacao) {
        this.dataValidacao = dataValidacao;
    }

    public UUID getAgendamentoId() {
        return agendamentoId;
    }

    public void setAgendamentoId(UUID agendamentoId) {
        this.agendamentoId = agendamentoId;
    }
}