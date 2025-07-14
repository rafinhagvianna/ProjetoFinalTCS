package com.microsservicos.TarefasFuncionario_Service.exceptions;

public class ValidacaoException extends RuntimeException {
  public ValidacaoException(String mensagem) {
    super(mensagem);
  }
}
