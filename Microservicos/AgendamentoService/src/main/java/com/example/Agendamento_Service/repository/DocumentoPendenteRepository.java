package com.example.Agendamento_Service.repository;

import com.example.Agendamento_Service.model.DocumentoPendente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentoPendenteRepository extends JpaRepository<DocumentoPendente, UUID> {

    Optional<DocumentoPendente> findByAgendamento_IdAndDocumentoCatalogoId(UUID triagemId, UUID documentoCatalogoId);

    List<DocumentoPendente> findByAgendamento_Id(UUID agendamentoId);
}
