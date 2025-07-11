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

// Você precisará de um DTO para enviar as credenciais para o Cliente/Funcionario Service

@FeignClient(name = "ClienteService")
public interface CadastroClienteServiceClient {

    // Este @PostMapping e o caminho devem corresponder ao endpoint de login no seu CadastroClienteService
    @PostMapping("/api/cliente/login") // Exemplo de endpoint. Ajuste conforme o seu ClienteService
    ResponseEntity<UserValidationResponseDTO> validarLoginCliente(@RequestBody LoginRequestDTO request);
}