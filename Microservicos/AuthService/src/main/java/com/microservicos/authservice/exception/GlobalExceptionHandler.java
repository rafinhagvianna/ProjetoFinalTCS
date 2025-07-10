package com.microservicos.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Você pode criar uma classe de DTO para mensagens de erro padronizadas
record ErrorResponseDTO(String message, HttpStatus status, String errorCode) {}

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage(), HttpStatus.UNAUTHORIZED, "AUTH-001");
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Coleta todas as mensagens de erro de validação
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst() // Pode pegar o primeiro erro ou concatenar todos
                .orElse("Erro de validação na requisição.");

        ErrorResponseDTO error = new ErrorResponseDTO(errorMessage, HttpStatus.BAD_REQUEST, "VALID-001");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        // Logar a exceção completa aqui
        // log.error("Ocorreu um erro interno: {}", ex.getMessage(), ex);
        ErrorResponseDTO error = new ErrorResponseDTO("Ocorreu um erro inesperado no servidor. Erro: "+ ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, "GENERIC-001");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}