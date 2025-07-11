package com.microsservicos.triagem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID; // Importe UUID

public record TriagemRequestDTO(
        @NotNull UUID servicoId, // ALTERADO: De Long para UUID
        @NotNull @Min(1) Integer prioridade
) {}