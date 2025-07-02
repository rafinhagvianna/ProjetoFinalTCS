// src/main/java/com/microsservicos/CadastroClienteService/controller/ClienteController.java
package com.microsservicos.CadastroClienteService.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.microsservicos.CadastroClienteService.dto.ClienteRequest;
import com.microsservicos.CadastroClienteService.dto.ClienteResponse;
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
    public ClienteResponse criarCliente(@RequestBody ClienteRequest req) {
        return service.criarCliente(req);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClienteResponse> listarClientes() {
        return service.listarClientes();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest req) {
        Optional<Cliente> opt = service.buscarPorEmail(req.email());

        if (opt.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Email incorreto");
        }

        Cliente cliente = opt.get();
        if (!cliente.getSenha().equals(req.senha())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Senha incorreta");
        }

        return ResponseEntity
                .ok("Login bem-sucedido");
    }

    public static record LoginRequest(String email, String senha) {}
}
