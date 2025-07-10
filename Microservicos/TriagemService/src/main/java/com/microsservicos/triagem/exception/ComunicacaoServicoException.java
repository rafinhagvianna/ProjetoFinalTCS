package com.microsservicos.triagem.exception;


// Exemplo 2: Falha na comunicação com outro serviço
public class ComunicacaoServicoException extends RuntimeException {
    public ComunicacaoServicoException(String message, Throwable cause) {
        super(message, cause);
    }
}

