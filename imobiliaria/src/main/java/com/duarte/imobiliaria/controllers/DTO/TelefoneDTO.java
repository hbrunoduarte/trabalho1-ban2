package com.duarte.imobiliaria.controllers.DTO;

import com.duarte.imobiliaria.formsValidation.groups.ValidacaoAvancada;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoBasica;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoFormato;
import com.duarte.imobiliaria.formsValidation.util.ValidacaoConstants;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

@GroupSequence({ValidacaoBasica.class, ValidacaoFormato.class, ValidacaoAvancada.class, TelefoneDTO.class})
public class TelefoneDTO {

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Pattern(regexp = ValidacaoConstants.TELEFONE_PATTERN, message = "{cadastro.telefone.formato}", groups = ValidacaoFormato.class)
    private String telefone;

    private Integer idTelefone;

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Integer getIdTelefone() {
        return idTelefone;
    }

    public void setIdTelefone(Integer idTelefone) {
        this.idTelefone = idTelefone;
    }
}
