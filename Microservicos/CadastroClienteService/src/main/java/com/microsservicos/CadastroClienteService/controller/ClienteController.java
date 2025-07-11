package com.microsservicos.CadastroClienteService.controller;

import java.util.List;
import java.util.Optional;

import com.microsservicos.CadastroClienteService.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
// Mude a assinatura para retornar um objeto LoginResponse
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        Cliente loginValido = service.verificarLogin(req);

        if (loginValido != null) {
            // Crie e retorne um novo objeto LoginResponse.
            // O Spring irá serializá-lo para JSON automaticamente.
            LoginResponse response = new LoginResponse("Login bem-sucedido", loginValido.getNome(), loginValido.getEmail(), loginValido.getId());
            return ResponseEntity.ok(response);
        } else {
            // Faça o mesmo para a resposta de erro.
            LoginResponse response = new LoginResponse("E-mail ou senha incorretos.", "","", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
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
