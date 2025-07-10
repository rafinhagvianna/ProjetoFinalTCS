// src/main/java/com/microsservicos/Catalogo_Service/controller/DocumentoCatalogoController.java
package com.microsservicos.Catalogo_Service.controller;

import com.microsservicos.Catalogo_Service.dto.DocumentoCatalogoRequest;
import com.microsservicos.Catalogo_Service.dto.DocumentoCatalogoResponse;
import com.microsservicos.Catalogo_Service.service.DocumentoCatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documentos") // Endpoint específico para documentos
@RequiredArgsConstructor
public class DocumentoCatalogoController {

    private final DocumentoCatalogoService documentoCatalogoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoCatalogoResponse criarDocumento(@RequestBody DocumentoCatalogoRequest request) {
        return documentoCatalogoService.criarDocumento(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DocumentoCatalogoResponse> listarDocumentos() {
        return documentoCatalogoService.listarDocumentos();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DocumentoCatalogoResponse buscarDocumentoPorId(@PathVariable UUID id) {
        return documentoCatalogoService.buscarDocumentoPorId(id);
    }

    @PutMapping("/{id}") // PUT para atualização completa
    @ResponseStatus(HttpStatus.OK)
    public DocumentoCatalogoResponse atualizarDocumento(@PathVariable UUID id, @RequestBody DocumentoCatalogoRequest request) {
        return documentoCatalogoService.atualizarDocumento(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content para remoção bem-sucedida
    public void removerDocumento(@PathVariable UUID id) {
        documentoCatalogoService.removerDocumento(id);
    }
}