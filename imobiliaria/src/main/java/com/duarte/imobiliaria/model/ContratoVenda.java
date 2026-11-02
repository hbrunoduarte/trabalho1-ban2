package com.duarte.imobiliaria.model;

import com.duarte.imobiliaria.controllers.DTO.ContratoPainelDTO;
import com.duarte.imobiliaria.controllers.DTO.FirmarVendaDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ContratoVenda {

    private Integer idVenda;
    private LocalDate data;
    private BigDecimal valorVenda;
    private BigDecimal valorSinal;
    private String formaPagamento;
    private BigDecimal valorFinanciado;
    private BigDecimal valorComissao;
    private Cliente cliente;
    private Proprietario proprietario;
    private Imovel imovel;
    private Corretor corretor;

    public ContratoVenda() {
    }

    public ContratoVenda(Integer idVenda) {
        this.idVenda = idVenda;
    }

    public ContratoVenda(Integer idVenda, LocalDate data, BigDecimal valorVenda, BigDecimal valorSinal, String formaPagamento, BigDecimal valorFinanciado, BigDecimal valorComissao, Cliente cliente, Proprietario proprietario, Imovel imovel, Corretor corretor) {
        this.idVenda = idVenda;
        this.data = data;
        this.valorVenda = valorVenda;
        this.valorSinal = valorSinal;
        this.formaPagamento = formaPagamento;
        this.valorFinanciado = valorFinanciado;
        this.valorComissao = valorComissao;
        this.cliente = cliente;
        this.proprietario = proprietario;
        this.imovel = imovel;
        this.corretor = corretor;
    }

    public ContratoVenda(FirmarVendaDTO dto) {
        this.valorSinal = dto.getHaSinal() ? dto.getValorSinal() : null;
        this.formaPagamento = dto.getFormaPagamento();
        this.valorFinanciado = dto.getFinanciado() ? dto.getValorFinanciado() : null;
    }

    public ContratoVenda(Corretor c, FirmarVendaDTO dto) {
        this(dto);
        this.corretor = c;
    }

    public Integer getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(Integer idVenda) {
        this.idVenda = idVenda;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public BigDecimal getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(BigDecimal valorVenda) {
        this.valorVenda = valorVenda;
    }

    public BigDecimal getValorSinal() {
        return valorSinal;
    }

    public void setValorSinal(BigDecimal valorSinal) {
        this.valorSinal = valorSinal;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public BigDecimal getValorFinanciado() {
        return valorFinanciado;
    }

    public void setValorFinanciado(BigDecimal valorFinanciado) {
        this.valorFinanciado = valorFinanciado;
    }

    public BigDecimal getValorComissao() {
        return valorComissao;
    }

    public void setValorComissao(BigDecimal valorComissao) {
        this.valorComissao = valorComissao;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Proprietario getProprietario() {
        return proprietario;
    }

    public void setProprietario(Proprietario proprietario) {
        this.proprietario = proprietario;
    }

    public Imovel getImovel() {
        return imovel;
    }

    public void setImovel(Imovel imovel) {
        this.imovel = imovel;
    }

    public Corretor getCorretor() {
        return corretor;
    }

    public void setCorretor(Corretor corretor) {
        this.corretor = corretor;
    }

    @JsonIgnore
    public ContratoPainelDTO getDTO() {
        return ContratoPainelDTO.venda(
                idVenda,
                data,
                cliente.getNome(),
                proprietario.isFisico() ? proprietario.getNome() : proprietario.getRazaoSocial(),
                imovel.getEndereco().montarEndereco(),
                valorVenda,
                valorComissao,
                valorSinal,
                formaPagamento,
                valorFinanciado
        );
    }
}
