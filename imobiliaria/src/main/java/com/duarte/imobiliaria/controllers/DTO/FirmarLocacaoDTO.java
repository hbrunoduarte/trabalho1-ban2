package com.duarte.imobiliaria.controllers.DTO;

import com.duarte.imobiliaria.formsValidation.groups.ValidacaoAvancada;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoBasica;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoFormato;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.GroupSequence;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@GroupSequence({ValidacaoBasica.class, ValidacaoFormato.class, ValidacaoAvancada.class, FirmarLocacaoDTO.class})
public class FirmarLocacaoDTO {

    @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    private Integer idVisita;

    @NotNull(message = "Selecione o tipo de garantia", groups = ValidacaoBasica.class)
    @Pattern(regexp = "FIADOR|SEGURO FIANCA|DEPOSITO CAUCAO", message = "Selecione o tipo de garantia")
    private String garantia;

    @Positive(message = "contrato.valorcaucao.invalido", groups = ValidacaoBasica.class)
    private BigDecimal valorCaucao;

    @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Min(value = 1, message = "A duração deve ser de pelo menos 1 mês")
    @Max(value = 600, message = "A duração deve ser de no máximo 600 meses")
    private Integer prazoMeses;

    @NotNull(message = "Escolha o dia de vencimento", groups = ValidacaoBasica.class)
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

    @JsonIgnore
    public boolean hasFiador() {
        return fiador != null;
    }

    @JsonIgnore
    public boolean isContratoValido() {
        return garantia.equals("SEGURO FIANCA") ||
                (garantia.equals("FIADOR") && hasFiador()) ||
                (garantia.equals("DEPOSITO CAUCAO") && valorCaucao != null);
    }

    @JsonIgnore
    public Map<String, String> getMensagemErro(MessageSource ms) {

        Map<String, String> map = new HashMap<>();

        if (garantia.equals("DEPOSITO CAUCAO")) {
            map.put("valorCaucao", ms.getMessage("contrato.valorcaucao.invalido", null, "contrato.valorcaucao.invalido", LocaleContextHolder.getLocale()));
        }

        if (garantia.equals("FIADOR")) {
            map.put("geral", "Informe um fiador");
        }

        return map;
    }
}
