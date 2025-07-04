package com.microsservicos.triagem.service;

import com.microsservicos.triagem.client.CatalogoServiceClient;
import com.microsservicos.triagem.client.ServicoResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CatalogoServiceFacade {
    private final CatalogoServiceClient catalogoServiceClient;

    public CatalogoServiceFacade(CatalogoServiceClient catalogoServiceClient) {
        this.catalogoServiceClient = catalogoServiceClient;
    }

    @CircuitBreaker(name = "catalogo", fallbackMethod = "fallbackGetServico")
    public ServicoResponse buscarServicoPorId(UUID servicoId) {
        return catalogoServiceClient.getServicoById(servicoId);
    }

    public ServicoResponse fallbackGetServico(UUID servicoId, Throwable t) {
        System.err.println("FALLBACK: Erro ao buscar serviço " + servicoId + ": " + t.getMessage());
        // Retorna um objeto de resposta padrão/vazio
        return new ServicoResponse(servicoId, "Serviço Indisponível", 0);
    }
}
