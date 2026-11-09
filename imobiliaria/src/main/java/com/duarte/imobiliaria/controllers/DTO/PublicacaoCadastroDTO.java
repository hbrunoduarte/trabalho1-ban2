package com.duarte.imobiliaria.controllers.DTO;

import com.duarte.imobiliaria.formsValidation.groups.ValidacaoAvancada;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoBasica;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoFormato;
import jakarta.validation.GroupSequence;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

@GroupSequence({ValidacaoBasica.class, ValidacaoFormato.class, ValidacaoAvancada.class, PublicacaoCadastroDTO.class})
public record PublicacaoCadastroDTO(

        @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
        @Positive(message = "{cadastro.publicacao.imovel}", groups = ValidacaoAvancada.class)
        Integer idImovel,

        @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
        @Size(max = 50, message = "{cadastro.publicacao.titulo}", groups = ValidacaoAvancada.class)
        String titulo,

        @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
        @Size(min = 1, max = 1, message = "{cadastro.publicacao.tipo}", groups = ValidacaoAvancada.class)
        String tipoPublicacao,

        @NotNull(message = "Indique qual foto será a capa", groups = ValidacaoBasica.class)
        @PositiveOrZero(message = "Índice não pode ser negativo", groups = ValidacaoAvancada.class)
        Integer indiceCapa,

        @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
        @NotEmpty(message = "Informe pelo menos uma foto para a publicação", groups = ValidacaoFormato.class)
        List<@Valid FotoDTO> fotos
) {
}
