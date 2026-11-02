package com.duarte.imobiliaria.database.postgree;

import com.duarte.imobiliaria.database.interfaceDAO.ReajusteDAO;
import com.duarte.imobiliaria.model.ContratoLocacao;
import com.duarte.imobiliaria.model.ReajusteAluguel;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Profile("postgre")
public class ReajustePostgre implements ReajusteDAO {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<ReajusteAluguel> rowMapper = (rs, _) -> {
        ReajusteAluguel r = new ReajusteAluguel();
        r.setIdReajuste(rs.getInt("id_reajuste"));
        r.setData(rs.getDate("data").toLocalDate());
        r.setIndiceUtilizado(rs.getString("indice_utilizado"));
        r.setNovoValor(rs.getBigDecimal("novo_valor"));
        r.setContratoLocacao(new ContratoLocacao(rs.getInt("id_locacao")));
        return r;
    };

    public ReajustePostgre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvar(ReajusteAluguel r) {
        String sql = "INSERT INTO Reajuste_Aluguel (novo_valor, indice_utilizado, id_locacao) VALUES (?,?,?)";
        jdbcTemplate.update(sql, r.getNovoValor(), r.getIndiceUtilizado(), r.getContratoLocacao().getIdLocacao());
    }

    @Override
    public List<ReajusteAluguel> buscarPorIdLocacao(int idLocacao) {
        String sql = "SELECT * FROM Reajuste_Aluguel WHERE id_locacao=? ";
        return jdbcTemplate.query(sql, rowMapper, idLocacao);
    }
}
