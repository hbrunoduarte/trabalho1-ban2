package com.duarte.imobiliaria.database.postgree;

import com.duarte.imobiliaria.database.interfaceDAO.ClienteDAO;
import com.duarte.imobiliaria.model.Cliente;
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
public class ClientePostgre implements ClienteDAO {

    private final JdbcTemplate jdbcTemplate;

    public ClientePostgre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Cliente> rowMapper = (rs, rowNum) -> {
        Cliente c = new Cliente();
        c.setIdCliente(rs.getInt("id_cliente"));
        c.setRendaMensal(rs.getDouble("renda_mensal"));
        c.setProfissao(rs.getString("profissao"));
        c.setTipoInteressado(rs.getInt("id_tipo_imovel"));
        c.setCpf(rs.getString("cpf"));
        c.setEmail(rs.getString("email"));
        c.setSenha(rs.getString("senha"));
        c.setNome(rs.getString("nome"));
        return c;
    };

    @Override
    public void salvar(Cliente cliente) {
        if (cliente.getIdCliente() == null || cliente.getIdCliente() == 0) {
            String sql = "INSERT INTO Cliente (renda_mensal, profissao, id_tipo_imovel, cpf, email, senha, nome) VALUES (?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id_cliente"});
                ps.setDouble(1, cliente.getRendaMensal());
                ps.setString(2, cliente.getProfissao());
                ps.setInt(3, cliente.getTipoInteressado());
                ps.setString(4, cliente.getCpf());
                ps.setString(5, cliente.getEmail());
                ps.setString(6, cliente.getSenha());
                ps.setString(7, cliente.getNome());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                cliente.setIdCliente(keyHolder.getKey().intValue());
            }
        } else {
            String sql = "UPDATE Cliente SET renda_mensal=?, profissao=?, id_tipo_imovel=?, cpf=?, email=?, senha=?, nome=? WHERE id_cliente=?";
            jdbcTemplate.update(sql, cliente.getRendaMensal(), cliente.getProfissao(), cliente.getTipoInteressado(), cliente.getCpf(), cliente.getEmail(), cliente.getSenha(), cliente.getNome(), cliente.getIdCliente());
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(int id) {
        String sql = "SELECT * FROM Cliente WHERE id_cliente = ?";
        List<Cliente> resultados = jdbcTemplate.query(sql, rowMapper, id);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM Cliente WHERE cpf = ?";
        List<Cliente> resultados = jdbcTemplate.query(sql, rowMapper, cpf);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public List<Cliente> listarTodos() {
        return jdbcTemplate.query("SELECT * FROM Cliente", rowMapper);
    }

    @Override
    public boolean excluir(int id) {
        return jdbcTemplate.update("DELETE FROM Cliente WHERE id_cliente = ?", id) > 0;
    }

}
