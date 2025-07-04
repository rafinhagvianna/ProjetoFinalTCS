package com.microsservicos.triagem.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

// Ajuste o name e a url conforme sua configuração real do FeignClient
@FeignClient(name = "catalogo-service", url = "${clients.catalogo-service.url}")
public interface CatalogoServiceClient {

    @GetMapping("/api/catalogo/servicos/{servicoId}/documentos")
    List<TipoDocumentoResponse> getDocumentosNecessariosParaServico(@PathVariable("servicoId") UUID servicoId);

    @GetMapping("/api/catalogo/servicos/{servicoId}/nome")
    String getNomeServico(@PathVariable("servicoId") UUID servicoId);

    // NOVO MÉTODO: Para obter o serviço completo com duração
    @GetMapping("/api/catalogo/servicos/{servicoId}")
    ServicoResponse getServicoById(@PathVariable("servicoId") UUID servicoId); // <-- NOVO MÉTODO
}