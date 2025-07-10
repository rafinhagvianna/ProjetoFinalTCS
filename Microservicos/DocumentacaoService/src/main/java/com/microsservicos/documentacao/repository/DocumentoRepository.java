package com.microsservicos.documentacao.repository;

import com.microsservicos.documentacao.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.Optional; // Importe Optional

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, UUID> {
    List<Documento> findByTriagemId(UUID triagemId);

    List<Documento> findByAgendamentoId(UUID agendamentoId); // NOVO MÉTODO

    Optional<Documento> findByTriagemIdAndDocumentoCatalogoId(UUID triagemId, UUID documentoCatalogoId);
    Optional<Documento> findByAgendamentoIdAndDocumentoCatalogoId(UUID agendamentoId, UUID documentoCatalogoId);

}