package com.duarte.imobiliaria.database.postgree;

import com.duarte.imobiliaria.database.interfaceDAO.TipoImovelDAO;
import com.duarte.imobiliaria.model.TipoImovel;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("postgre")
public class TipoImovelPostgre implements TipoImovelDAO {

    private final JdbcTemplate jdbcTemplate;

    public TipoImovelPostgre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<TipoImovel> listarTodos() {
        String sql = "SELECT * FROM TipoImovel";
        return jdbcTemplate.query(sql, (rs,_) -> new TipoImovel(rs.getInt("id_tipo_imovel"), rs.getString("descricao")));
    }
}
