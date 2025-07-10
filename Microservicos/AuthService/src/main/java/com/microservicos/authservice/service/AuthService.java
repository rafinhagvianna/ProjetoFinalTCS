package com.microservicos.authservice.service;

import com.microservicos.authservice.dto.AuthValidationResponseDTO;
import com.microservicos.authservice.client.CadastroClienteServiceClient;
import com.microservicos.authservice.client.CadastroFuncionarioServiceClient;
import com.microservicos.authservice.dto.LoginCredentialsDTO; // Novo DTO para enviar credenciais aos serviços de cadastro
import com.microservicos.authservice.dto.LoginRequestDTO;
import com.microservicos.authservice.dto.LoginResponseDTO;
import com.microservicos.authservice.exception.InvalidCredentialsException; // Exceção personalizada

import feign.FeignException; // Para tratar erros de comunicação Feign

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtService jwtService;
    private final CadastroClienteServiceClient cadastroClienteServiceClient;
    private final CadastroFuncionarioServiceClient cadastroFuncionarioServiceClient;

    public AuthService(JwtService jwtService,
                       CadastroClienteServiceClient cadastroClienteServiceClient,
                       CadastroFuncionarioServiceClient cadastroFuncionarioServiceClient) {
        this.jwtService = jwtService;
        this.cadastroClienteServiceClient = cadastroClienteServiceClient;
        this.cadastroFuncionarioServiceClient = cadastroFuncionarioServiceClient;
    }

    public LoginResponseDTO authenticateAndGenerateToken(LoginRequestDTO loginRequest) {
        AuthValidationResponseDTO authDetails = null;

        // Tenta autenticar como Cliente primeiro
        try {
            authDetails = cadastroClienteServiceClient.validateClientCredentials(
                    new LoginCredentialsDTO(loginRequest.usernameOrEmail(), loginRequest.password())
            );
        } catch (FeignException.NotFound e) { // Usuário não encontrado no cliente
            // Não faz nada, continua para tentar como funcionário
        } catch (FeignException.BadRequest e) { // Credenciais inválidas para cliente
            throw new InvalidCredentialsException("Credenciais inválidas para cliente.");
        } catch (Exception e) {
            // Logar o erro: erro de comunicação inesperado
            throw new RuntimeException("Erro de comunicação com o serviço de cadastro de clientes.", e);
        }

        // Se não autenticou como cliente, tenta como Funcionário
        if (authDetails == null) {
            try {
                authDetails = cadastroFuncionarioServiceClient.validateEmployeeCredentials(
                        new LoginCredentialsDTO(loginRequest.usernameOrEmail(), loginRequest.password())
                );
            } catch (FeignException.NotFound e) { // Usuário não encontrado no funcionário
                // Não faz nada, a exceção final será lançada
            } catch (FeignException.BadRequest e) { // Credenciais inválidas para funcionário
                throw new InvalidCredentialsException("Credenciais inválidas para funcionário.");
            } catch (Exception e) {
                // Logar o erro: erro de comunicação inesperado
                throw new RuntimeException("Erro de comunicação com o serviço de cadastro de funcionários.", e);
            }
        }

        // Se após ambas as tentativas, não houver detalhes de autenticação, as credenciais são inválidas
        if (authDetails == null) {
            throw new InvalidCredentialsException("Nome de usuário/email ou senha inválidos.");
        }

        // Se chegou aqui, a autenticação foi bem-sucedida em um dos serviços de cadastro
        // Gera o JWT usando os detalhes obtidos
        String accessToken = jwtService.generateToken(authDetails.userId(), authDetails.role(), authDetails.username(), authDetails.email(), authDetails.fullName());
        String refreshToken = jwtService.generateRefreshToken(authDetails.userId(), authDetails.role()); // Opcional, se estiver usando refresh tokens

        return new LoginResponseDTO(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getExpirationTime(), // Método em JwtService para obter tempo de expiração
                authDetails.userId().toString(),
                authDetails.role()
        );
    }
}