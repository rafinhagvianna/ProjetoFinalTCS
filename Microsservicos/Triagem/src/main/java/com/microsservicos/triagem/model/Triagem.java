package com.microsservicos.triagem.model;

import com.microsservicos.triagem.enums.StatusTriagem;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Triagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clienteId;
    private Long servicoId;

    private LocalDateTime horarioSolicitacao;
    private LocalDateTime horarioEstimadoAtendimento;

    @Enumerated(EnumType.STRING)
    private StatusTriagem status;

    private Integer tempoEstimadoMinutos;
    private Integer prioridade;

    private String nomeClienteSnapshot;
    private String nomeServicoSnapshot;

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
}