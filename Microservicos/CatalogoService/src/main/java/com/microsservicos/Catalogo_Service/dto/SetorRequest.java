// Location: com.microsservicos.Catalogo_Service.dto.SetorRequest

package com.microsservicos.Catalogo_Service.dto;

import java.util.List; // Importar List
import java.util.UUID;

public record SetorRequest(
        UUID id, // For updates, though often ID is not sent in request body
        String nome,
        String descricao,
        boolean isAtivo,
        int prioridade,
        Integer tempoMedioMinutos,
        List<UUID> documentosObrigatoriosIds, // NOVO: IDs dos documentos obrigatórios
        String icone
) {
}