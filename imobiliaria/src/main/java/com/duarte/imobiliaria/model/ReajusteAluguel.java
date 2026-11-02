package com.duarte.imobiliaria.model;

import com.duarte.imobiliaria.controllers.DTO.ReajusteDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReajusteAluguel {

    private Integer idReajuste;
    private LocalDate data;
    private BigDecimal novoValor;
    private String indiceUtilizado;
    private ContratoLocacao contratoLocacao;

    public ReajusteAluguel() {
    }

    public ReajusteAluguel(Integer idReajuste) {
        this.idReajuste = idReajuste;
    }

    public ReajusteAluguel(Integer idReajuste, LocalDate data, BigDecimal novoValor, String indiceUtilizado, ContratoLocacao contratoLocacao) {
        this.idReajuste = idReajuste;
        this.data = data;
        this.novoValor = novoValor;
        this.indiceUtilizado = indiceUtilizado;
        this.contratoLocacao = contratoLocacao;
    }

    public ReajusteAluguel(ReajusteDTO dto) {
        this.novoValor = dto.getNovoValor();
        this.indiceUtilizado = dto.getIndiceUtilizado();
    }

    public ReajusteAluguel(ContratoLocacao contratoLocacao, ReajusteDTO dto) {
        this.contratoLocacao = contratoLocacao;
        this(dto);
    }

    public Integer getIdReajuste() {
        return idReajuste;
    }

    public void setIdReajuste(Integer idReajuste) {
        this.idReajuste = idReajuste;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public BigDecimal getNovoValor() {
        return novoValor;
    }

    public void setNovoValor(BigDecimal novoValor) {
        this.novoValor = novoValor;
    }

    public String getIndiceUtilizado() {
        return indiceUtilizado;
    }

    public void setIndiceUtilizado(String indiceUtilizado) {
        this.indiceUtilizado = indiceUtilizado;
    }

    public ContratoLocacao getContratoLocacao() {
        return contratoLocacao;
    }

    public void setContratoLocacao(ContratoLocacao contratoLocacao) {
        this.contratoLocacao = contratoLocacao;
    }

}
