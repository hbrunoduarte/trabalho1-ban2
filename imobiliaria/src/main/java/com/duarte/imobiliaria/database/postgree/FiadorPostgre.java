package com.duarte.imobiliaria.database.postgree;

import com.duarte.imobiliaria.database.interfaceDAO.FiadorDAO;
import com.duarte.imobiliaria.model.ContratoLocacao;
import com.duarte.imobiliaria.model.Endereco;
import com.duarte.imobiliaria.model.Fiador;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("postgre")
public class FiadorPostgre implements FiadorDAO {

    private final JdbcTemplate jdbcTemplate;

    public FiadorPostgre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Fiador> rowMapper = (rs, _) -> {
        Fiador f = new Fiador();
        f.setIdFiador(rs.getInt("id_fiador"));
        f.setCpf(rs.getString("cpf"));
        f.setNome(rs.getString("nome"));
        f.setComprovanteRenda(rs.getBytes("comprovante_renda"));
        f.setEndereco(new Endereco(rs.getInt("id_endereco")));
        return f;
    };

    public static RowMapper<Fiador> getRowMapper() {
        return rowMapper;
    }

    @Override
    public void salvar(Fiador f) {
        if (f.getIdFiador() == null || f.getIdFiador() == 0) {
            String sql = "INSERT INTO Fiador (cpf, nome, comprovante_renda, id_endereco) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id_fiador"});
                ps.setString(1, f.getCpf());
                ps.setString(2, f.getNome());
                ps.setBytes(3, f.getComprovanteRenda());
                ps.setInt(4, f.getEndereco().getIdEndereco());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                f.setIdFiador(keyHolder.getKey().intValue());
            }
        } else {
            String sql = "UPDATE Fiador SET cpf=?, nome=?, comprovante_renda=?, id_endereco=? WHERE id_fiador=?";
            jdbcTemplate.update(sql, f.getCpf(), f.getNome(), f.getComprovanteRenda(), f.getEndereco().getIdEndereco(), f.getIdFiador());
        }
    }

    @Override
    public Optional<Fiador> buscarPorId(int id) {
        String sql = "SELECT * FROM Fiador WHERE id_fiador = ?";
        List<Fiador> resultados = jdbcTemplate.query(sql, rowMapper, id);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public Optional<Fiador> buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM Fiador WHERE cpf=?";
        List<Fiador> resultados = jdbcTemplate.query(sql, rowMapper, cpf);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public boolean excluir(int id) {
        return jdbcTemplate.update("DELETE FROM Fiador WHERE id_fiador = ?", id) > 0;
    }
}