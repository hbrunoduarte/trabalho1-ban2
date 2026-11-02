package com.duarte.imobiliaria.controllers.DTO;

import com.duarte.imobiliaria.formsValidation.groups.ValidacaoAvancada;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoBasica;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Locale;

@GroupSequence({ValidacaoBasica.class, ValidacaoAvancada.class, ReajusteDTO.class})
public class ReajusteDTO {

    public static final int INDICE_MAX_SIZE = 10;

    @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @DecimalMin(value = "0.01", message = "{reajuste.valor.invalido}", groups = ValidacaoAvancada.class)
    @Digits(integer = 13, fraction = 2, message = "{reajuste.valor.invalido}", groups = ValidacaoAvancada.class)
    private BigDecimal novoValor;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Size(max = INDICE_MAX_SIZE, message = "{reajuste.indice.size}", groups = ValidacaoAvancada.class)
    private String indiceUtilizado;

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
        this.indiceUtilizado = indiceUtilizado == null ? null : indiceUtilizado.trim().toUpperCase(Locale.ROOT);
    }
}