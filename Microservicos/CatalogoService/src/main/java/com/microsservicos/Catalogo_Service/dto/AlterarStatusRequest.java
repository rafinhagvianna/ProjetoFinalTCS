package com.microsservicos.Catalogo_Service.dto;

public record AlterarStatusRequest() {
    public record AlteraStatusRequest(boolean isAtivo) {}

}
