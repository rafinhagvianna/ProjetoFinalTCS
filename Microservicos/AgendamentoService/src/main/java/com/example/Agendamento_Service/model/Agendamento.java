package com.example.Agendamento_Service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.Agendamento_Service.enums.StatusAgendamento;

@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "atendente_id")
    private UUID atendenteId;

    @Column(name = "servico_id", nullable = false)
    private UUID servicoId;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "atendido_em")
    private LocalDateTime atendidoEm;

    @Column(name = "observacoes")
    private String observacoes;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm = LocalDateTime.now();

    @OneToMany(
            mappedBy = "agendamento", // 'triagem' é o nome do campo na classe DocumentoPendente
            cascade = CascadeType.ALL, // Salva/atualiza/deleta os documentos junto com a triagem
            orphanRemoval = true // Remove do banco um DocumentoPendente se ele for removido desta lista
    )
    private List<DocumentoPendente> documentosPendentes = new ArrayList<>();

    @Column(name = "nome_cliente_snapshot")
    private String nomeClienteSnapshot;

    @Column(name = "nome_servico_snapshot")
    private String nomeServicoSnapshot;

    @Enumerated(EnumType.STRING) // Adicione um status para o Agendamento
    @Column(name = "status_agendamento")
    private StatusAgendamento status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public UUID getAtendenteId() {
        return atendenteId;
    }

    public void setAtendenteId(UUID atendenteId) {
        this.atendenteId = atendenteId;
    }

    public UUID getServicoId() {
        return servicoId;
    }

    public void setServicoId(UUID servicoId) {
        this.servicoId = servicoId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public LocalDateTime getAtendidoEm() {
        return atendidoEm;
    }

    public void setAtendidoEm(LocalDateTime atendidoEm) {
        this.atendidoEm = atendidoEm;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getNomeClienteSnapshot() {
        return nomeClienteSnapshot;
    }

    public void setNomeClienteSnapshot(String nomeClienteSnapshot) {
        this.nomeClienteSnapshot = nomeClienteSnapshot;
    }

    public String getNomeServicoSnapshot() {
        return nomeServicoSnapshot;
    }

    public void setNomeServicoSnapshot(String nomeServicoSnapshot) {
        this.nomeServicoSnapshot = nomeServicoSnapshot;
    }

    public StatusAgendamento getStatus(){
        return status;
    }

    public void setStatus(StatusAgendamento status){
        this.status = status;
    }

    public List<DocumentoPendente> getDocumentosPendentes() {
        return documentosPendentes;
    }

    public void setDocumentosPendentes(List<DocumentoPendente> documentosPendentes) {
        this.documentosPendentes = documentosPendentes;
    }

    public void addDocumentoPendente(DocumentoPendente documento) {
        this.documentosPendentes.add(documento);
        documento.setAgendamento(this);
    }

    public void removeDocumentoPendente(DocumentoPendente documento) {
        this.documentosPendentes.remove(documento);
        documento.setAgendamento(null); // Detach from the parent
    }
}