package com.duarte.imobiliaria.controllers.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public class FirmarLocacaoDTO {

    @NotNull(message = "{cadastro.campo.obrigatorio}")
    private Integer idVisita;

    @NotNull(message = "Selecione o tipo de garantia")
    @Pattern(regexp = "FIADOR|SEGURO FIANCA|DEPOSITO CAUCAO", message = "Selecione o tipo de garantia")
    private String garantia;

    private BigDecimal valorCaucao;

    @NotNull(message = "{cadastro.campo.obrigatorio}")
    @Min(value = 1, message = "A duração deve ser de pelo menos 1 mês")
    @Max(value = 600, message = "A duração deve ser de no máximo 600 meses")
    private Integer prazoMeses;

    @NotNull(message = "Escolha o dia de vencimento")
    @Min(value = 1, message = "Escolha um dia de vencimento entre 1 e 28")
    @Max(value = 28, message = "Escolha um dia de vencimento entre 1 e 28")
    private Integer diaVencimento;

    @Valid
    private FiadorCadastroDTO fiador;

    public Integer getIdVisita() {
        return idVisita;
    }

    public void setIdVisita(Integer idVisita) {
        this.idVisita = idVisita;
    }

    public String getGarantia() {
        return garantia;
    }

    public void setGarantia(String garantia) {
        this.garantia = garantia;
    }

    public BigDecimal getValorCaucao() {
        return valorCaucao;
    }

    public void setValorCaucao(BigDecimal valorCaucao) {
        this.valorCaucao = valorCaucao;
    }

    public Integer getPrazoMeses() {
        return prazoMeses;
    }

    public void setPrazoMeses(Integer prazoMeses) {
        this.prazoMeses = prazoMeses;
    }

    public Integer getDiaVencimento() {
        return diaVencimento;
    }

    public void setDiaVencimento(Integer diaVencimento) {
        this.diaVencimento = diaVencimento;
    }

    public FiadorCadastroDTO getFiador() {
        return fiador;
    }

    public void setFiador(FiadorCadastroDTO fiador) {
        this.fiador = fiador;
    }

    public boolean hasFiador() {
        return fiador != null;
    }
}
