package com.duarte.imobiliaria.controllers.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record VisitaPainelDTO(
        Integer idVisita,
        LocalDate data,
        LocalTime horario,
        String proprietario,
        String cliente,
        String endereco,
        String tipoContrato,
        BigDecimal valor,
        Boolean arquivada
) {
}
