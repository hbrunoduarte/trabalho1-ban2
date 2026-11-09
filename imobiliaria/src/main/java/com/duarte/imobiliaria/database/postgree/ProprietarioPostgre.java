package com.duarte.imobiliaria.database.postgree;

import com.duarte.imobiliaria.database.interfaceDAO.ProprietarioDAO;
import com.duarte.imobiliaria.model.Endereco;
import com.duarte.imobiliaria.model.Proprietario;
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
public class ProprietarioPostgre implements ProprietarioDAO {

    private final JdbcTemplate jdbcTemplate;

    public ProprietarioPostgre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Proprietario> rowMapper = (rs, rowNum) -> {
        Proprietario p = new Proprietario();
        p.setIdProprietario(rs.getInt("id_proprietario"));
        p.setTipo(rs.getString("tipo").charAt(0));
        p.setCpf(rs.getString("cpf"));
        p.setCnpj(rs.getString("cnpj"));
        p.setEmail(rs.getString("email"));
        p.setSenha(rs.getString("senha"));
        p.setDadosBancarios(rs.getString("dados_bancario"));
        p.setNome(rs.getString("nome"));
        p.setRazaoSocial(rs.getString("razao_social"));
        p.setEndereco(new Endereco(rs.getInt("id_endereco")));
        return p;
    };

    @Override
    public void salvar(Proprietario p) {
        if (p.getIdProprietario() == null || p.getIdProprietario() == 0) {
            String sql = "INSERT INTO Proprietario (tipo, cpf, cnpj, email, senha, dados_bancario, nome, razao_social, id_endereco) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id_proprietario"});
                ps.setString(1, p.getTipo().toString());
                ps.setString(2, p.getCpf());
                ps.setString(3, p.getCnpj());
                ps.setString(4, p.getEmail());
                ps.setString(5, p.getSenha());
                ps.setString(6, p.getDadosBancarios());
                ps.setString(7, p.getNome());
                ps.setString(8, p.getRazaoSocial());
                ps.setInt(9, p.getEndereco().getIdEndereco());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                p.setIdProprietario(keyHolder.getKey().intValue());
            }
        } else {
            String sql = "UPDATE Proprietario SET tipo=?, cpf=?, cnpj=?, email=?, senha=?, dados_bancario=?, nome=?, razao_social=?, id_endereco=? WHERE id_proprietario=?";
            jdbcTemplate.update(sql, p.getTipo(), p.getCpf(), p.getCnpj(), p.getEmail(), p.getSenha(), p.getDadosBancarios(), p.getNome(), p.getRazaoSocial(), p.getEndereco().getIdEndereco(), p.getIdProprietario());
        }
    }

    @Override
    public void atualizarEndereco(Integer idProprietario, Integer idEndereco) {

        if (idProprietario == null || idEndereco == null) return;

        String sql = "UPDATE Proprietario SET id_endereco=? WHERE id_proprietario=?";
        jdbcTemplate.update(sql, idEndereco, idProprietario);
    }

    @Override
    public Optional<Proprietario> buscarPorId(int id) {
        String sql = "SELECT * FROM Proprietario WHERE id_proprietario = ?";
        List<Proprietario> resultados = jdbcTemplate.query(sql, rowMapper, id);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public Optional<Proprietario> buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM Proprietario WHERE cpf = ?";
        List<Proprietario> resultados = jdbcTemplate.query(sql, rowMapper, cpf);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public Optional<Proprietario> buscarPorCnpj(String cnpj) {
        String sql = "SELECT * FROM Proprietario WHERE cnpj = ?";
        List<Proprietario> resultados = jdbcTemplate.query(sql, rowMapper, cnpj);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public List<Proprietario> listarTodos() {
        return jdbcTemplate.query("SELECT * FROM Proprietario", rowMapper);
    }

    @Override
    public boolean excluir(int id) {
        return jdbcTemplate.update("DELETE FROM Proprietario WHERE id_proprietario = ?", id) > 0;
    }
}
