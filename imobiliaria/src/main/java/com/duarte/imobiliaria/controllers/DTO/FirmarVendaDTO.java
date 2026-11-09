package com.duarte.imobiliaria.controllers.DTO;

import com.duarte.imobiliaria.formsValidation.groups.ValidacaoAvancada;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoBasica;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoFormato;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.*;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@GroupSequence({ValidacaoBasica.class, ValidacaoFormato.class, ValidacaoAvancada.class, FirmarVendaDTO.class})
public class FirmarVendaDTO {

    @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    private Integer idVisita;

    @NotNull(message = "Informe se haverá valor de sinal", groups = ValidacaoBasica.class)
    private Boolean haSinal;

    @Positive(message = "contrato.valorsinal.invalido", groups = ValidacaoBasica.class)
    private BigDecimal valorSinal;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Size(max = 20, message = "A forma de pagamento deve ter no máximo 20 caracteres", groups = ValidacaoAvancada.class)
    private String formaPagamento;

    @NotNull(message = "Informe se o valor foi financiado", groups = ValidacaoBasica.class)
    private Boolean financiado;

    @Positive(message = "contrato.valorfinanciado.invalido", groups = ValidacaoBasica.class)
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

    @JsonIgnore
    public boolean isContratoValido() {
        return (!haSinal || valorSinal != null) && (!financiado || valorFinanciado != null);
    }

    @JsonIgnore
    public Map<String, String> getMensagemErro(MessageSource ms) {

        Map<String, String> map = new HashMap<>();

        if (haSinal) {
            map.put("valorSinal", ms.getMessage("contrato.valorsinal.invalido", null, "contrato.valorsinal.invalido", LocaleContextHolder.getLocale()));
        }

        if (financiado) {
            map.put("valorFinanciado", ms.getMessage("contrato.valorfinanciado.invalido", null, "contrato.valorfinanciado.invalido", LocaleContextHolder.getLocale()));
        }

        return map;
    }
}
