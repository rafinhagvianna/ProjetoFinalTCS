package com.example.Agendamento_Service.controller;

import com.example.Agendamento_Service.model.Agendamento;
import com.example.Agendamento_Service.service.AgendamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/agendamentos")
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

    @PostMapping
    public Agendamento criar(@RequestBody Agendamento agendamento) {
        return service.salvar(agendamento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Agendamento> atualizar(@PathVariable UUID id, @RequestBody Agendamento atualizado) {
        return service.buscarPorId(id)
                .map(agendamento -> {
                    atualizado.setId(id);
                    return ResponseEntity.ok(service.salvar(atualizado));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Agendamento> atualizarEspecifico(@PathVariable UUID id, @RequestBody Agendamento atualizado) {
        return service.buscarPorId(id)
                .map(agendamentoExistente -> {
                    if (atualizado.getUsuarioId() != null)
                        agendamentoExistente.setUsuarioId(atualizado.getUsuarioId());

                    if (atualizado.getAtendenteId() != null)
                        agendamentoExistente.setAtendenteId(atualizado.getAtendenteId());

                    if (atualizado.getServicoId() != null)
                        agendamentoExistente.setServicoId(atualizado.getServicoId());

                    if (atualizado.getDataHora() != null)
                        agendamentoExistente.setDataHora(atualizado.getDataHora());

                    if (atualizado.getAtendidoEm() != null)
                        agendamentoExistente.setAtendidoEm(atualizado.getAtendidoEm());

                    if (atualizado.getObservacoes() != null)
                        agendamentoExistente.setObservacoes(atualizado.getObservacoes());

                    return ResponseEntity.ok(service.salvar(agendamentoExistente));
                })
                .orElse(ResponseEntity.notFound().build());
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