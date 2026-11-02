package com.duarte.imobiliaria.controllers.DTO;

import com.duarte.imobiliaria.formsValidation.groups.ValidacaoAvancada;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoBasica;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@GroupSequence({ValidacaoBasica.class, ValidacaoAvancada.class, RescisaoDTO.class})
public class RescisaoDTO {

    public static final int MOTIVO_MAX_SIZE = 50;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Size(max = MOTIVO_MAX_SIZE, message = "{rescisao.motivo.size}", groups = ValidacaoAvancada.class)
    private String motivo;

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
