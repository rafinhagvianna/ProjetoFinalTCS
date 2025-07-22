package com.microsservicos.triagem.controller;

import com.microsservicos.triagem.dto.ContagemPorDataDTO;
import com.microsservicos.triagem.dto.ContagemPorItemDTO;
import com.microsservicos.triagem.service.TriagemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final TriagemService triagemService;

    public AnalyticsController(TriagemService triagemService) {
        this.triagemService = triagemService;
    }

    @GetMapping("/servicos-mais-utilizados")
    public ResponseEntity<List<ContagemPorItemDTO>> getServicosMaisUtilizados() {
        return ResponseEntity.ok(triagemService.contarAtendimentosPorServico());
    }

    @GetMapping("/atendimentos-por-dia")
    public ResponseEntity<List<ContagemPorDataDTO>> getAtendimentosPorDia() {
        return ResponseEntity.ok(triagemService.contarAtendimentosPorDia());
    }
}