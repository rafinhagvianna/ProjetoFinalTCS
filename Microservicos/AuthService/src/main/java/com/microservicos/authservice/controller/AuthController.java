package com.microservicos.authservice.controller; // Ajuste o pacote

import com.microservicos.authservice.dto.LoginRequestDTO;
import com.microservicos.authservice.dto.LoginResponseDTO;
import com.microservicos.authservice.exception.InvalidCredentialsException;
import com.microservicos.authservice.service.AuthService; // Supondo que você tenha um AuthService

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        // O tratamento de exceções (try-catch) é opcional aqui se você tiver um GlobalExceptionHandler
        // mas é útil para ilustrar o fluxo.
        try {
            LoginResponseDTO response = authService.authenticateAndGenerateToken(loginRequest);
            return ResponseEntity.ok(response);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Ou um DTO de erro mais detalhado
        } catch (Exception e) {
            // Logar o erro completo aqui
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}