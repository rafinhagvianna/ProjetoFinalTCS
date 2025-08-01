package com.microsservicos.CadastroFuncionarioService.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microsservicos.CadastroFuncionarioService.controller.FuncionarioController.LoginRequest;
import com.microsservicos.CadastroFuncionarioService.dto.FuncionarioRequest;
import com.microsservicos.CadastroFuncionarioService.dto.FuncionarioResponse;
import com.microsservicos.CadastroFuncionarioService.model.Funcionario;
import com.microsservicos.CadastroFuncionarioService.repository.FuncionarioRepository;

@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;

    public FuncionarioService(FuncionarioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public FuncionarioResponse criarFuncionario(FuncionarioRequest req) {
        if (repository.existsByCpf(req.cpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        Funcionario entidade = new Funcionario(
                req.nome(),
                req.cpf(),
                req.senha(),
                req.email()
        );
        Funcionario salvo = repository.save(entidade);
        return toResponse(salvo);
    }

    public List<FuncionarioResponse> listarFuncionarios() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<Funcionario> buscarPorNome(String nome) {
        return repository.findByNome(nome);
    }

    public Optional<Funcionario> buscarPorEmail (String email){
        return repository.findByEmail(email);
    }


    private FuncionarioResponse toResponse(Funcionario f) {
        return new FuncionarioResponse(
                f.getNome(),
                f.getCpf(),
                f.getSenha(),
                f.getEmail()
        );
    }

    public Funcionario verificarLogin(LoginRequest req) {
        Optional<Funcionario> optFuncionario = repository.findByEmail(req.email());

        // Se o email não for encontrado, o login falha.
        if (optFuncionario.isEmpty()) {
            return null;
        }

        Funcionario funcionario = optFuncionario.get();
        String senhaPura = req.senha(); // A senha que o usuário digitou
        String hashDoBanco = funcionario.getSenha(); // O hash que está salvo
        

        // O 'matches' compara a senha pura com o hash do banco de forma segura.
        // if (passwordEncoder.matches(senhaPura, hashDoBanco)){
        if (hashDoBanco.equals(senhaPura)){
            return funcionario;
        }else {
            return null;
        }
    }
}
