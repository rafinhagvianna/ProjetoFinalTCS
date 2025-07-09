package com.example.Agendamento_Service.enums;

public enum StatusAgendamento {
    PENDENTE,
    AGENDADO,
    CONFIRMADO,
    CANCELADO,
    CONCLUIDO,
    EM_ATENDIMENTO // Se o agendamento puder ter um status de "em andamento"
}