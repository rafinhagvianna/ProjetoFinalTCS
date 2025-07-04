package com.microsservicos.Catalogo_Service.exceptions;

import com.microsservicos.Catalogo_Service.dto.SetorRequest;
import com.microsservicos.Catalogo_Service.dto.SetorResponse;
import com.microsservicos.Catalogo_Service.model.Setor;

import java.util.UUID;

public class SetorNaoEncontradoException extends RuntimeException {
    public SetorNaoEncontradoException(UUID id) {
        super("Setor com ID '" + id + "' não encontrado.");
    }
}