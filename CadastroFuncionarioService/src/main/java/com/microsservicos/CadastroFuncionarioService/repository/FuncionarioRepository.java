package com.microsservicos.CadastroFuncionarioService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.microsservicos.CadastroFuncionarioService.model.Funcionario;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    Optional<Funcionario> findByNome(String nome);
    boolean existsByCpf(String cpf);
}
