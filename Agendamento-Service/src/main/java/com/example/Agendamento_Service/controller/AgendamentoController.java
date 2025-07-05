package com.example.Agendamento_Service.controller;

import com.example.Agendamento_Service.model.Agendamento;
import com.example.Agendamento_Service.service.AgendamentoService;
import com.example.Agendamento_Service.dto.AgendamentoRequestDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Agendamento> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable UUID id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/disponibilidade")
    public ResponseEntity<List<LocalDateTime>> getHorariosDisponiveis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        List<LocalDateTime> horariosDisponiveis = service.getHorariosDisponiveisNoDia(data);
        return ResponseEntity.ok(horariosDisponiveis);
    }

    @PostMapping
    public ResponseEntity<Agendamento> criar(@Valid @RequestBody AgendamentoRequestDTO agendamentoDTO) {

        Agendamento novoAgendamento = service.salvar(agendamentoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAgendamento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Agendamento> atualizar(@PathVariable UUID id, @Valid @RequestBody AgendamentoRequestDTO agendamentoDTO) {

        Agendamento agendamentoAtualizado = service.atualizarAgendamento(id, agendamentoDTO);
        return ResponseEntity.ok(agendamentoAtualizado);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Agendamento> atualizarEspecifico(@PathVariable UUID id, @RequestBody AgendamentoRequestDTO agendamentoDTO) {

        Agendamento agendamentoAtualizado = service.atualizarParcialAgendamento(id, agendamentoDTO);
        return ResponseEntity.ok(agendamentoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        if (service.buscarPorId(id).isPresent()) {
            service.deletar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}