package com.microsservicos.documentacao.client;

import com.microsservicos.documentacao.dto.DocumentoStatusUpdateRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "TriagemService") // URL do Triagem Service
public interface TriagemServiceClient {

    // Endpoint que o Triagem Service precisará expor para receber atualizações de documentos
    @PutMapping("/api/triagens/{triagemId}/documentos/{documentoCatalogoId}/status")
    void atualizarStatusDocumentoTriagem(
            @PathVariable UUID triagemId,
            @PathVariable UUID documentoCatalogoId,
            @RequestBody DocumentoStatusUpdateRequestDTO requestDTO);
}