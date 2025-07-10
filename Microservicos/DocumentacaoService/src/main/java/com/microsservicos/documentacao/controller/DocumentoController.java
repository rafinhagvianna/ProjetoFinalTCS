package com.microsservicos.documentacao.controller;

import com.microsservicos.documentacao.dto.DocumentoResponseDTO;
import com.microsservicos.documentacao.dto.ValidacaoDocumentoRequestDTO;
import com.microsservicos.documentacao.service.DocumentoService;
import jakarta.servlet.http.HttpServletRequest; // NOVO
import jakarta.validation.Valid;
import org.springframework.core.io.Resource; // NOVO
import org.springframework.http.HttpHeaders; // NOVO
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException; // NOVO
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documentos")
@CrossOrigin(origins = "http://localhost:4200")
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoResponseDTO> uploadDocumento(
            @RequestParam(value = "triagemId", required = false) UUID triagemId,
            @RequestParam(value = "agendamentoId", required = false) UUID agendamentoId,
            @RequestParam(value = "documentoCatalogoId", required = false) UUID documentoCatalogoId,
            @RequestParam("arquivo") MultipartFile arquivo) {

        DocumentoResponseDTO response = documentoService.uploadDocumento(triagemId, agendamentoId, documentoCatalogoId, arquivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/validar")
    public ResponseEntity<DocumentoResponseDTO> validarDocumento(
            @PathVariable UUID id,
            @Valid @RequestBody ValidacaoDocumentoRequestDTO validacaoDTO) {

        DocumentoResponseDTO response = documentoService.validarDocumento(id, validacaoDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentoResponseDTO> getDocumentoById(@PathVariable UUID id) {
        DocumentoResponseDTO response = documentoService.getDocumentoById(id);
        return ResponseEntity.ok(response);
    }

    // --- NOVO: Endpoint para download de documento ---
    @GetMapping("/{documentoId}/download")
    public ResponseEntity<Resource> downloadDocumento(@PathVariable UUID documentoId, HttpServletRequest request) {
        Resource resource = documentoService.loadFileAsResource(documentoId);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Se não puder determinar o tipo de conteúdo, use um tipo padrão
            System.out.println("Could not determine file type.");
        }

        // Fallback para tipo de conteúdo padrão se não for determinado
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/triagem/{triagemId}")
    public ResponseEntity<List<DocumentoResponseDTO>> getDocumentosByTriagemId(@PathVariable UUID triagemId) {
        List<DocumentoResponseDTO> response = documentoService.getDocumentosByTriagemId(triagemId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/agendamento/{agendamentoId}")
    public ResponseEntity<List<DocumentoResponseDTO>> getDocumentosByAgendamentoId(@PathVariable UUID agendamentoId) {
        List<DocumentoResponseDTO> response = documentoService.getDocumentosByAgendamentoId(agendamentoId);
        return ResponseEntity.ok(response);
    }
}