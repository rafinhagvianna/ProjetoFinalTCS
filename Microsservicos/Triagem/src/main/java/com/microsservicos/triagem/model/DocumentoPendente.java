package com.microsservicos.triagem.model;

import com.microsservicos.triagem.enums.StatusDocumento;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "documentos_pendentes") // É uma boa prática nomear a tabela no plural
public class DocumentoPendente {

    @Id
    private UUID id;

    // Relacionamento de volta para a Triagem
    @ManyToOne(fetch = FetchType.LAZY) // LAZY para não carregar a triagem desnecessariamente
    @JoinColumn(name = "triagem_id", nullable = false)
    private Triagem triagem;

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

    // Getters e Setters
    public UUID getId() { // Alterado o retorno para UUID
        return id;
    }

    public void setId(UUID id) { // Alterado o parâmetro para UUID
        this.id = id;
    }

    public Triagem getTriagem() {
        return triagem;
    }

    public void setTriagem(Triagem triagem) {
        this.triagem = triagem;
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