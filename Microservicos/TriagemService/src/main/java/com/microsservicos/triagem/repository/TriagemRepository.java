package com.microsservicos.triagem.repository;

import com.microsservicos.triagem.dto.ContagemPorDataDTO;
import com.microsservicos.triagem.dto.ContagemPorItemDTO;
import com.microsservicos.triagem.enums.StatusDocumento;
import com.microsservicos.triagem.enums.StatusTriagem;
import com.microsservicos.triagem.model.Triagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TriagemRepository extends JpaRepository<Triagem, UUID> {

    List<Triagem> findByStatus(StatusTriagem status);
    
    List<Triagem> findByStatusOrStatusOrderByPrioridadeAscHorarioSolicitacaoAsc(StatusTriagem status1, StatusTriagem status2);

    Optional<Triagem> findFirstByStatusOrderByPrioridadeAscHorarioSolicitacaoAsc(StatusTriagem status);

    Optional<Triagem> findTopByOrderByHorarioEstimadoAtendimentoDesc();

    Triagem findByClienteIdAndStatus(UUID clienteId, StatusTriagem statusTriagem);

    List<Triagem> findByStatusOrderByHorarioSolicitacaoDesc(StatusTriagem status);

    Optional<Triagem> findFirstByClienteIdAndStatusOrderByHorarioSolicitacaoAsc(UUID clienteId, StatusTriagem status);


    // Query para o gráfico 1: Serviços mais utilizados
    @Query("SELECT new com.microsservicos.triagem.dto.ContagemPorItemDTO(t.nomeServicoSnapshot, COUNT(t)) " +
            "FROM Triagem t WHERE t.status = com.microsservicos.triagem.enums.StatusTriagem.FINALIZADO " +
            "GROUP BY t.nomeServicoSnapshot ORDER BY COUNT(t) DESC")
    List<ContagemPorItemDTO> contarAtendimentosPorServico();

    // Query para o gráfico 2: Atendimentos por dia (CORRIGIDA PARA SQL SERVER)
    @Query(value = "SELECT CAST(horario_solicitacao AS DATE) as data, COUNT(*) as quantidade " +
            "FROM triagens t WHERE t.status = 'FINALIZED' " + // Use o valor exato salvo no banco
            "GROUP BY CAST(horario_solicitacao AS DATE) " +
            "ORDER BY data ASC",
            nativeQuery = true)
    List<ContagemPorDataDTO> contarAtendimentosPorDia();
}