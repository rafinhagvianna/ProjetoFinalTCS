package com.microsservicos.CadastroFuncionarioService.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.microsservicos.CadastroFuncionarioService.dto.FuncionarioRequest;
import com.microsservicos.CadastroFuncionarioService.dto.FuncionarioResponse;
import com.microsservicos.CadastroFuncionarioService.dto.LoginResponse;
import com.microsservicos.CadastroFuncionarioService.model.Funcionario;
import com.microsservicos.CadastroFuncionarioService.service.FuncionarioService;

@RestController
@RequestMapping("/api/funcionario")
public class FuncionarioController {

    private final FuncionarioService service;

    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FuncionarioResponse criarFuncionario(@RequestBody FuncionarioRequest req) {
        return service.criarFuncionario(req);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FuncionarioResponse> listarFuncionarios() {
        return service.listarFuncionarios();
    }

//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody LoginRequest req) {
//        Optional<Funcionario> opt = service.buscarPorNome(req.nome());
//
//        if (opt.isEmpty()) {
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body("Nome incorreto");
//        }
//
//        Funcionario f = opt.get();
//        if (!f.getSenha().equals(req.senha())) {
//            return ResponseEntity
//                    .status(HttpStatus.UNAUTHORIZED)
//                    .body("Senha incorreta");
//        }
//
//        return ResponseEntity
//                .ok("Login bem-sucedido");
//    }

    // @PostMapping("/login")
    // public ResponseEntity<String> login(@RequestBody LoginRequest req) {
    //     // ALTERE AQUI para usar a busca por e-mail
    //     Optional<Funcionario> opt = service.buscarPorEmail(req.email());

    //     if (opt.isEmpty()) {
    //         return ResponseEntity
    //                 .status(HttpStatus.UNAUTHORIZED)
    //                 .body("E-mail ou senha incorretos."); // Mensagem genérica é mais segura
    //     }

    //     Funcionario f = opt.get();
    //     // ATENÇÃO: Em produção, use um comparador de senhas criptografadas (BCrypt)
    //     if (!f.getSenha().equals(req.senha())) {
    //         return ResponseEntity
    //                 .status(HttpStatus.UNAUTHORIZED)
    //                 .body("E-mail ou senha incorretos.");
    //     }

    //     return ResponseEntity.ok("Login de funcionário bem-sucedido");
    // }

        @PostMapping("/login")
// Mude a assinatura para retornar um objeto LoginResponse
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        Funcionario loginValido = service.verificarLogin(req);

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

//    public static record LoginRequest(String nome, String senha) {}

    public static record LoginRequest(String email, String senha) {}
}
