// src/main/java/com/microsservicos/CadastroClienteService/service/ClienteService.java
package com.microsservicos.CadastroClienteService.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.microsservicos.CadastroClienteService.dto.ClienteRequest;
import com.microsservicos.CadastroClienteService.dto.ClienteResponse;
import com.microsservicos.CadastroClienteService.model.Cliente;
import com.microsservicos.CadastroClienteService.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public ClienteResponse criarCliente(ClienteRequest req) {
        Cliente entidade = new Cliente(
                req.nome(),
                req.cpf(),
                req.telefone(),
                req.email(),
                req.senha(),
                req.agencia(),
                req.conta()
        );
        Cliente salvo = repository.save(entidade);
        return toResponse(salvo);
    }

    public List<ClienteResponse> listarClientes() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return repository.findByEmail(email);
    }

    private ClienteResponse toResponse(Cliente c) {
        return new ClienteResponse(
                c.getNome(),
                c.getCpf(),
                c.getTelefone(),
                c.getEmail(),
                c.getSenha(),
                c.getAgencia(),
                c.getConta()
        );
    }
}
