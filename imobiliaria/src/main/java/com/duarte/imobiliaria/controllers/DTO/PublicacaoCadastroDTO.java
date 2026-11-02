package com.duarte.imobiliaria.controllers.DTO;

import java.util.List;

public record PublicacaoCadastroDTO(
        Integer idImovel,
        String titulo,
        String tipoPublicacao,
        Integer indiceCapa,
        List<FotoDTO> fotos
) {
}
