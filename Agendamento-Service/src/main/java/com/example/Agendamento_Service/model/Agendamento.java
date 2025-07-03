package com.example.Agendamento_Service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "atendente_id", nullable = false)
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
}