package com.microsservicos.CadastroFuncionarioService.dto;

public record
FuncionarioResponse(
        String nome,
        String cpf,
        String senha
) {}
