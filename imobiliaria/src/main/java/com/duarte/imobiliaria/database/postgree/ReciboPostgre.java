package com.duarte.imobiliaria.database.postgree;

import com.duarte.imobiliaria.controllers.DTO.FiltroReciboDTO;
import com.duarte.imobiliaria.database.interfaceDAO.ReciboDAO;
import com.duarte.imobiliaria.model.ContratoLocacao;
import com.duarte.imobiliaria.model.ReciboAluguel;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("postgre")
public class ReciboPostgre implements ReciboDAO {

    private static final String CLAUSULA_ORDENACAO = "ORDER BY ano ASC, mes ASC";

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<ReciboAluguel> rowMapper = (rs, _) -> {
        ReciboAluguel r = new ReciboAluguel();
        r.setIdRecibo(rs.getInt("id_recibo"));
        r.setDesconto(rs.getBigDecimal("desconto"));
        r.setAcrescimo(rs.getBigDecimal("acrescimo"));
        r.setValorLiquido(rs.getBigDecimal("valor_liquido"));
        r.setFormaPagamento(rs.getString("forma_pagamento"));
        r.setDataVencimento(rs.getDate("data_vencimento").toLocalDate());
        Date dataPagamento = rs.getDate("data_pagamento");
        r.setDataPagamento(dataPagamento == null ? null : dataPagamento.toLocalDate());
        r.setValorAluguel(rs.getBigDecimal("valor_aluguel"));
        r.setMes(rs.getInt("mes"));
        r.setAno(rs.getInt("ano"));
        r.setContratoLocacao(new ContratoLocacao(rs.getInt("id_locacao")));
        return r;
    };

    public ReciboPostgre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ReciboAluguel> filtrar(FiltroReciboDTO filtro) {

        String sql = "SELECT * FROM Recibo_Aluguel WHERE id_locacao=? ";
        List<Object> params = new ArrayList<>();
        params.add(filtro.getIdLocacao());

        if (filtro.getAno() != null) {
            sql += "AND ano=? ";
            params.add(Integer.valueOf(filtro.getAno()));
        }

        if (filtro.getSituacao() != null) {
            sql += filtro.isPago() ? "AND data_pagamento IS NOT NULL " : "AND data_pagamento IS NULL ";
        }

        sql += CLAUSULA_ORDENACAO;

        return jdbcTemplate.query(sql, rowMapper, params.toArray());
    }

    @Override
    public List<ReciboAluguel> buscarPorLocacao(int idLocacao) {
        String sql = "SELECT * FROM Recibo_Aluguel WHERE id_locacao=? " + CLAUSULA_ORDENACAO;
        return jdbcTemplate.query(sql, rowMapper, idLocacao);
    }

    @Override
    public int gerarRecibosPendentes() {
        Integer qtd = jdbcTemplate.queryForObject("SELECT fn_gerar_recibos()", Integer.class);
        return qtd == null ? 0 : qtd;
    }
}
