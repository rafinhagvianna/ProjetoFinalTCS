package com.microsservicos.documentacao.dto;

import java.util.UUID;

import com.microsservicos.documentacao.enums.StatusDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ValidacaoDocumentoRequestDTO(
        @NotNull StatusDocumento novoStatus, // Ex: VALIDADO, REJEITADO
        // @NotBlank(message = "Observação é obrigatória para status REJEITADO") // Exemplo de validação condicional
        String observacao, // Motivo da validação/rejeição
        UUID triagemId,
        UUID agendamentoId,
        UUID documentoCatalogoId
        // Poderia ter um campo para o ID do validador (UUID) aqui
) {}