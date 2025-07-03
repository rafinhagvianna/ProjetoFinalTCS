package com.microsservicos.triagem.controller;

import com.microsservicos.triagem.dto.AtualizarStatusTriagemDTO;
import com.microsservicos.triagem.dto.TriagemRequestDTO;
import com.microsservicos.triagem.dto.TriagemResponseDTO;
import com.microsservicos.triagem.service.TriagemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/triagens")
public class TriagemController {

    private final TriagemService triagemService;

    public TriagemController(TriagemService triagemService) {
        this.triagemService = triagemService;
    }

    @PostMapping
    public ResponseEntity<TriagemResponseDTO> criarTriagem(@RequestBody TriagemRequestDTO dto) {
        return ResponseEntity
                .status(201)
                .body(triagemService.criarTriagem(dto));
    }

    @GetMapping("/proxima")
    public ResponseEntity<TriagemResponseDTO> buscarProximaTriagem() {
        return ResponseEntity
                .ok(triagemService.buscarProximaTriagem());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TriagemResponseDTO> atualizarStatusTriagem(
            @PathVariable Long id,
            @RequestBody AtualizarStatusTriagemDTO dto
    ) {
        TriagemResponseDTO response = triagemService.atualizarStatus(id, dto.novoStatus());
        return ResponseEntity.ok(response);
    }
}