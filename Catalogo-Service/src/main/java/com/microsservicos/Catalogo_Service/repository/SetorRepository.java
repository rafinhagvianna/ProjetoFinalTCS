package com.microsservicos.Catalogo_Service.repository;

import com.microsservicos.Catalogo_Service.model.Setor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SetorRepository extends MongoRepository<Setor, String> {
}
