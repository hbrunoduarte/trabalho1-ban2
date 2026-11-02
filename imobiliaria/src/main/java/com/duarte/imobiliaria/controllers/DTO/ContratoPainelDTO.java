package com.duarte.imobiliaria.controllers.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContratoPainelDTO(
        String tipo,
        Integer id,
        LocalDate data,
        String cliente,
        String proprietario,
        String endereco,
        BigDecimal valor,
        BigDecimal valorComissao,
        // locação
        String garantia,
        BigDecimal valorCaucao,
        Integer prazoMeses,
        Integer diaVencimento,
        String status,
        // locação rescindida
        LocalDate dataRescisao,
        BigDecimal multa,
        String motivo,
        // venda
        BigDecimal valorSinal,
        String formaPagamento,
        BigDecimal valorFinanciado
) {

    public static ContratoPainelDTO locacao(Integer id, LocalDate data, String cliente, String proprietario,
                                            String endereco, BigDecimal valorAluguel, BigDecimal valorComissao,
                                            String garantia, BigDecimal valorCaucao, Integer prazoMeses,
                                            Integer diaVencimento, String status,
                                            LocalDate dataRescisao, BigDecimal multa, String motivo) {
        return new ContratoPainelDTO("LOCACAO", id, data, cliente, proprietario, endereco, valorAluguel, valorComissao,
                garantia, valorCaucao, prazoMeses, diaVencimento, status, dataRescisao, multa, motivo,
                null, null, null);
    }

    public static ContratoPainelDTO venda(Integer id, LocalDate data, String cliente, String proprietario,
                                          String endereco, BigDecimal valorVenda, BigDecimal valorComissao,
                                          BigDecimal valorSinal, String formaPagamento, BigDecimal valorFinanciado) {
        return new ContratoPainelDTO("VENDA", id, data, cliente, proprietario, endereco, valorVenda, valorComissao,
                null, null, null, null, null, null, null, null, valorSinal, formaPagamento, valorFinanciado);
    }
}
