package com.microsservicos.CadastroClienteService.dto;

public record ClienteRequest(
        String nome,
        String cpf,
        String telefone,
        String email,
        String senha,
        String agencia,
        String conta
) {}
