package com.microsservicos.documentacao.client;

import com.microsservicos.documentacao.dto.DocumentoStatusUpdateRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "AgendamentoService") // URL do Agendamento Service
public interface AgendamentoServiceClient {

    // Endpoint que o Agendamento Service precisará expor
    @PutMapping("/api/agendamentos/{agendamentoId}/documentos/{documentoCatalogoId}/status")
    void atualizarStatusDocumentoAgendamento(
            @PathVariable UUID agendamentoId,
            @PathVariable UUID documentoCatalogoId,
            @RequestBody DocumentoStatusUpdateRequestDTO requestDTO);
}