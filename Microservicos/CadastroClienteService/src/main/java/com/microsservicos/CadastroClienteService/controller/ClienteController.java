package com.microsservicos.CadastroClienteService.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.microsservicos.CadastroClienteService.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.microsservicos.CadastroClienteService.model.Cliente;
import com.microsservicos.CadastroClienteService.service.ClienteService;


@RestController
@RequestMapping("/api/cliente")
@CrossOrigin(origins = "http://localhost:4200")
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

    @PostMapping("/esqueci-senha")
    public ResponseEntity<String> esqueciSenha(@RequestBody EsqueciSenhaRequest req) {
        service.solicitarRedefinicaoSenha(req.email());
        // Sempre retorne uma mensagem genérica para não revelar se um e-mail está cadastrado ou não
        return ResponseEntity.ok("Se um e-mail correspondente for encontrado, um link de redefinição será enviado.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest req) {
        // 1. A lógica de verificação foi movida para o service.
        boolean loginValido = service.verificarLogin(req);

        if (loginValido) {
            // 2. Se for válido, retorna sucesso.
            return ResponseEntity.ok("Login bem-sucedido");
        } else {
            // 3. Se for inválido, retorna falha.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail ou senha incorretos.");
        }

    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<String> redefinirSenha(@RequestBody RedefinirSenhaRequest req) {
        boolean sucesso = service.redefinirSenha(req.token(), req.novaSenha());

        if (sucesso) {
            return ResponseEntity.ok("Senha redefinida com sucesso!");
        } else {
            return ResponseEntity.badRequest().body("Token inválido ou expirado.");
        }
    }

    @GetMapping("{id}/nome")
    public String getMethodName(@PathVariable UUID id) {
        Optional<Cliente> cliente = service.buscarPorId(id);

        if (cliente.isEmpty()) {
            return "";
        }

        return cliente.get().getNome();
    }

//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody LoginRequest req) {
//        Optional<Cliente> opt = service.buscarPorEmail(req.email());
//
//        if (opt.isEmpty()) {
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body("Email incorreto");
//        }
//
//        Cliente cliente = opt.get();
//        if (cliente.getSenha() == null || !cliente.getSenha().equals(req.senha())) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha incorreta");
//        }
//
//        return ResponseEntity
//                .ok("Login bem-sucedido");
//    }

//    public static record LoginRequest(String email, String senha) {}
}
