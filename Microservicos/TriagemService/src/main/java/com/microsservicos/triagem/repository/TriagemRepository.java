package com.microsservicos.triagem.repository;

import com.microsservicos.triagem.enums.StatusDocumento;
import com.microsservicos.triagem.enums.StatusTriagem;
import com.microsservicos.triagem.model.Triagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TriagemRepository extends JpaRepository<Triagem, UUID> {

    List<Triagem> findByStatus(StatusTriagem status);
    
    List<Triagem> findByStatusOrStatusOrderByPrioridadeAscHorarioSolicitacaoAsc(StatusTriagem status1, StatusTriagem status2);

    Optional<Triagem> findFirstByStatusOrderByPrioridadeAscHorarioSolicitacaoAsc(StatusTriagem status);

    Optional<Triagem> findTopByOrderByHorarioEstimadoAtendimentoDesc();

    Triagem findByClienteIdAndStatus(UUID clienteId, StatusTriagem statusTriagem);
}