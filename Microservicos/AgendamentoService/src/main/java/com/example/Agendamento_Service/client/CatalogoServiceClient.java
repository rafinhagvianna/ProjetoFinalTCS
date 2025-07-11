package com.example.Agendamento_Service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "CatalogoService")
public interface CatalogoServiceClient {

    @GetMapping("/api/documentos/{id}")
    DocumentoCatalogoResponse getDocumentoCatalogoById(@PathVariable("id") UUID id);

    @GetMapping("/api/setor/{id}/nome") 
    String getNomeServico(@PathVariable("id") UUID id);

    @GetMapping("/api/setor/{id}")
    ServicoResponse getServicoById(@PathVariable("id") UUID id);
    
    @GetMapping("/api/documentos")
    List<DocumentoCatalogoResponse> getAllDocumentosCatalogo();
}
