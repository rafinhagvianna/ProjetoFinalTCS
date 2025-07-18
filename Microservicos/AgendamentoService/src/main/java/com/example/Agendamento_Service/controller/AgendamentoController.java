package com.example.Agendamento_Service.controller;

import com.example.Agendamento_Service.dto.AgendamentoRequestDTO;
import com.example.Agendamento_Service.dto.AgendamentoResponseDTO; // Importe o novo DTO
import com.example.Agendamento_Service.dto.DocumentoPendenteResponseDTO;
import com.example.Agendamento_Service.dto.DocumentoStatusUpdateRequestDTO;
import com.example.Agendamento_Service.exception.AuthServiceException;
import com.example.Agendamento_Service.exception.InvalidTokenException;
import com.example.Agendamento_Service.model.Agendamento;
import com.example.Agendamento_Service.service.AgendamentoService;
import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoService service;
     // Injete seu cliente Feign para autenticação


    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    // Método de conversão de Model para ResponseDTO
    private AgendamentoResponseDTO toResponseDTO(Agendamento agendamento) {
        List<DocumentoPendenteResponseDTO> documentosDTO = agendamento.getDocumentosPendentes().stream()
                .map(doc -> new DocumentoPendenteResponseDTO(
                        doc.getId(),
                        doc.getDocumentoCatalogoId(),
                        doc.getNomeDocumentoSnapshot(),
                        doc.getStatus(),
                        doc.getObservacao(),
                        doc.getUrlDocumento()
                ))
                .collect(Collectors.toList());

        return new AgendamentoResponseDTO(
                agendamento.getId(),
                agendamento.getUsuarioId(),
                agendamento.getNomeClienteSnapshot(),
                agendamento.getAtendenteId(),
                agendamento.getServicoId(),
                agendamento.getNomeServicoSnapshot(),
                agendamento.getDataHora(),
                agendamento.getAtendidoEm(),
                agendamento.getObservacoes(),
                agendamento.getCriadoEm(),
                agendamento.getStatus(),
                documentosDTO
        );
    }

    @GetMapping
    public ResponseEntity<List<AgendamentoResponseDTO>> listarTodos() {
        // log.info("Requisição para listar todos os agendamentos.");
        List<AgendamentoResponseDTO> agendamentos = service.listarTodos().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/data") // Endpoint para GET /api/agendamentos/data?data=YYYY-MM-DD
    public ResponseEntity<List<AgendamentoResponseDTO>> getAgendamentosPorData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        List<AgendamentoResponseDTO> agendamentos = service.getAgendamentosPorData(data).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(agendamentos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> buscarPorId(@PathVariable UUID id) {
        // log.info("Requisição para buscar agendamento por ID: {}", id);
        // O .orElseThrow do service já lança RecursoNaoEncontradoException, que é tratado pelo GlobalExceptionHandler
        Agendamento agendamento = service.buscarPorId(id);
        return ResponseEntity.ok(toResponseDTO(agendamento));
    }

    @GetMapping("/disponibilidade")
    public ResponseEntity<List<LocalDateTime>> getHorariosDisponiveis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam UUID servicoId) {
        List<LocalDateTime> horariosDisponiveis = service.getHorariosDisponiveisNoDia(data, servicoId); // Passa servicoId
        return ResponseEntity.ok(horariosDisponiveis);
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody AgendamentoRequestDTO agendamentoDTO,
                @RequestHeader("Authorization") String authorizationHeader) {

        UUID idCliente;
        try {
            idCliente = service.validateTokenAndGetUserId(authorizationHeader);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (AuthServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado ao buscar agendamentos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro interno ao processar a requisição.");
        }
        // log.info("Requisição para criar novo agendamento para usuário ID: {} e serviço ID: {}", agendamentoDTO.usuarioId(), agendamentoDTO.servicoId());
        Agendamento novoAgendamento = service.salvar(agendamentoDTO, idCliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(novoAgendamento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> atualizar(@PathVariable UUID id, @Valid @RequestBody AgendamentoRequestDTO agendamentoDTO) {

        // log.info("Requisição para atualizar agendamento ID: {}", id);
        Agendamento agendamentoAtualizado = service.atualizarAgendamento(id, agendamentoDTO);
        return ResponseEntity.ok(toResponseDTO(agendamentoAtualizado));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AgendamentoResponseDTO> atualizarParcial(@PathVariable UUID id, @RequestBody AgendamentoRequestDTO agendamentoDTO) {
        Agendamento agendamentoAtualizado = service.atualizarParcialAgendamento(id, agendamentoDTO);
        return ResponseEntity.ok(toResponseDTO(agendamentoAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        // log.info("Requisição para deletar agendamento por ID: {}", id);
        // O service.deletar já lança RecursoNaoEncontradoException se o ID não existir,
        // que será capturada pelo GlobalExceptionHandler.
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{agendamentoId}/documentos/{documentoCatalogoId}/status")
    public ResponseEntity<DocumentoPendenteResponseDTO> atualizarStatusDocumentoAgendamento(
            @PathVariable UUID agendamentoId,
            @PathVariable UUID documentoCatalogoId,
            @Valid @RequestBody DocumentoStatusUpdateRequestDTO requestDTO) { // Adicionado @Valid
        // log.info("Requisição para atualizar status do documento de catálogo ID {} para agendamento ID {}.", documentoCatalogoId, agendamentoId);
        DocumentoPendenteResponseDTO documentoAtualizado = service.atualizarStatusDocumentoAgendamento(agendamentoId, documentoCatalogoId, requestDTO);
        return ResponseEntity.ok(documentoAtualizado); // Retorna o DTO do documento atualizado
    }

    @GetMapping("/cliente")
    public ResponseEntity<?>  buscarPorCliente(
            @RequestHeader("Authorization") String authorizationHeader) {

        UUID idCliente;
        try {
            idCliente = service.validateTokenAndGetUserId(authorizationHeader);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (AuthServiceException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado ao buscar agendamentos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro interno ao processar a requisição.");
        }

        List<AgendamentoResponseDTO> agendamentos = service.buscarPorCliente(idCliente).stream()
                                                            .map(this::toResponseDTO)
                                                            .collect(Collectors.toList());

        return ResponseEntity.ok(agendamentos);
    }
    
    @GetMapping("/cliente/{id}")
    public ResponseEntity<List<AgendamentoResponseDTO>>  buscarPorClienteId(@PathVariable UUID id) {
        List<AgendamentoResponseDTO> agendamentos = service.buscarPorCliente(id).stream()
                                                            .map(this::toResponseDTO)
                                                            .collect(Collectors.toList());

        return ResponseEntity.ok(agendamentos);
    }
    
}
