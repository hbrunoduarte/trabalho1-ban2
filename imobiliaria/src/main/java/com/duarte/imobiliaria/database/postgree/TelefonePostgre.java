package com.duarte.imobiliaria.database.postgree;

import com.duarte.imobiliaria.database.interfaceDAO.TelefoneDAO;
import com.duarte.imobiliaria.model.*;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@Profile("postgre")
public class TelefonePostgre implements TelefoneDAO {

    private final JdbcTemplate jdbcTemplate;

    public TelefonePostgre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvarDoCliente(List<TelefoneCliente> telefones) {
        if (telefones == null || telefones.isEmpty()) return;

        for (TelefoneCliente t : telefones) {

            if (t.getIdTelefone() == null) {

                String sql = "INSERT INTO TelefoneCliente (telefone, id_cliente) VALUES (?, ?) ";

                KeyHolder keyHolder = new GeneratedKeyHolder();

                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id_telefone"});
                    ps.setString(1, t.getTelefone());
                    ps.setInt(2, t.getCliente().getIdCliente());
                    return ps;
                }, keyHolder);

                if (keyHolder.getKey() != null) {
                    t.setIdTelefone(keyHolder.getKey().intValue());
                }

            } else {
                String sql = "UPDATE TelefoneCliente SET telefone=? WHERE id_telefone=?";
                jdbcTemplate.update(sql, t.getTelefone(), t.getIdTelefone());
            }
        }
    }

    @Override
    public void salvarDoProprietario(List<TelefoneProprietario> telefones) {
        if (telefones == null || telefones.isEmpty()) return;

        for (TelefoneProprietario t : telefones) {

            if (t.getIdTelefone() == null) {

                String sql = "INSERT INTO TelefoneProprietario (telefone, id_proprietario) VALUES (?, ?) ";

                KeyHolder keyHolder = new GeneratedKeyHolder();

                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id_telefone"});
                    ps.setString(1, t.getTelefone());
                    ps.setInt(2, t.getProprietario().getIdProprietario());
                    return ps;
                }, keyHolder);

                if (keyHolder.getKey() != null) {
                    t.setIdTelefone(keyHolder.getKey().intValue());
                }

            } else {
                String sql = "UPDATE TelefoneProprietario SET telefone=? WHERE id_telefone=?";
                jdbcTemplate.update(sql, t.getTelefone(), t.getIdTelefone());
            }
        }
    }

    @Override
    public void salvarDoFiador(List<TelefoneFiador> telefones) {
        if (telefones == null || telefones.isEmpty()) return;

        for (TelefoneFiador t : telefones) {

            if (t.getIdTelefone() == null) {

                String sql = "INSERT INTO TelefoneFiador (telefone, id_fiador) VALUES (?, ?) ";

                KeyHolder keyHolder = new GeneratedKeyHolder();

                jdbcTemplate.update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id_telefone"});
                    ps.setString(1, t.getTelefone());
                    ps.setInt(2, t.getFiador().getIdFiador());
                    return ps;
                }, keyHolder);

                if (keyHolder.getKey() != null) {
                    t.setIdTelefone(keyHolder.getKey().intValue());
                }

            } else {
                String sql = "UPDATE TelefoneFiador SET telefone=? WHERE id_telefone=?";
                jdbcTemplate.update(sql, t.getTelefone(), t.getIdTelefone());
            }
        }
    }

    @Override
    public boolean excluirDoCliente(int idTelefone) {
        return deletar(idTelefone, "TelefoneCliente");
    }

    @Override
    public boolean excluirDoProprietario(int idTelefone) {
        return deletar(idTelefone, "TelefoneProprietario");
    }

    @Override
    public boolean excluirDoFiador(int idTelefone) {
        return deletar(idTelefone, "TelefoneFiador");
    }

    @Override
    public List<TelefoneCliente> listarDoCliente(int idCliente) {
        String sql = "SELECT id_telefone, telefone, id_cliente FROM TelefoneCliente WHERE id_cliente = ?";

        return jdbcTemplate.query(sql, (rs, _) -> {
            TelefoneCliente t = new TelefoneCliente();
            t.setIdTelefone(rs.getInt("id_telefone"));
            t.setTelefone(rs.getString("telefone"));
            t.setCliente(new Cliente(rs.getInt("id_cliente")));
            return t;
        }, idCliente);
    }

    @Override
    public List<TelefoneProprietario> listarDoProprietario(int idProprietario) {
        String sql = "SELECT id_telefone, telefone, id_proprietario FROM TelefoneProprietario WHERE id_proprietario = ?";

        return jdbcTemplate.query(sql, (rs, _) -> {
            TelefoneProprietario t = new TelefoneProprietario();
            t.setIdTelefone(rs.getInt("id_telefone"));
            t.setTelefone(rs.getString("telefone"));
            t.setProprietario(new Proprietario(rs.getInt("id_proprietario")));
            return t;
        }, idProprietario);
    }

    @Override
    public List<TelefoneProprietario> buscarPorIdImovel(int idImovel) {
        String sql = "SELECT tf.* " +
                        "FROM TelefoneProprietario tf JOIN Imovel i ON tf.id_proprietario=i.id_proprietario " +
                        "WHERE i.id_imovel=?";

        return jdbcTemplate.query(sql, (rs, _) -> {
            TelefoneProprietario t = new TelefoneProprietario();
            t.setIdTelefone(rs.getInt("id_telefone"));
            t.setTelefone(rs.getString("telefone"));
            t.setProprietario(new Proprietario(rs.getInt("id_proprietario")));
            return t;
        }, idImovel);
    }

    @Override
    public List<TelefoneFiador> listarDoFiador(int idFiador) {
        String sql = "SELECT id_telefone, telefone, id_fiador FROM TelefoneFiador WHERE id_fiador = ?";

        return jdbcTemplate.query(sql, (rs, _) -> {
            TelefoneFiador t = new TelefoneFiador();
            t.setIdTelefone(rs.getInt("id_telefone"));
            t.setTelefone(rs.getString("telefone"));
            t.setFiador(new Fiador(rs.getInt("id_fiador")));
            return t;
        }, idFiador);
    }

    private boolean deletar(int idTelefone, String tabela) {
        return jdbcTemplate.update("DELETE FROM " + tabela + " WHERE id_telefone=?", idTelefone) > 0;
    }
}
