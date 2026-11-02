package com.duarte.imobiliaria.database.postgree;

import com.duarte.imobiliaria.database.interfaceDAO.FotoDAO;
import com.duarte.imobiliaria.model.Foto;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Profile("postgre")
public class FotoPostgre implements FotoDAO {

    private final JdbcTemplate jdbcTemplate;

    public FotoPostgre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvar(List<Foto> fotos) {
        if (fotos == null || fotos.isEmpty()) return;

        StringBuilder sql = new StringBuilder("INSERT INTO Foto (tipo, imagem, id_imovel) VALUES ");

        for (int i = 0; i < fotos.size(); i++) {
            sql.append("(?,?,?)");
            if (i < fotos.size() - 1) {
                sql.append(",");
            }
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql.toString(), new String[] {"id_foto"});

            int index = 1;
            for (Foto f : fotos) {
                ps.setString(index++, f.getTipo());
                ps.setBytes(index++, f.getImagem());
                ps.setInt(index++, f.getIdImovel());
            }

            return ps;
        }, keyHolder);

        List<Map<String, Object>> keys = keyHolder.getKeyList();
        for (int i = 0; i < fotos.size(); i++) {
            Map<String, Object> keyMap = keys.get(i);

            Number id = (Number) keyMap.values().iterator().next();

            if (id != null) {
                fotos.get(i).setIdFoto(id.intValue());
            }
        }
    }

    @Override
    public void salvar(Foto foto) {

        String sql = "INSERT INTO Foto (tipo, imagem, id_imovel) VALUES (?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id_foto"});
            ps.setString(1, foto.getTipo());
            ps.setBytes(2, foto.getImagem());
            ps.setInt(3, foto.getIdImovel());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            foto.setIdFoto(keyHolder.getKey().intValue());
        }
    }

    @Override
    public Optional<Foto> buscarPorId(int idFoto) {
        String sql = "SELECT * FROM Foto WHERE id_foto = ?";

        List<Foto> resultados = jdbcTemplate.query(sql, (rs, _) -> {
            Foto foto = new Foto();
            foto.setIdFoto(rs.getInt("id_foto"));
            foto.setTipo(rs.getString("tipo"));
            foto.setImagem(rs.getBytes("imagem"));
            foto.setIdImovel(rs.getInt("id_imovel"));
            return foto;
        }, idFoto);

        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public List<Integer> buscarIdsPorImovel(int idImovel) {
        String sql = "SELECT id_foto FROM Foto WHERE id_imovel = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, idImovel);
    }
}
