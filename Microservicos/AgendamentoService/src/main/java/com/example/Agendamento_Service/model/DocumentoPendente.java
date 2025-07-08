package com.example.Agendamento_Service.model;


import com.example.Agendamento_Service.enums.StatusDocumento;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "documentos_pendentes") // É uma boa prática nomear a tabela no plural
public class DocumentoPendente {

    @Id
    private UUID id;

    // Relacionamento de volta para a Agendamento
    @ManyToOne(fetch = FetchType.LAZY) // LAZY para não carregar a agendamento desnecessariamente
    @JoinColumn(name = "agendamento_id", nullable = false)
    private Agendamento agendamento;

    // ID do tipo de documento no Catálogo-Service ou Documentacao-Service
    @Column(nullable = false)
    private UUID documentoCatalogoId;

    // Snapshot do nome do documento para resiliência
    @Column(nullable = false)
    private String nomeDocumentoSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDocumento status;

    // Opcional: para armazenar o motivo de uma recusa, por exemplo
    private String observacao;

    private String urlDocumento; // Ou String idDocumentoExterno;

    public DocumentoPendente() {
        this.id = UUID.randomUUID();
    }

    public DocumentoPendente(UUID documentoCatalogoId, String nomeDocumentoSnapshot, StatusDocumento status) {
        this.id = UUID.randomUUID();
        this.documentoCatalogoId = documentoCatalogoId;
        this.nomeDocumentoSnapshot = nomeDocumentoSnapshot;
        this.status = status;
    }


    // Getters e Setters
    public UUID getId() { // Alterado o retorno para UUID
        return id;
    }

    public void setId(UUID id) { // Alterado o parâmetro para UUID
        this.id = id;
    }

    public Agendamento getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Agendamento agendamento) {
        this.agendamento = agendamento;
    }

    public UUID getDocumentoCatalogoId() {
        return documentoCatalogoId;
    }

    public void setDocumentoCatalogoId(UUID documentoCatalogoId) {
        this.documentoCatalogoId = documentoCatalogoId;
    }

    public String getNomeDocumentoSnapshot() {
        return nomeDocumentoSnapshot;
    }

    public void setNomeDocumentoSnapshot(String nomeDocumentoSnapshot) {
        this.nomeDocumentoSnapshot = nomeDocumentoSnapshot;
    }

    public StatusDocumento getStatus() {
        return status;
    }

    public void setStatus(StatusDocumento status) {
        this.status = status;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getUrlDocumento() {
        return urlDocumento;
    }

    public void setUrlDocumento(String urlDocumento) {
        this.urlDocumento = urlDocumento;
    }
}