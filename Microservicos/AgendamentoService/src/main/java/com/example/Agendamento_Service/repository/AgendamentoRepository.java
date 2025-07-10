package com.example.Agendamento_Service.repository;

import com.example.Agendamento_Service.enums.StatusAgendamento;
import com.example.Agendamento_Service.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {
    List<Agendamento> findByDataHoraBetween(LocalDateTime start, LocalDateTime end);
    Optional<Agendamento> findByUsuarioIdAndServicoIdAndDataHora(UUID usuarioId, UUID servicoId, LocalDateTime dataHora);
    List<Agendamento> findByUsuarioIdAndStatus(UUID usuarioId, StatusAgendamento statusAgendamento);
}
