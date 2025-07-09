package com.microsservicos.triagem.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;


@FeignClient(name = "CatalogoService")
public interface CatalogoServiceClient {

    @GetMapping("/api/setor/{id}")
    SetorResponse getSetorById(@PathVariable("id") UUID id);

    @GetMapping("/api/setor/{id}/nome") 
    String getNomeSetor(@PathVariable("id") UUID id); 

    @GetMapping("/api/documentos-catalogo/{id}")
    DocumentoCatalogoResponse getDocumentoCatalogoById(@PathVariable("id") UUID id);

    @GetMapping("/api/documentos-catalogo")
    List<DocumentoCatalogoResponse> getAllDocumentosCatalogo();

}