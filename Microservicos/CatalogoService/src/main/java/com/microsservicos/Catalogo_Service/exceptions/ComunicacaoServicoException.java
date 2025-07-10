package com.microsservicos.Catalogo_Service.exceptions;

public class ComunicacaoServicoException extends RuntimeException {
    public ComunicacaoServicoException(String nomeServico) {
        super("O serviço '" + nomeServico + "' está temporariamente indisponível. A operação foi concluída com dados de fallback.");
    }
}