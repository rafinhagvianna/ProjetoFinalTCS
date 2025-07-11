package com.microsservicos.CadastroClienteService.dto;

import java.util.UUID;

public record LoginResponse(String message, String nome, String email, UUID id) {
}