package com.duarte.imobiliaria.database.postgree;

import com.duarte.imobiliaria.model.Endereco;
import com.duarte.imobiliaria.model.Imovel;
import com.duarte.imobiliaria.model.Proprietario;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.duarte.imobiliaria.database.interfaceDAO.ImovelDAO;
import org.springframework.web.client.HttpClientErrorException;

@Repository
@Profile("postgre")
public class ImovelPostgre implements ImovelDAO {

    private final JdbcTemplate jdbcTemplate;

    public ImovelPostgre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Imovel> rowMapper = (rs, rowNum) -> {
        Imovel i = new Imovel();
        i.setIdImovel(rs.getInt("id_imovel"));
        i.setTipo(rs.getInt("id_tipo_imovel"));
        i.setEndereco(new Endereco(rs.getInt("id_endereco")));
        i.setProprietario(new Proprietario(rs.getInt("id_proprietario")));
        i.setAreaTotal(rs.getFloat("area_total"));
        i.setQtdDormitorios(rs.getInt("qtd_dormitorios"));
        i.setQtdBanheiros(rs.getInt("qtd_banheiros"));
        i.setQtdVagas(rs.getInt("qtd_vagas"));
        i.setAnoConstrucao(rs.getInt("ano_construcao"));
        i.setDescricao(rs.getString("descricao"));
        i.setValor(rs.getBigDecimal("valor"));
        i.setStatus(rs.getString("status"));
        return i;
    };

    @Override
    public void salvar(Imovel imovel) {
        if (imovel.getIdImovel() == null) {
            String sql = "INSERT INTO Imovel (id_tipo_imovel, id_endereco, id_proprietario, area_total, qtd_dormitorios, qtd_banheiros, qtd_vagas, ano_construcao, descricao, valor, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id_imovel"});
                ps.setInt(1, imovel.getTipo());
                ps.setInt(2, imovel.getEndereco().getIdEndereco());
                ps.setInt(3, imovel.getProprietario().getIdProprietario());
                ps.setFloat(4, imovel.getAreaTotal());
                ps.setInt(5, imovel.getQtdDormitorios());
                ps.setInt(6, imovel.getQtdBanheiros());
                ps.setInt(7, imovel.getQtdVagas());
                ps.setInt(8, imovel.getAnoConstrucao());
                ps.setString(9, imovel.getDescricao());
                ps.setBigDecimal(10, imovel.getValor());
                ps.setString(11, imovel.getStatus());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                imovel.setIdImovel(keyHolder.getKey().intValue());
            }
        } else {
            String sql = "UPDATE Imovel SET id_tipo_imovel=?, id_endereco=?, id_proprietario=?, area_total=?, qtd_dormitorios=?, qtd_banheiros=?, qtd_vagas=?, ano_construcao=?, descricao=?, valor=?, status=? WHERE id_imovel=?";
            jdbcTemplate.update(sql, imovel.getTipo(), imovel.getEndereco().getIdEndereco(), imovel.getProprietario().getIdProprietario(), imovel.getAreaTotal(), imovel.getQtdDormitorios(), imovel.getQtdBanheiros(), imovel.getQtdVagas(), imovel.getAnoConstrucao(), imovel.getDescricao(), imovel.getValor(), imovel.getStatus(), imovel.getIdImovel());
        }
    }

    @Override
    public Optional<Imovel> buscarPorId(int id) {
        String sql = "SELECT * FROM Imovel WHERE id_imovel = ?";
        List<Imovel> resultados = jdbcTemplate.query(sql, rowMapper, id);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public Optional<Imovel> buscarPorEndereco(int cep, int numero) {
        String sql = "SELECT i.* FROM Imovel i JOIN Endereco e USING (id_endereco) WHERE e.cep=? AND e.numero=?";
        String cepFormatado = String.format("%08d", cep);
        List<Imovel> resultados = jdbcTemplate.query(sql, rowMapper, cepFormatado, numero);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public List<Imovel> buscarPorProprietario(int idProprietario) {
        String sql = "SELECT * FROM Imovel WHERE id_proprietario=?";
        return jdbcTemplate.query(sql, rowMapper, idProprietario);
    }

    @Override
    public Optional<List<Imovel>> buscarPorTipo(int idTipoImovel) {
        String sql = "SELECT * FROM Imovel WHERE id_tipo_imovel=?";
        List<Imovel> resultados = jdbcTemplate.query(sql, rowMapper, idTipoImovel);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados);
    }

    public Optional<Float> buscarMaiorPreco(String tipoPublicacao) throws SQLException {
        String sql = "SELECT MAX(valor) as maior_valor FROM Imovel ";

        String statusImovel = null;
        if (tipoPublicacao != null && !tipoPublicacao.isBlank()) {
            if (tipoPublicacao.equals("V"))
                statusImovel = STATUS_VENDA;
            else if (tipoPublicacao.equals("L"))
                statusImovel = STATUS_LOCACAO;
            else throw new SQLException();

            sql += "WHERE status=?";
        }

        List<Float> resultado = statusImovel == null ? jdbcTemplate.query(sql, (rs,_) -> rs.getFloat("maior_valor"))
                                                     : jdbcTemplate.query(sql, (rs,_) -> rs.getFloat("maior_valor"), statusImovel);
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.getFirst());
    }

    @Override
    public List<Imovel> listarTodos() {
        return jdbcTemplate.query("SELECT * FROM Imovel", rowMapper);
    }

    @Override
    public boolean excluir(int id) {
        return jdbcTemplate.update("DELETE FROM Imovel WHERE id_imovel = ?", id) > 0;
    }

}
