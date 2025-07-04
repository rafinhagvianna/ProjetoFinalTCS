package com.microsservicos.documentacao.service;

import com.microsservicos.documentacao.client.AgendamentoServiceClient; // NOVO
import com.microsservicos.documentacao.client.TriagemServiceClient; // NOVO
import com.microsservicos.documentacao.dto.DocumentoResponseDTO;
import com.microsservicos.documentacao.dto.DocumentoStatusUpdateRequestDTO; // NOVO
import com.microsservicos.documentacao.dto.ValidacaoDocumentoRequestDTO;
import com.microsservicos.documentacao.enums.StatusDocumento;
import com.microsservicos.documentacao.exception.RecursoNaoEncontradoException;
import com.microsservicos.documentacao.model.Documento;
import com.microsservicos.documentacao.repository.DocumentoRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder; // Para construir a URL

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final TriagemServiceClient triagemServiceClient; // NOVO
    private final AgendamentoServiceClient agendamentoServiceClient; // NOVO

    @Value("${document.storage.location}")
    private String storageLocation;

    private Path fileStorageLocation;

    // Injetar os Feign Clients
    public DocumentoService(DocumentoRepository documentoRepository,
                            TriagemServiceClient triagemServiceClient,
                            AgendamentoServiceClient agendamentoServiceClient) {
        this.documentoRepository = documentoRepository;
        this.triagemServiceClient = triagemServiceClient;
        this.agendamentoServiceClient = agendamentoServiceClient;
    }

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(storageLocation).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Transactional
    public DocumentoResponseDTO uploadDocumento(UUID triagemId, UUID agendamentoId, UUID documentoCatalogoId, MultipartFile arquivo) {
        if (triagemId == null && agendamentoId == null) {
            throw new IllegalArgumentException("O documento deve estar associado a um ID de Triagem ou a um ID de Agendamento.");
        }
        if (triagemId != null && agendamentoId != null) {
            throw new IllegalArgumentException("O documento não pode estar associado a um ID de Triagem E a um ID de Agendamento simultaneamente.");
        }
        if (arquivo.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode ser vazio.");
        }
        if (arquivo.getOriginalFilename() == null || arquivo.getOriginalFilename().isBlank()) {
            throw new IllegalArgumentException("Nome do arquivo original é inválido.");
        }

        String fileName = UUID.randomUUID().toString() + "_" + arquivo.getOriginalFilename();
        Path targetLocation = this.fileStorageLocation.resolve(fileName);

        try {
            Files.copy(arquivo.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("Falha ao armazenar o arquivo " + fileName, ex);
        }

        Documento documento = new Documento();
        documento.setTriagemId(triagemId);
        documento.setAgendamentoId(agendamentoId);
        documento.setDocumentoCatalogoId(documentoCatalogoId);
        documento.setNomeOriginalArquivo(arquivo.getOriginalFilename());
        documento.setCaminhoArmazenamento(targetLocation.toString());
        documento.setStatus(StatusDocumento.AGUARDANDO_VALIDACAO);
        documento.setDataUpload(LocalDateTime.now());

        // --- NOVO: Definir urlVisualizacao ---
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/documentos/")
                .path(documento.getId().toString()) // O ID do documento ainda não existe aqui, pois não foi salvo.
                // Vamos gerar uma URL temporária ou salvá-la após obter o ID.
                // Melhor: obter o ID após o save e então construir a URL.
                .path("/download")
                .toUriString();
        // A URL será construída após salvar o documento, para ter o ID real.

        Documento salvo = documentoRepository.save(documento);

        // --- ATUALIZAR urlVisualizacao COM O ID REAL DO DOCUMENTO ---
        // Se a base URL do serviço for alterada, esta URL também precisa ser ajustada
        String finalFileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/documentos/")
                .path(salvo.getId().toString())
                .path("/download")
                .toUriString();
        salvo.setUrlVisualizacao(finalFileDownloadUri);
        documentoRepository.save(salvo); // Salvar novamente para persistir a URL

        // --- NOVO: Comunicar ao Triagem/Agendamento Service ---
        DocumentoStatusUpdateRequestDTO updateDTO = new DocumentoStatusUpdateRequestDTO(
                salvo.getId(),
                salvo.getStatus(),
                salvo.getUrlVisualizacao(),
                null // Sem observação inicial
        );

        if (salvo.getTriagemId() != null) {
            triagemServiceClient.atualizarStatusDocumentoTriagem(salvo.getTriagemId(), salvo.getDocumentoCatalogoId(), updateDTO);
        } else if (salvo.getAgendamentoId() != null) {
            agendamentoServiceClient.atualizarStatusDocumentoAgendamento(salvo.getAgendamentoId(), salvo.getDocumentoCatalogoId(), updateDTO);
        }

        return toResponseDTO(salvo);
    }

    @Transactional
    public DocumentoResponseDTO validarDocumento(UUID documentoId, ValidacaoDocumentoRequestDTO validacaoDTO) {
        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Documento com ID " + documentoId + " não encontrado."));

        if (validacaoDTO.novoStatus() == StatusDocumento.REJEITADO && (validacaoDTO.observacao() == null || validacaoDTO.observacao().isBlank())) {
            throw new IllegalArgumentException("Observação é obrigatória para documentos rejeitados.");
        }

        documento.setStatus(validacaoDTO.novoStatus());
        documento.setObservacaoValidacao(validacaoDTO.observacao());
        documento.setDataValidacao(LocalDateTime.now());

        Documento atualizado = documentoRepository.save(documento);

        // --- NOVO: Comunicar ao Triagem/Agendamento Service após validação ---
        DocumentoStatusUpdateRequestDTO updateDTO = new DocumentoStatusUpdateRequestDTO(
                atualizado.getId(),
                atualizado.getStatus(),
                atualizado.getUrlVisualizacao(),
                atualizado.getObservacaoValidacao()
        );

        if (atualizado.getTriagemId() != null) {
            triagemServiceClient.atualizarStatusDocumentoTriagem(atualizado.getTriagemId(), atualizado.getDocumentoCatalogoId(), updateDTO);
        } else if (atualizado.getAgendamentoId() != null) {
            agendamentoServiceClient.atualizarStatusDocumentoAgendamento(atualizado.getAgendamentoId(), atualizado.getDocumentoCatalogoId(), updateDTO);
        }

        return toResponseDTO(atualizado);
    }

    // --- NOVO: Método para servir o arquivo ---
    public Resource loadFileAsResource(UUID documentoId) {
        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Documento não encontrado para download com ID: " + documentoId));

        try {
            Path filePath = Paths.get(documento.getCaminhoArmazenamento()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RecursoNaoEncontradoException("Arquivo não encontrado no caminho: " + documento.getCaminhoArmazenamento());
            }
        } catch (MalformedURLException ex) {
            throw new RecursoNaoEncontradoException("Erro ao carregar o arquivo: " + ex.getMessage());
        }
    }


    @Transactional(readOnly = true)
    public DocumentoResponseDTO getDocumentoById(UUID id) {
        return documentoRepository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Documento com ID " + id + " não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<DocumentoResponseDTO> getDocumentosByTriagemId(UUID triagemId) {
        return documentoRepository.findByTriagemId(triagemId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentoResponseDTO> getDocumentosByAgendamentoId(UUID agendamentoId) {
        return documentoRepository.findByAgendamentoId(agendamentoId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private DocumentoResponseDTO toResponseDTO(Documento documento) {
        return new DocumentoResponseDTO(
                documento.getId(),
                documento.getTriagemId(),
                documento.getAgendamentoId(),
                documento.getDocumentoCatalogoId(),
                documento.getNomeOriginalArquivo(),
                documento.getCaminhoArmazenamento(),
                documento.getStatus(),
                documento.getUrlVisualizacao(),
                documento.getObservacaoValidacao(),
                documento.getDataUpload(),
                documento.getDataValidacao()
        );
    }
}