package com.microsservicos.Catalogo_Service.repository;

import com.microsservicos.Catalogo_Service.model.Setor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SetorRepository extends MongoRepository<Setor, UUID> {


    // Spring Data gera a query: "SELECT * FROM setor WHERE is_ativo = ?"
    List<Setor> findByIsAtivo(boolean ativo);

    Optional<Setor> findByNome(String nome);
}
