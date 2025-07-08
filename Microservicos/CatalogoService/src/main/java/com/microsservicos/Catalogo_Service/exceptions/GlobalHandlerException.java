package com.microsservicos.Catalogo_Service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(SetorNaoEncontradoException.class)
    public ResponseEntity<String> handleSetorNaoEncontrado(SetorNaoEncontradoException ex) {
        // Agora, sempre que a exceção for lançada, a API retornará 404
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // 404
                .body(ex.getMessage());
    }
}