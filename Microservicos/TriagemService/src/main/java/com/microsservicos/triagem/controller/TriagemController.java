package com.microsservicos.triagem.controller;

import com.microsservicos.triagem.dto.AtualizarStatusTriagemDTO;
import com.microsservicos.triagem.dto.DocumentoStatusUpdateRequestDTO;
import com.microsservicos.triagem.dto.TriagemRequestDTO;
import com.microsservicos.triagem.dto.TriagemResponseDTO;
import com.microsservicos.triagem.service.TriagemService;
import jakarta.validation.Valid; // Importante: Adicionar import para @Valid
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST para gerenciar operações relacionadas a triagens.
 */
@RestController
@RequestMapping("/api/triagens")
public class TriagemController {

    private final TriagemService triagemService;

    /**
     * Construtor para injeção de dependência do TriagemService.
     * @param triagemService O serviço de triagem.
     */
    public TriagemController(TriagemService triagemService) {
        this.triagemService = triagemService;
    }

    /**
     * Cria uma nova triagem no sistema.
     * Requer que o DTO de requisição seja válido.
     *
     * @param dto Os dados da triagem a serem criados.
     * @return ResponseEntity com o DTO da triagem criada e status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<TriagemResponseDTO> criarTriagem(@Valid @RequestBody TriagemRequestDTO dto) {
        // O @Valid garante que o DTO será validado antes de chegar aqui.
        // Se a validação falhar, uma MethodArgumentNotValidException será lançada,
        // que pode ser capturada por um @ControllerAdvice global.
        return ResponseEntity
                .status(201) // HTTP 201 Created para recursos recém-criados
                .body(triagemService.criarTriagem(dto));
    }

    /**
     * Busca a próxima triagem disponível na fila (com status AGUARDANDO).
     *
     * @return ResponseEntity com o DTO da próxima triagem e status 200 (OK).
     * Retorna 404 (Not Found) se nenhuma triagem aguardando for encontrada (tratado por ControllerAdvice).
     */
    @GetMapping("/proxima")
    public ResponseEntity<TriagemResponseDTO> buscarProximaTriagem() {
        return ResponseEntity
                .ok(triagemService.buscarProximaTriagem());
    }

    /**
     * Atualiza o status de uma triagem específica.
     * Requer que o DTO de atualização seja válido.
     *
     * @param id O ID da triagem a ser atualizada.
     * @param dto O DTO contendo o novo status.
     * @return ResponseEntity com o DTO da triagem atualizada e status 200 (OK).
     * Retorna 404 (Not Found) se a triagem não for encontrada (tratado por ControllerAdvice).
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TriagemResponseDTO> atualizarStatusTriagem(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarStatusTriagemDTO dto // @Valid para validar o novo status
    ) {
        TriagemResponseDTO response = triagemService.atualizarStatus(id, dto.novoStatus());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{triagemId}/documentos/{documentoCatalogoId}/status")
    public ResponseEntity<Void> atualizarStatusDocumentoTriagem(
            @PathVariable UUID triagemId,
            @PathVariable UUID documentoCatalogoId,
            @RequestBody DocumentoStatusUpdateRequestDTO requestDTO) {

        triagemService.atualizarStatusDocumentoTriagem(triagemId, documentoCatalogoId, requestDTO);

        return ResponseEntity.ok().build();
    }
}