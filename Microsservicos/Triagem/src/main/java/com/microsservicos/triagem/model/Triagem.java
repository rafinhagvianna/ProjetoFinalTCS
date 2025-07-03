package com.microsservicos.triagem.model;

import com.microsservicos.triagem.enums.StatusTriagem;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "triagens") // Nome da tabela no plural
public class Triagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clienteId;

    @Column(nullable = false)
    private Long servicoId;

    private LocalDateTime horarioSolicitacao;
    private LocalDateTime horarioEstimadoAtendimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTriagem status;

    private Integer tempoEstimadoMinutos;
    private Integer prioridade;

    private String nomeClienteSnapshot;
    private String nomeServicoSnapshot;

    // --- NOVA PARTE ---
    // Relacionamento com os documentos pendentes
    @OneToMany(
            mappedBy = "triagem", // 'triagem' é o nome do campo na classe DocumentoPendente
            cascade = CascadeType.ALL, // Salva/atualiza/deleta os documentos junto com a triagem
            orphanRemoval = true // Remove do banco um DocumentoPendente se ele for removido desta lista
    )
    private List<DocumentoPendente> documentosPendentes = new ArrayList<>();

    // --- FIM DA NOVA PARTE ---

    // Getters e Setters para todos os campos, incluindo a nova lista

    public void addDocumentoPendente(DocumentoPendente documento) {
        this.documentosPendentes.add(documento);
        documento.setTriagem(this);
    }

    public void removeDocumentoPendente(DocumentoPendente documento) {
        this.documentosPendentes.remove(documento);
        documento.setTriagem(null);
    }

    // ... todos os outros getters e setters ...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getServicoId() {
        return servicoId;
    }

    public void setServicoId(Long servicoId) {
        this.servicoId = servicoId;
    }

    public LocalDateTime getHorarioSolicitacao() {
        return horarioSolicitacao;
    }

    public void setHorarioSolicitacao(LocalDateTime horarioSolicitacao) {
        this.horarioSolicitacao = horarioSolicitacao;
    }

    public LocalDateTime getHorarioEstimadoAtendimento() {
        return horarioEstimadoAtendimento;
    }

    public void setHorarioEstimadoAtendimento(LocalDateTime horarioEstimadoAtendimento) {
        this.horarioEstimadoAtendimento = horarioEstimadoAtendimento;
    }

    public StatusTriagem getStatus() {
        return status;
    }

    public void setStatus(StatusTriagem status) {
        this.status = status;
    }

    public Integer getTempoEstimadoMinutos() {
        return tempoEstimadoMinutos;
    }

    public void setTempoEstimadoMinutos(Integer tempoEstimadoMinutos) {
        this.tempoEstimadoMinutos = tempoEstimadoMinutos;
    }

    public Integer getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Integer prioridade) {
        this.prioridade = prioridade;
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

    public List<DocumentoPendente> getDocumentosPendentes() {
        return documentosPendentes;
    }

    public void setDocumentosPendentes(List<DocumentoPendente> documentosPendentes) {
        this.documentosPendentes = documentosPendentes;
    }
}