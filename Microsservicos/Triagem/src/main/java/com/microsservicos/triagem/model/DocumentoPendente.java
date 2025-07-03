package com.microsservicos.triagem.model;

import com.microsservicos.triagem.enums.StatusDocumento;
import jakarta.persistence.*;

@Entity
@Table(name = "documentos_pendentes") // É uma boa prática nomear a tabela no plural
public class DocumentoPendente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento de volta para a Triagem
    @ManyToOne(fetch = FetchType.LAZY) // LAZY para não carregar a triagem desnecessariamente
    @JoinColumn(name = "triagem_id", nullable = false)
    private Triagem triagem;

    // ID do tipo de documento no Catálogo-Service ou Documentacao-Service
    @Column(nullable = false)
    private Long documentoCatalogoId;

    // Snapshot do nome do documento para resiliência
    @Column(nullable = false)
    private String nomeDocumentoSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusDocumento status;

    // Opcional: para armazenar o motivo de uma recusa, por exemplo
    private String observacao;

    private String urlDocumento; // Ou String idDocumentoExterno;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Triagem getTriagem() {
        return triagem;
    }

    public void setTriagem(Triagem triagem) {
        this.triagem = triagem;
    }

    public Long getDocumentoCatalogoId() {
        return documentoCatalogoId;
    }

    public void setDocumentoCatalogoId(Long documentoCatalogoId) {
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