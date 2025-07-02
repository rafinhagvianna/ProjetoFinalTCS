package com.microsservicos.triagem.controller;

import com.microsservicos.triagem.dto.TriagemRequestDTO;
import com.microsservicos.triagem.dto.TriagemResponseDTO;
import com.microsservicos.triagem.service.TriagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/triagens")
public class TriagemController {

    @Autowired
    private TriagemService triagemService;

    @PostMapping
    public ResponseEntity<TriagemResponseDTO> criarTriagem(@RequestBody TriagemRequestDTO dto) {
        TriagemResponseDTO response = triagemService.criarTriagem(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/proxima")
    public ResponseEntity<TriagemResponseDTO> buscarProximaTriagem() {
        TriagemResponseDTO response = triagemService.buscarProximaTriagem();
        return ResponseEntity.ok(response);
    }
}