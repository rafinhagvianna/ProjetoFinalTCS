package com.example.Agendamento_Service.enums;

public enum StatusDocumento {
    PENDENTE,      // O cliente ainda não enviou o documento
    ENVIADO,       // O cliente enviou, aguardando análise
    APROVADO,      // O documento foi verificado e está correto
    REJEITADO      // O documento foi analisado e recusado
}
