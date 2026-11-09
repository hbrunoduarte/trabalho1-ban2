package com.duarte.imobiliaria.database.postgree;

import com.duarte.imobiliaria.database.interfaceDAO.EnderecoDAO;
import com.duarte.imobiliaria.model.Endereco;
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
public class EnderecoPostgre implements EnderecoDAO {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Endereco> mapearEndereco = (rs, rowNum) -> {
        Endereco endereco = new Endereco();
        endereco.setIdEndereco(rs.getInt("id_endereco"));
        endereco.setCep(rs.getString("cep"));
        endereco.setEstado(rs.getString("estado"));
        endereco.setCidade(rs.getString("cidade"));
        endereco.setBairro(rs.getString("bairro"));
        endereco.setNumero(rs.getInt("numero"));
        endereco.setRua(rs.getString("rua"));
        endereco.setComplemento(rs.getString("complemento"));
        return endereco;
    };

    private final RowMapper<Endereco> mapearCEP = (rs, rowNum) -> {
        Endereco endereco = new Endereco();
        endereco.setCep(rs.getString("cep"));
        endereco.setEstado(rs.getString("estado"));
        endereco.setCidade(rs.getString("cidade"));
        endereco.setBairro(rs.getString("bairro"));
        endereco.setRua(rs.getString("rua"));
        return endereco;
    };

    public EnderecoPostgre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void inserir(Endereco endereco) {
        String sql = "INSERT INTO Endereco (cep, estado, cidade, bairro, numero, rua, complemento) VALUES (?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id_endereco"});
            ps.setString(1, endereco.getCep());
            ps.setString(2, endereco.getEstado());
            ps.setString(3, endereco.getCidade());
            ps.setString(4, endereco.getBairro());
            ps.setInt(5, endereco.getNumero());
            ps.setString(6, endereco.getRua());
            ps.setString(7, endereco.getComplemento());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            endereco.setIdEndereco(keyHolder.getKey().intValue());
        }
    }

    @Override
    public void salvar(Endereco endereco) {

        if (endereco.getIdEndereco() == null) {
            inserir(endereco);
            return;
        }

        String sql = "UPDATE Endereco SET cep=?, estado=?, cidade=?, bairro=?, numero=?, rua=?, complemento=? WHERE id_endereco=?";

        jdbcTemplate.update(sql,
                endereco.getCep(),
                endereco.getEstado(),
                endereco.getCidade(),
                endereco.getBairro(),
                endereco.getNumero(),
                endereco.getRua(),
                endereco.getComplemento(),
                endereco.getIdEndereco()
        );
    }

    @Override
    public Optional<Endereco> buscarPorId(int id) {
        String sql = "SELECT * FROM Endereco WHERE id_endereco=?";
        List<Endereco> resultado = jdbcTemplate.query(sql, mapearEndereco, id);
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.getFirst());
    }

    @Override
    public Optional<Endereco> buscarEndereco(int cep, int numero) {
        String sql = "SELECT * FROM Endereco WHERE cep = ? AND numero = ?";

        String cepFormatado = String.format("%08d", cep);

        List<Endereco> resultados = jdbcTemplate.query(sql, mapearEndereco, cepFormatado, numero);

        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public Optional<Endereco> buscarDadosCep(int cep) {
        String sql = "SELECT cep, estado, cidade, bairro, rua FROM Endereco WHERE cep = ? LIMIT 1";

        String cepFormatado = String.format("%08d", cep);

        List<Endereco> resultados = jdbcTemplate.query(sql, mapearCEP, cepFormatado);

        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public void remover(int idEndereco) {
        jdbcTemplate.update("DELETE FROM Endereco WHERE id_endereco=?", idEndereco);
    }
}