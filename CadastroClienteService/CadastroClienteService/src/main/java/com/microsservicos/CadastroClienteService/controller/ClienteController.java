// src/main/java/com/microsservicos/CadastroClienteService/controller/ClienteController.java
package com.microsservicos.CadastroClienteService.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.microsservicos.CadastroClienteService.model.Cliente;
import com.microsservicos.CadastroClienteService.service.ClienteService;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente criarCliente(@RequestBody Cliente cliente) {
        return service.criarCliente(cliente);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Cliente> listarClientes() {
        return service.listarClientes();
    }
}
