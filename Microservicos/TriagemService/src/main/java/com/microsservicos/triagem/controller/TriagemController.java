package com.microsservicos.triagem.controller;

import com.microsservicos.triagem.dto.AtualizarStatusTriagemDTO;
import com.microsservicos.triagem.dto.DocumentoStatusUpdateRequestDTO;
import com.microsservicos.triagem.dto.TriagemRequestDTO;
import com.microsservicos.triagem.dto.TriagemResponseDTO;
import com.microsservicos.triagem.exception.AuthServiceException;
import com.microsservicos.triagem.exception.InvalidTokenException;
import com.microsservicos.triagem.model.Triagem;
import com.microsservicos.triagem.service.TriagemService;
import jakarta.validation.Valid; // Importante: Adicionar import para @Valid

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


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
    public ResponseEntity<?> criarTriagem(@Valid @RequestBody TriagemRequestDTO dto,
                @RequestHeader("Authorization") String authorizationHeader) {

        UUID idCliente;
        try {
            idCliente = triagemService.validateTokenAndGetUserId(authorizationHeader);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (AuthServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado ao buscar agendamentos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro interno ao processar a requisição.");
        }
        return ResponseEntity
                .status(201) // HTTP 201 Created para recursos recém-criados
                .body(triagemService.criarTriagem(dto, idCliente));
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

    @GetMapping("/cliente/{id}")
    public ResponseEntity<TriagemResponseDTO> buscarPorCliente(@PathVariable UUID id) {
        TriagemResponseDTO triagem = triagemService.buscarPorCliente(id);
        return ResponseEntity.ok(triagem);
    }
    @GetMapping("/{id}")
    public ResponseEntity<TriagemResponseDTO> buscarPorId(@PathVariable UUID id) {
        TriagemResponseDTO triagem = triagemService.buscarPorId(id);
        return ResponseEntity.ok(triagem);
    }

    @GetMapping("/disponibilidade")
    public Map<String, LocalDateTime> horarioDisponivel() {
        Triagem triagem = new Triagem();
        Map<String, LocalDateTime> horarioDisponivel = new HashMap<>();
        horarioDisponivel.put("disponibilidade", triagemService.calcularHorarioInicioEstimado(triagem));
        return horarioDisponivel;
    }

    @GetMapping
    public ResponseEntity<List<TriagemResponseDTO>> listarTodas() {
        List<TriagemResponseDTO> triagens = triagemService.listarTodasTriagens();
        return ResponseEntity.ok(triagens);
    }
    
}