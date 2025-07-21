package com.microsservicos.CadastroClienteService.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Crie uma classe nova para tratar exceções
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String errorMessage = "Dados já cadastrados. ";
        // Analisa a mensagem de erro para ver qual campo foi violado
        if (ex.getCause().getCause().getMessage().contains("cpf")) {
            errorMessage += "O CPF informado já está em uso.";
        } else if (ex.getCause().getCause().getMessage().contains("email")) {
            errorMessage += "O e-mail informado já está em uso.";
        } else {
            errorMessage += "Verifique os dados e tente novamente.";
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        // Retorna a mensagem definida no service com um status 400 Bad Request
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}