// Location: com.microsservicos.Catalogo_Service.dto.SetorResponse

package com.microsservicos.Catalogo_Service.dto;

import java.util.List; // Importar List
import java.util.UUID;

public record SetorResponse(
        UUID id,
        String nome,
        String descricao,
        boolean isAtivo,
        int prioridade,
        Integer tempoMedioMinutos,
        List<UUID> documentosObrigatoriosIds // NOVO: IDs dos documentos obrigatórios
) {
}