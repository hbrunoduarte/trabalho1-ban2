package com.duarte.imobiliaria.controllers.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RescisaoSimulacaoDTO(
        LocalDate dataRescisao,
        BigDecimal valorAluguel,
        Integer prazoMeses,
        Integer mesesCumpridos,
        Integer mesesRestantes,
        BigDecimal multa
) {
}
