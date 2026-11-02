package com.duarte.imobiliaria.controllers.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class FirmarVendaDTO {

    @NotNull(message = "{cadastro.campo.obrigatorio}")
    private Integer idVisita;

    @NotNull(message = "Informe se haverá valor de sinal")
    private Boolean haSinal;

    private BigDecimal valorSinal;

    @NotBlank(message = "{cadastro.campo.obrigatorio}")
    @Size(max = 20, message = "A forma de pagamento deve ter no máximo 20 caracteres")
    private String formaPagamento;

    @NotNull(message = "Informe se o valor foi financiado")
    private Boolean financiado;

    private BigDecimal valorFinanciado;

    public Integer getIdVisita() {
        return idVisita;
    }

    public void setIdVisita(Integer idVisita) {
        this.idVisita = idVisita;
    }

    public Boolean getHaSinal() {
        return haSinal;
    }

    public void setHaSinal(Boolean haSinal) {
        this.haSinal = haSinal;
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

    public Boolean getFinanciado() {
        return financiado;
    }

    public void setFinanciado(Boolean financiado) {
        this.financiado = financiado;
    }

    public BigDecimal getValorFinanciado() {
        return valorFinanciado;
    }

    public void setValorFinanciado(BigDecimal valorFinanciado) {
        this.valorFinanciado = valorFinanciado;
    }
}
