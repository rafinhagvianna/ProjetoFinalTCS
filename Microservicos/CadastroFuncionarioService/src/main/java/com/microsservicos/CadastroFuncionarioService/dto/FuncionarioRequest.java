package com.microsservicos.CadastroFuncionarioService.dto;

public record FuncionarioRequest(
        String nome,
        String cpf,
        String senha,
        String email
) {}
