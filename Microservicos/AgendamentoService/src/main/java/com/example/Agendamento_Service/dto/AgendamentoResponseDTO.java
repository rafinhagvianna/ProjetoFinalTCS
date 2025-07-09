package com.example.Agendamento_Service.dto;

import com.example.Agendamento_Service.enums.StatusAgendamento;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AgendamentoResponseDTO(
    UUID id,
    UUID usuarioId,
    String nomeClienteSnapshot, // Inclua o snapshot do nome do cliente
    UUID atendenteId,
    UUID servicoId,
    String nomeServicoSnapshot, // Inclua o snapshot do nome do serviço
    LocalDateTime dataHora,
    LocalDateTime atendidoEm,
    String observacoes,
    LocalDateTime criadoEm,
    StatusAgendamento status,
    List<DocumentoPendenteResponseDTO> documentosPendentes // Lista de documentos pendentes
) { }