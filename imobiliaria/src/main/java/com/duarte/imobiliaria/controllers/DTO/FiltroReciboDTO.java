package com.duarte.imobiliaria.controllers.DTO;

import com.duarte.imobiliaria.formsValidation.groups.ValidacaoAvancada;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoBasica;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoFormato;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@GroupSequence({ValidacaoBasica.class, ValidacaoFormato.class, ValidacaoAvancada.class, FiltroReciboDTO.class})
public class FiltroReciboDTO {

    public static final String ANO_PATTERN = "\\d{4}";
    public static final String SITUACAO_PATTERN = "PAGO|PENDENTE";

    private Integer idLocacao;

    @Pattern(regexp = ANO_PATTERN, message = "{recibo.ano.formato}", groups = ValidacaoFormato.class)
    private String ano;

    @Pattern(regexp = SITUACAO_PATTERN, message = "{recibo.situacao.invalida}", groups = ValidacaoFormato.class)
    private String situacao;

    public Integer getIdLocacao() {
        return idLocacao;
    }

    public void setIdLocacao(Integer idLocacao) {
        this.idLocacao = idLocacao;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    @JsonIgnore
    public boolean isPago() {
        return situacao.equals("PAGO");
    }

    @JsonIgnore
    public boolean isPendente() {
        return situacao.equals("PENDENTE");
    }
}
