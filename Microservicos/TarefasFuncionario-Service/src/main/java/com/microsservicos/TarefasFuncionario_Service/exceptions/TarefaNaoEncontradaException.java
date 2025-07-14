package com.microsservicos.TarefasFuncionario_Service.exceptions;

import java.util.UUID;

public class TarefaNaoEncontradaException extends RuntimeException {
    public TarefaNaoEncontradaException(UUID id) {
        super("Tarefa com ID '" + id + "' não encontrado.");
    }
}
