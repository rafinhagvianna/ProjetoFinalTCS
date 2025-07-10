package com.microsservicos.Catalogo_Service.repository;

import com.microsservicos.Catalogo_Service.model.DocumentoCatalogo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentoCatalogoRepository extends MongoRepository<DocumentoCatalogo, UUID> {
    Optional<DocumentoCatalogo> findByNome(String nome);
}