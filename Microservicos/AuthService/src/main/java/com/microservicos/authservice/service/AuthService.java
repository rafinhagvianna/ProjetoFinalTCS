package com.microservicos.authservice.service;

import com.microservicos.authservice.dto.*;
import com.microservicos.authservice.client.CadastroClienteServiceClient;
import com.microservicos.authservice.client.CadastroFuncionarioServiceClient;
import com.microservicos.authservice.exception.InvalidCredentialsException; // Exceção personalizada

import feign.FeignException; // Para tratar erros de comunicação Feign

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import feign.FeignException; // Importe FeignException
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.Console;
import java.util.UUID;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final CadastroClienteServiceClient clienteClient;
    private final CadastroFuncionarioServiceClient funcionarioClient;

    public AuthService(JwtService jwtService, CadastroClienteServiceClient clienteClient, CadastroFuncionarioServiceClient funcionarioClient) {
        this.jwtService = jwtService;
        this.clienteClient = clienteClient;
        this.funcionarioClient = funcionarioClient;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        UserValidationResponseDTO userData = null;
        String role = null;

        // 1. Tenta autenticar como Cliente
        try {
            ResponseEntity<UserValidationResponseDTO> clienteResponse = clienteClient.validarLoginCliente(request);

            if (clienteResponse.getStatusCode() == HttpStatus.OK && clienteResponse.getBody() != null) {
                userData = clienteResponse.getBody();
                role = "CLIENTE";
                // System.out.println(userData.getNome());
            }
        } catch (FeignException.Unauthorized e) {
            // Se o cliente service retornar 401, isso significa credenciais inválidas para cliente
            System.out.println("Login de cliente falhou (401): " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro na chamada ao cliente service: " + e.getMessage());
            // Outros erros da chamada ao Feign
        }

        // 2. Se não foi cliente, tenta autenticar como Funcionário
        if (userData == null) {
            try {
                ResponseEntity<UserValidationResponseDTO> funcionarioResponse = funcionarioClient.validarLoginFuncionario(request);

                if (funcionarioResponse.getStatusCode() == HttpStatus.OK && funcionarioResponse.getBody() != null) {
                    userData = funcionarioResponse.getBody();
                    role = "FUNCIONARIO";
                }
            } catch (FeignException.Unauthorized e) {
                // Se o funcionario service retornar 401, credenciais inválidas para funcionário
                System.out.println("Login de funcionário falhou (401): " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Erro na chamada ao funcionário service: " + e.getMessage());
            }
        }

        // 3. Se as credenciais forem válidas para um dos tipos de usuário, gera o JWT
        if (userData != null && role != null) {
            String accessToken = jwtService.generateToken(
                    userData.getId(),
                    role,
                    userData.getEmail(),
                    userData.getNome()
            );
            String refreshToken = jwtService.generateRefreshToken(userData.getId(), role); // Gerar refresh token

            
            return new LoginResponseDTO(
                    "Login bem-sucedido",
                    accessToken,
                    refreshToken,
                    userData.getNome(),
                    userData.getEmail(),
                    userData.getId(), // <-- Passe o ID
                    role
            );
        } else {
            // Se chegou aqui, nenhuma autenticação foi bem-sucedida
            throw new InvalidCredentialsException("E-mail ou senha incorretos.");
        }
    }
}