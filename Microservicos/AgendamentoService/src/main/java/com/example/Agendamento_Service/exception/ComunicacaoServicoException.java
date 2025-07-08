package com.example.Agendamento_Service.exception;

public class ComunicacaoServicoException extends RuntimeException {
    public ComunicacaoServicoException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComunicacaoServicoException(String message) {
        super(message);
    }
}