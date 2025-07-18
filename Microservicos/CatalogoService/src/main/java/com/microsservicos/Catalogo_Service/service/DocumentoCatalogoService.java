package com.microsservicos.Catalogo_Service.service;

import com.microsservicos.Catalogo_Service.dto.DocumentoCatalogoRequest;
import com.microsservicos.Catalogo_Service.dto.DocumentoCatalogoResponse;
import com.microsservicos.Catalogo_Service.exceptions.RecursoDuplicadoException;
import com.microsservicos.Catalogo_Service.exceptions.RecursoNaoEncontradoException; // Crie esta exceção se ainda não existir
import com.microsservicos.Catalogo_Service.exceptions.ValidacaoException;
import com.microsservicos.Catalogo_Service.model.DocumentoCatalogo;
import com.microsservicos.Catalogo_Service.repository.DocumentoCatalogoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

public class DocumentoCatalogoService {

    private static final Logger log = LoggerFactory.getLogger(DocumentoCatalogoService.class);

    private final DocumentoCatalogoRepository documentoCatalogoRepository;

    public DocumentoCatalogoService(DocumentoCatalogoRepository documentoCatalogoRepository){
        this.documentoCatalogoRepository = documentoCatalogoRepository;
    }


    // Método auxiliar para mapear DocumentoCatalogo para DocumentoCatalogoResponse
    private DocumentoCatalogoResponse mapToDocumentoCatalogoResponse(DocumentoCatalogo documento) {
        return new DocumentoCatalogoResponse(
            documento.getId(),
            documento.getNome(),
            documento.getDescricao(),
            documento.isObrigatorioPorPadrao(),
            documento.isAtivo()
        );
    }

    /**
     * Cria um novo documento no catálogo.
     * @param request Dados do documento a ser criado.
     * @return O documento criado como DTO de resposta.
     * @throws ValidacaoException Se o nome for nulo ou vazio.
     * @throws RecursoDuplicadoException Se já existir um documento com o mesmo nome.
     */
    public DocumentoCatalogoResponse criarDocumento(DocumentoCatalogoRequest request) {
        if (request.nome() == null || request.nome().isBlank()) {
            throw new ValidacaoException("O nome do documento não pode ser nulo ou vazio.");
        }
        if (documentoCatalogoRepository.findByNome(request.nome()).isPresent()) {
            throw new RecursoDuplicadoException("Já existe um documento com o nome: " + request.nome());
        }

        DocumentoCatalogo documento = new DocumentoCatalogo();
        // O ID é gerado no construtor default do DocumentoCatalogo
        documento.setNome(request.nome());
        documento.setDescricao(request.descricao());
        documento.setObrigatorioPorPadrao(request.isObrigatorioPorPadrao());
        documento.setAtivo(request.isAtivo());

        documentoCatalogoRepository.save(documento);
        log.info("Documento de catálogo '{}' (ID: {}) criado com sucesso.", documento.getNome(), documento.getId());
        return mapToDocumentoCatalogoResponse(documento);
    }

    /**
     * Lista todos os documentos no catálogo.
     * @return Lista de documentos como DTOs de resposta.
     */
    public List<DocumentoCatalogoResponse> listarDocumentos() {
        return documentoCatalogoRepository.findAll().stream()
                .map(this::mapToDocumentoCatalogoResponse)
                .collect(Collectors.toList());
    }

    /**
     * Busca um documento por ID.
     * @param id ID do documento.
     * @return O documento encontrado como DTO de resposta.
     * @throws RecursoNaoEncontradoException Se o documento não for encontrado.
     */
    public DocumentoCatalogoResponse buscarDocumentoPorId(UUID id) {
        DocumentoCatalogo documento = documentoCatalogoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Documento de catálogo com ID '" + id + "' não encontrado."));
        return mapToDocumentoCatalogoResponse(documento);
    }

    /**
     * Atualiza um documento existente.
     * @param id ID do documento a ser atualizado.
     * @param request Dados para atualização do documento.
     * @return O documento atualizado como DTO de resposta.
     * @throws RecursoNaoEncontradoException Se o documento não for encontrado.
     * @throws RecursoDuplicadoException Se o novo nome já existir em outro documento.
     */
    public DocumentoCatalogoResponse atualizarDocumento(UUID id, DocumentoCatalogoRequest request) {
        DocumentoCatalogo documentoExistente = documentoCatalogoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Documento de catálogo com ID '" + id + "' não encontrado para atualização."));

        // Verifica duplicidade de nome, exceto se for o próprio documento
        if (!documentoExistente.getNome().equals(request.nome()) && documentoCatalogoRepository.findByNome(request.nome()).isPresent()) {
            throw new RecursoDuplicadoException("Já existe outro documento com o nome: " + request.nome());
        }

        documentoExistente.setNome(request.nome());
        documentoExistente.setDescricao(request.descricao());
        documentoExistente.setObrigatorioPorPadrao(request.isObrigatorioPorPadrao());
        documentoExistente.setAtivo(request.isAtivo());

        DocumentoCatalogo documentoAtualizado = documentoCatalogoRepository.save(documentoExistente);
        log.info("Documento de catálogo '{}' (ID: {}) atualizado com sucesso.", documentoAtualizado.getNome(), documentoAtualizado.getId());
        return mapToDocumentoCatalogoResponse(documentoAtualizado);
    }

    /**
     * Remove um documento do catálogo.
     * @param id ID do documento a ser removido.
     * @throws RecursoNaoEncontradoException Se o documento não for encontrado.
     */
    public void removerDocumento(UUID id) {
        if (!documentoCatalogoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Documento de catálogo com ID '" + id + "' não encontrado para remoção.");
        }
        documentoCatalogoRepository.deleteById(id);
        log.info("Documento de catálogo com ID {} removido com sucesso.", id);
    }
}