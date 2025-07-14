package com.microsservicos.TarefasFuncionario_Service.exceptions;

public class RecursoDuplicadoException extends RuntimeException {
    public RecursoDuplicadoException(String mensagem) {
        super(mensagem);
    }
}
