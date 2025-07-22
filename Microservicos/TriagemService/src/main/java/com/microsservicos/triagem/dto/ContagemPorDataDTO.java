package com.microsservicos.triagem.dto;

import java.time.LocalDate;

public record ContagemPorDataDTO(LocalDate data, Long quantidade) {
}
