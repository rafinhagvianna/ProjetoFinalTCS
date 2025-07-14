package com.microsservicos.TarefasFuncionario_Service.repository;

import com.microsservicos.TarefasFuncionario_Service.model.Tarefa;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface TarefaRepository extends MongoRepository<Tarefa, UUID> {

    Optional<Tarefa> findByNome(String nome);
}
