package com.duarte.imobiliaria.database.postgree;

import com.duarte.imobiliaria.database.interfaceDAO.CorretorDAO;
import com.duarte.imobiliaria.model.Corretor;
import com.duarte.imobiliaria.model.VisitaAgendada;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("postgre")
public class CorretorPostgre implements CorretorDAO {

    private final JdbcTemplate jdbcTemplate;

    public CorretorPostgre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Corretor> rowMapper = (rs, rowNum) -> {
        Corretor c = new Corretor();
        c.setIdCorretor(rs.getInt("id_corretor"));
        c.setNome(rs.getString("nome"));
        c.setEmail(rs.getString("email"));
        c.setTelefone(rs.getString("telefone"));
        c.setCpf(rs.getString("cpf"));
        c.setPercentualComissao(rs.getFloat("percentual_comissao"));
        c.setDataAdmissao(rs.getDate("data_admissao").toLocalDate());
        c.setCreci(rs.getString("creci"));
        return c;
    };

    @Override
    public Optional<Corretor> proximoParaVisita(VisitaAgendada v) {

        String sql = "SELECT c.id_corretor " +
                        "FROM Corretor c " +
                        "LEFT JOIN Visita_Agendada v ON v.id_corretor = c.id_corretor " +
                        "WHERE NOT EXISTS ( " +
                        "SELECT 1 FROM Visita_Agendada ocupada " +
                        "WHERE ocupada.id_corretor = c.id_corretor AND ocupada.potencial_contrato " +
                        "AND (ocupada.data + ocupada.horario) > ?::timestamp - INTERVAL '1 hour' " +
                        "AND (ocupada.data + ocupada.horario) < ?::timestamp + INTERVAL '1 hour' " +
                        ") " +
                        "GROUP BY c.id_corretor " +
                        "ORDER BY COUNT(v.id_visita) ASC, c.id_corretor ASC " +
                        "LIMIT 1";

        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.of(v.getData(), v.getHorario()));

        List<Corretor> resultado = jdbcTemplate.query(sql, (rs, _) -> {
            Corretor c = new Corretor();
            c.setIdCorretor(rs.getInt("id_corretor"));
            return c;
        }, timestamp, timestamp);

        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.getFirst());
    }

    @Override
    public void salvar(Corretor corretor) {
        if (corretor.getIdCorretor() == null || corretor.getIdCorretor() == 0) {
            String sql = "INSERT INTO Corretor (nome, email, senha, telefone, cpf, percentual_comissao, data_admissao, creci) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id_corretor"});
                ps.setString(1, corretor.getNome());
                ps.setString(2, corretor.getEmail());
                ps.setString(3, corretor.getSenha());
                ps.setString(4, corretor.getTelefone());
                ps.setString(5, corretor.getCpf());
                ps.setDouble(6, corretor.getPercentualComissao());
                ps.setDate(7, Date.valueOf(corretor.getDataAdmissao()));
                ps.setString(8, corretor.getCreci());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                corretor.setIdCorretor(keyHolder.getKey().intValue());
            }
        } else {
            String sql = "UPDATE Corretor SET nome=?, email=?, senha=?, telefone=?, cpf=?, percentual_comissao=?, data_admissao=?, creci=? WHERE id_corretor=?";
            jdbcTemplate.update(sql, corretor.getNome(), corretor.getEmail(), corretor.getSenha(), corretor.getTelefone(), corretor.getCpf(), corretor.getPercentualComissao(), Date.valueOf(corretor.getDataAdmissao()), corretor.getCreci(), corretor.getIdCorretor());
        }
    }

    @Override
    public Optional<Corretor> buscarPorId(int id) {
        String sql = "SELECT * FROM Corretor WHERE id_corretor = ?";
        List<Corretor> resultados = jdbcTemplate.query(sql, rowMapper, id);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public Optional<Corretor> buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM Corretor WHERE cpf = ?";
        List<Corretor> resultados = jdbcTemplate.query(sql, rowMapper, cpf);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public Optional<Corretor> buscarPorCreci(String creci) {
        String sql = "SELECT * FROM Corretor WHERE creci = ?";
        List<Corretor> resultados = jdbcTemplate.query(sql, rowMapper, creci);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public List<Corretor> listarTodos() {
        return jdbcTemplate.query("SELECT * FROM Corretor", rowMapper);
    }

    @Override
    public boolean excluir(int id) {
        return jdbcTemplate.update("DELETE FROM Corretor WHERE id_corretor = ?", id) > 0;
    }

}
