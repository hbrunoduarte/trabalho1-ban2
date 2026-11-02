package com.duarte.imobiliaria.controllers.DTO;

import java.math.BigDecimal;

public record ImovelPainelDTO(
        Integer idImovel,
        String tipoImovel,
        String endereco,
        Float areaTotal,
        Integer qtdDormitorios,
        Integer qtdBanheiros,
        Integer qtdVagas,
        Integer anoConstrucao,
        String descricao,
        BigDecimal valor,
        String status,
        Integer idPublicacaoAtiva
) {
}
