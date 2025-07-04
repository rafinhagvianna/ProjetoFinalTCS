package com.microsservicos.documentacao.enums;

public enum StatusDocumento {
    PENDENTE_UPLOAD, // Documento esperado, mas ainda não enviado
    AGUARDANDO_VALIDACAO, // Upload realizado, aguardando validação
    VALIDADO, // Documento validado e aceito
    REJEITADO, // Documento rejeitado (ex: ilegível, incorreto)
    AGENDADO_REVISAO // Para documentos que precisam de revisão manual ou agendamento específico
}