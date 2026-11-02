package com.duarte.imobiliaria.model;

import com.duarte.imobiliaria.controllers.DTO.ReciboPainelDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReciboAluguel {

    private Integer idRecibo;
    private BigDecimal desconto;
    private BigDecimal acrescimo;
    private BigDecimal valorLiquido;
    private String formaPagamento;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private BigDecimal valorAluguel;
    private Integer mes;
    private Integer ano;
    private ContratoLocacao contratoLocacao;

    public ReciboAluguel() {
    }

    public ReciboAluguel(Integer idRecibo) {
        this.idRecibo = idRecibo;
    }

    public ReciboAluguel(Integer idRecibo, BigDecimal desconto, BigDecimal acrescimo, BigDecimal valorLiquido, String formaPagamento, LocalDate dataVencimento, LocalDate dataPagamento, BigDecimal valorAluguel, Integer mes, Integer ano, ContratoLocacao contratoLocacao) {
        this.idRecibo = idRecibo;
        this.desconto = desconto;
        this.acrescimo = acrescimo;
        this.valorLiquido = valorLiquido;
        this.formaPagamento = formaPagamento;
        this.dataVencimento = dataVencimento;
        this.dataPagamento = dataPagamento;
        this.valorAluguel = valorAluguel;
        this.mes = mes;
        this.ano = ano;
        this.contratoLocacao = contratoLocacao;
    }

    public Integer getIdRecibo() {
        return idRecibo;
    }

    public void setIdRecibo(Integer idRecibo) {
        this.idRecibo = idRecibo;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getAcrescimo() {
        return acrescimo;
    }

    public void setAcrescimo(BigDecimal acrescimo) {
        this.acrescimo = acrescimo;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public BigDecimal getValorAluguel() {
        return valorAluguel;
    }

    public void setValorAluguel(BigDecimal valorAluguel) {
        this.valorAluguel = valorAluguel;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public ContratoLocacao getContratoLocacao() {
        return contratoLocacao;
    }

    public void setContratoLocacao(ContratoLocacao contratoLocacao) {
        this.contratoLocacao = contratoLocacao;
    }

    @JsonIgnore
    public ReciboPainelDTO getDTO() {
        return new ReciboPainelDTO(
                idRecibo,
                mes,
                ano,
                dataVencimento,
                dataPagamento,
                valorAluguel,
                desconto,
                acrescimo,
                valorLiquido,
                formaPagamento
        );
    }
}
