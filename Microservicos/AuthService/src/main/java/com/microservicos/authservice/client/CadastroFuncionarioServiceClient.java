package com.microservicos.authservice.client;

import com.microservicos.authservice.dto.AuthValidationResponseDTO;
import com.microservicos.authservice.dto.LoginCredentialsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

// Reutiliza LoginCredentialsDTO
// public record LoginCredentialsDTO(String usernameOrEmail, String password) {}

@FeignClient(name = "FuncionarioService")
public interface CadastroFuncionarioServiceClient {

    @PostMapping("/funcionarios/auth/validate") // Exemplo de endpoint no CadastroFuncionarioService
    AuthValidationResponseDTO validateEmployeeCredentials(@RequestBody LoginCredentialsDTO credentials);
}