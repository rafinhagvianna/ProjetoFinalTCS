// package com.example.Agendamento_Service.service.facade; // Sugestão de pacote para Facades
package com.example.Agendamento_Service.service; // Ou use 'facade' diretamente no pacote service

import com.example.Agendamento_Service.client.CatalogoServiceClient;
import com.example.Agendamento_Service.client.ServicoResponse;
import com.example.Agendamento_Service.client.TipoDocumentoResponse;
import com.example.Agendamento_Service.client.DocumentoCatalogoResponse; // Importar o DTO de Documento
import com.example.Agendamento_Service.exception.ComunicacaoServicoException; // Importar exceção personalizada
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections; // Para retornar listas vazias de forma segura
import java.util.List;
import java.util.UUID;

@Service
public class CatalogoServiceFacade {

    private static final Logger log = LoggerFactory.getLogger(CatalogoServiceFacade.class);

    private final CatalogoServiceClient catalogoServiceClient;

    public CatalogoServiceFacade(CatalogoServiceClient catalogoServiceClient) {
        this.catalogoServiceClient = catalogoServiceClient;
    }

    /**
     * Busca os detalhes de um Setor (serviço) pelo ID, com Circuit Breaker.
     * @param servicoId O ID do setor/serviço.
     * @return SetorResponse com os detalhes do setor.
     * @throws ComunicacaoServicoException se o serviço de catálogo estiver indisponível e o fallback não for adequado.
     */
    @CircuitBreaker(name = "catalogoServiceCircuitBreaker", fallbackMethod = "fallbackGetSetorById")
    public ServicoResponse buscarSetorPorId(UUID servicoId) {
        log.debug("Chamando Catalogo-Service para buscar setor por ID: {}", servicoId);
        return catalogoServiceClient.getServicoById(servicoId);
    }

    /**
     * Fallback para a busca de Setor por ID.
     * @param servicoId O ID do setor.
     * @param t A exceção que disparou o fallback.
     * @return Um SetorResponse de fallback com dados padrão ou vazios.
     */
    public ServicoResponse fallbackGetSetorById(UUID servicoId, Throwable t) {
        log.error("FALLBACK ATIVADO: Erro ao buscar setor por ID {} no Catalogo-Service. Erro: {}", servicoId, t.getMessage());
        // Retorna um objeto de resposta padrão/vazio para evitar quebrar a triagem completamente
        return new ServicoResponse(
            servicoId,
            "Serviço Indisponível",
            "Serviço de catálogo temporariamente indisponível.",
            false, // isAtivo
            99,    // prioridade baixa
            0,     // tempo médio 0
            new ArrayList<>() // Nenhuma lista de documentos obrigatórios disponível
        );
    }

    /**
     * Busca os detalhes de um Documento de Catálogo pelo ID, com Circuit Breaker.
     * @param documentoId O ID do documento de catálogo.
     * @return DocumentoCatalogoResponse com os detalhes do documento.
     * @throws ComunicacaoServicoException se o serviço de catálogo estiver indisponível e o fallback não for adequado.
     */
    @CircuitBreaker(name = "catalogoDocumentoCircuitBreaker", fallbackMethod = "fallbackGetDocumentoCatalogoById")
    public DocumentoCatalogoResponse buscarDocumentoCatalogoPorId(UUID documentoId) {
        log.debug("Chamando Catalogo-Service para buscar documento por ID: {}", documentoId);
        return catalogoServiceClient.getDocumentoCatalogoById(documentoId);
    }

    /**
     * Fallback para a busca de Documento de Catálogo por ID.
     * @param documentoId O ID do documento.
     * @param t A exceção que disparou o fallback.
     * @return Um DocumentoCatalogoResponse de fallback com dados padrão ou vazios.
     */
    public DocumentoCatalogoResponse fallbackGetDocumentoCatalogoById(UUID documentoId, Throwable t) {
        log.error("FALLBACK ATIVADO: Erro ao buscar documento de catálogo por ID {} no Catalogo-Service. Erro: {}", documentoId, t.getMessage());
        // Retorna um objeto de resposta padrão/vazio para este documento
        return new DocumentoCatalogoResponse(
            documentoId,
            "Documento Indisponível",
            "Detalhes do documento temporariamente indisponíveis.",
            false, // isObrigatorioPorPadrao
            false  // isAtivo
        );
    }

    /**
     * Tenta buscar os nomes dos documentos obrigatórios de um setor.
     * Este método pode agregar chamadas para encapsular a lógica de busca de múltiplos documentos.
     * @param documentosIds Lista de UUIDs dos documentos obrigatórios.
     * @return Uma lista de DocumentoCatalogoResponse para os IDs fornecidos. Retorna vazia em caso de erro.
     */
    public List<DocumentoCatalogoResponse> buscarDetalhesDocumentos(List<UUID> documentosIds) {
        if (documentosIds == null || documentosIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<DocumentoCatalogoResponse> detalhesDocumentos = new ArrayList<>();
        for (UUID documentoId : documentosIds) {
            try {
                // Chama o método com Circuit Breaker para cada documento
                detalhesDocumentos.add(buscarDocumentoCatalogoPorId(documentoId));
            } catch (Exception e) {
                log.warn("Não foi possível obter detalhes para o documento ID {}. Ignorando este documento. Erro: {}", documentoId, e.getMessage());
                // Continua para o próximo documento, em vez de falhar toda a operação
            }
        }
        return detalhesDocumentos;
    }

    public List<DocumentoCatalogoResponse> buscarDetalhesDocumentosObrigatorios(List<UUID> documentosIds) {
        if (documentosIds == null || documentosIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<DocumentoCatalogoResponse> detalhesDocumentos = new ArrayList<>();
        for (UUID documentoId : documentosIds) {
            try {
                DocumentoCatalogoResponse doc = buscarDocumentoCatalogoPorId(documentoId);
                // Adiciona apenas se não for um documento de fallback "vazio"
                if (!"Documento Indisponível".equals(doc.nome())) {
                    detalhesDocumentos.add(doc);
                }
            } catch (Exception e) {
                log.warn("Falha ao obter detalhes para o documento ID {}. Ignorando este documento. Erro: {}", documentoId, e.getMessage());
            }
        }
        return detalhesDocumentos;
    }
}