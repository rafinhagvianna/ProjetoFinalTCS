package com.microservicos.authservice.client;

import com.microservicos.authservice.dto.AuthValidationResponseDTO;
import com.microservicos.authservice.dto.LoginCredentialsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

// Você precisará de um DTO para enviar as credenciais para o Cliente/Funcionario Service

@FeignClient(name = "ClienteService")
public interface CadastroClienteServiceClient {

    @PostMapping("/clientes/auth/validate") // Exemplo de endpoint no CadastroClienteService
    AuthValidationResponseDTO validateClientCredentials(@RequestBody LoginCredentialsDTO credentials);
}