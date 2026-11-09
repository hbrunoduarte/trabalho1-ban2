package com.duarte.imobiliaria.database.postgree;

import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.database.interfaceDAO.ContratoVendaDAO;
import com.duarte.imobiliaria.model.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("postgre")
public class ContratoVendaPostgre implements ContratoVendaDAO {

    private static final String SQL_ENDERECO_IMOVEL = "e.rua, e.numero, e.complemento, e.bairro, e.cidade, e.estado";
    private static final String BUSCA_ID_PAINEL = "SELECT c.*, cl.nome AS nome_cliente, " +
            "p.id_proprietario, p.tipo AS tipo_proprietario, COALESCE(p.nome, p.razao_social) AS nome_proprietario, " +
            "i.id_imovel, " + SQL_ENDERECO_IMOVEL + " " +
            "FROM Contrato_Venda c " +
            "JOIN Cliente cl ON c.id_cliente=cl.id_cliente " +
            "JOIN Proprietario p ON c.id_proprietario=p.id_proprietario " +
            "JOIN Imovel i ON c.id_imovel=i.id_imovel " +
            "JOIN Endereco e ON i.id_endereco=e.id_endereco " +
            "WHERE %s=? " +
            "ORDER BY data %s ";

    private final JdbcTemplate jdbcTemplate;

    public ContratoVendaPostgre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static void setAtributosGerais(ResultSet rs, ContratoVenda c) throws SQLException {
        c.setIdVenda(rs.getInt("id_venda"));
        c.setData(rs.getDate("data").toLocalDate());
        c.setValorVenda(rs.getBigDecimal("valor_venda"));
        c.setValorSinal(rs.getBigDecimal("valor_sinal"));
        c.setFormaPagamento(rs.getString("forma_pagamento"));
        c.setValorFinanciado(rs.getBigDecimal("valor_financiado"));
        c.setValorComissao(rs.getBigDecimal("valor_comissao"));
    }

    private final RowMapper<ContratoVenda> rowMapper = (rs, _) -> {
        ContratoVenda c = new ContratoVenda();
        setAtributosGerais(rs, c);
        c.setCliente(new Cliente(rs.getInt("id_cliente")));
        c.setProprietario(new Proprietario(rs.getInt("id_proprietario")));
        c.setImovel(new Imovel(rs.getInt("id_imovel")));
        return c;
    };

    private final RowMapper<ContratoVenda> rmPainel = (rs, _) -> {
        ContratoVenda c = new ContratoVenda();
        setAtributosGerais(rs, c);

        Proprietario p = new Proprietario();
            p.setIdProprietario(rs.getInt("id_proprietario"));
            p.setTipo(rs.getString("tipo_proprietario").charAt(0));
            if (p.isFisico())
                p.setNome(rs.getString("nome_proprietario"));
            else
                p.setRazaoSocial(rs.getString("nome_proprietario"));
        c.setProprietario(p);

        Imovel i = new Imovel();
            i.setIdImovel(rs.getInt("id_imovel"));

            Endereco endereco = new Endereco();
                endereco.setEstado(rs.getString("estado"));
                endereco.setCidade(rs.getString("cidade"));
                endereco.setBairro(rs.getString("bairro"));
                endereco.setNumero(rs.getInt("numero"));
                endereco.setRua(rs.getString("rua"));
                endereco.setComplemento(rs.getString("complemento"));
            i.setEndereco(endereco);
        c.setImovel(i);

        Cliente cl = new Cliente();
            cl.setIdCliente(rs.getInt("id_cliente"));
            cl.setNome(rs.getString("nome_cliente"));
        c.setCliente(cl);

        return c;
    };

    @Override
    public void salvar(ContratoVenda contrato) {
        if (contrato.getIdVenda() == null || contrato.getIdVenda() == 0) {
            String sql = "INSERT INTO Contrato_Venda (valor_venda, valor_sinal, forma_pagamento, valor_financiado, valor_comissao, id_cliente, id_proprietario, id_imovel, id_corretor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id_venda"});
                ps.setBigDecimal(1, contrato.getValorVenda());
                if (contrato.getValorSinal() != null)
                    ps.setBigDecimal(2, contrato.getValorSinal());
                else
                    ps.setNull(2, Types.NUMERIC);
                ps.setString(3, contrato.getFormaPagamento());
                if (contrato.getValorFinanciado() != null)
                    ps.setBigDecimal(4, contrato.getValorFinanciado());
                else
                    ps.setNull(4, Types.NUMERIC);
                ps.setBigDecimal(5, contrato.getValorComissao());
                ps.setInt(6, contrato.getCliente().getIdCliente());
                ps.setInt(7, contrato.getProprietario().getIdProprietario());
                ps.setInt(8, contrato.getImovel().getIdImovel());
                ps.setInt(9, contrato.getCorretor().getIdCorretor());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                contrato.setIdVenda(keyHolder.getKey().intValue());
            }
        } else {
            String sql = "UPDATE Contrato_Venda SET data=?, valor_venda=?, valor_sinal=?, forma_pagamento=?, valor_financiado=?, valor_comissao=?, id_cliente=?, id_proprietario=?, id_imovel=?, id_corretor=? WHERE id_venda=?";
            jdbcTemplate.update(sql,
                    Date.valueOf(contrato.getData()), contrato.getValorVenda(),
                    contrato.getValorSinal(), contrato.getFormaPagamento(),
                    contrato.getValorFinanciado(), contrato.getValorComissao(),
                    contrato.getCliente().getIdCliente(),
                    contrato.getProprietario().getIdProprietario(),
                    contrato.getImovel().getIdImovel(),
                    contrato.getCorretor().getIdCorretor(), contrato.getIdVenda());
        }
    }

    @Override
    public Optional<ContratoVenda> buscarPorId(int id) {
        String sql = "SELECT * FROM Contrato_Venda WHERE id_venda = ?";
        List<ContratoVenda> resultados = jdbcTemplate.query(sql, rowMapper, id);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public List<ContratoVenda> buscarPorId(int id, TipoUsuarioLogado t, boolean maisRecente) {
        if (t == null) return List.of();

        String sql;
        String ordem = maisRecente ? "DESC" : "ASC";
        if (t == TipoUsuarioLogado.CLIENTE)
            sql = String.format(BUSCA_ID_PAINEL, "c.id_cliente", ordem);
        else if (t == TipoUsuarioLogado.PROPRIETARIO)
            sql = String.format(BUSCA_ID_PAINEL, "c.id_proprietario", ordem);
        else
            sql = String.format(BUSCA_ID_PAINEL, "c.id_corretor", ordem);

        return jdbcTemplate.query(sql, rmPainel, id);
    }

    @Override
    public List<ContratoVenda> listarTodos() {
        return jdbcTemplate.query("SELECT * FROM Contrato_Venda", rowMapper);
    }

}
