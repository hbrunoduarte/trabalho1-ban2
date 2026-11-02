package com.duarte.imobiliaria.controllers.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReciboPainelDTO(
        Integer idRecibo,
        Integer mes,
        Integer ano,
        LocalDate dataVencimento,
        LocalDate dataPagamento,
        BigDecimal valorAluguel,
        BigDecimal desconto,
        BigDecimal acrescimo,
        BigDecimal valorLiquido,
        String formaPagamento
) {
}
