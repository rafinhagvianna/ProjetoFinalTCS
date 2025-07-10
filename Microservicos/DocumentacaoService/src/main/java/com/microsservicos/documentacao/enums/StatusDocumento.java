package com.microsservicos.documentacao.enums;

public enum StatusDocumento {
    PENDENTE, // Documento esperado, mas ainda não enviado
    ENVIADO, // Upload realizado, aguardando validação
    APROVADO, // Documento validado e aceito
    REJEITADO, // Documento rejeitado (ex: ilegível, incorreto)
    AGENDADO_REVISAO // Para documentos que precisam de revisão manual ou agendamento específico
}