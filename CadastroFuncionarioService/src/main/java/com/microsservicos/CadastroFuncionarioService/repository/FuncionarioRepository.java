package com.microsservicos.CadastroFuncionarioService.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microsservicos.CadastroFuncionarioService.model.Funcionario;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, UUID> {
    Optional<Funcionario> findByNome(String nome);
    boolean existsByCpf(String cpf);
}
