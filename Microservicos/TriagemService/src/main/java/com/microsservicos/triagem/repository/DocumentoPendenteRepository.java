package com.microsservicos.triagem.repository;

import com.microsservicos.triagem.model.DocumentoPendente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentoPendenteRepository extends JpaRepository<DocumentoPendente, UUID> {
    // Buscar um documento pendente específico por ID da triagem e ID do tipo de documento
    // Note o uso de 'Triagem_Id' para acessar o ID da entidade relacionada
    Optional<DocumentoPendente> findByTriagem_IdAndDocumentoCatalogoId(UUID triagemId, UUID documentoCatalogoId);

    // Buscar todos os documentos pendentes para uma triagem
    List<DocumentoPendente> findByTriagem_Id(UUID triagemId);
}