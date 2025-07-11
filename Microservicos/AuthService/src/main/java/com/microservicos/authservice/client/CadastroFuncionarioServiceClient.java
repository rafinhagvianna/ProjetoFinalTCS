package com.microservicos.authservice.client;

import com.microservicos.authservice.dto.AuthValidationResponseDTO;
import com.microservicos.authservice.dto.LoginCredentialsDTO;
import com.microservicos.authservice.dto.LoginRequestDTO;
import com.microservicos.authservice.dto.UserValidationResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

// Reutiliza LoginCredentialsDTO
// public record LoginCredentialsDTO(String usernameOrEmail, String password) {}

@FeignClient(name = "FuncionarioService")
public interface CadastroFuncionarioServiceClient {

    // Este @PostMapping e o caminho devem corresponder ao endpoint de login no seu CadastroFuncionarioService
    @PostMapping("/api/funcionario/login") // Exemplo de endpoint. Ajuste conforme o seu FuncionarioService
    ResponseEntity<UserValidationResponseDTO> validarLoginFuncionario(@RequestBody LoginRequestDTO request);
}