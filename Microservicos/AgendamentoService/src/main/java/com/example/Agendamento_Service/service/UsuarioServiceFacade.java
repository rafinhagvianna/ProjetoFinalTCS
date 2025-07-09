package com.example.Agendamento_Service.service;

import com.example.Agendamento_Service.client.CadastroClienteServiceClient; // Seu Feign Client para Cadastro
import com.example.Agendamento_Service.client.CatalogoServiceClient;
import com.example.Agendamento_Service.exception.ComunicacaoServicoException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Service
public class UsuarioServiceFacade {

    private static final Logger log = LoggerFactory.getLogger(CatalogoServiceFacade.class);
    
    private final CadastroClienteServiceClient cadastroClienteServiceClient; // Ou UsuarioServiceClient

    public UsuarioServiceFacade(CadastroClienteServiceClient cadastroClienteServiceClient) {
        this.cadastroClienteServiceClient = cadastroClienteServiceClient;
    }

    @CircuitBreaker(name = "cadastroClienteCircuitBreaker", fallbackMethod = "fallbackGetNomeCliente")
    public String buscarNomeCliente(UUID clienteId) {
        log.debug("Chamando Cadastro-Cliente-Service para buscar nome do cliente por ID: {}", clienteId);
        return cadastroClienteServiceClient.getNomeCliente(clienteId);
    }

    public String fallbackGetNomeCliente(UUID clienteId, Throwable t) {
        log.error("FALLBACK ATIVADO: Erro ao buscar nome do cliente por ID {} no Cadastro-Service. Erro: {}", clienteId, t.getMessage());
        return "Cliente Indisponível"; // Retorna um nome padrão em caso de falha
    }
}