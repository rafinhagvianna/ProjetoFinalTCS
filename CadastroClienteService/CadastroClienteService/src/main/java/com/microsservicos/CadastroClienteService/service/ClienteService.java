// src/main/java/com/microsservicos/CadastroClienteService/service/ClienteService.java
package com.microsservicos.CadastroClienteService.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.microsservicos.CadastroClienteService.model.Cliente;
import com.microsservicos.CadastroClienteService.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public Cliente criarCliente(Cliente dto) {
        Cliente entidade = new Cliente(
                dto.getNome(),
                dto.getCpf(),
                dto.getTelefone(),
                dto.getEmail(),
                dto.getSenha(),
                dto.getAgencia(),
                dto.getConta()
        );
        return repository.save(entidade);
    }

    public List<Cliente> listarClientes() {
        return repository.findAll();
    }
}
