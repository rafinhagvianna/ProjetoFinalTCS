package com.example.Agendamento_Service.repository;

import com.example.Agendamento_Service.dto.ContagemPorItemDTO;
import com.example.Agendamento_Service.enums.StatusAgendamento;
import com.example.Agendamento_Service.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {
    List<Agendamento> findByDataHoraBetween(LocalDateTime start, LocalDateTime end);
    Optional<Agendamento> findByUsuarioIdAndServicoIdAndDataHora(UUID usuarioId, UUID servicoId, LocalDateTime dataHora);
    List<Agendamento> findByUsuarioIdAndStatus(UUID usuarioId, StatusAgendamento statusAgendamento);
    Optional<Agendamento> findFirstByUsuarioIdAndStatusAndDataHoraAfterOrderByDataHoraAsc(
            UUID usuarioId, StatusAgendamento status, LocalDateTime dataAtual);

    @Query("SELECT new com.example.Agendamento_Service.dto.ContagemPorItemDTO(a.nomeServicoSnapshot, COUNT(a)) " +
            "FROM Agendamento a WHERE a.status = com.example.Agendamento_Service.enums.StatusAgendamento.CONCLUIDO " +
            "GROUP BY a.nomeServicoSnapshot ORDER BY COUNT(a) DESC")
    List<ContagemPorItemDTO> contarAtendimentosPorServico();
}
