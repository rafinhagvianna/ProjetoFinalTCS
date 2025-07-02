// src/main/java/com/microsservicos/CadastroClienteService/repository/ClienteRepository.java
package com.microsservicos.CadastroClienteService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.microsservicos.CadastroClienteService.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
