package com.duarte.imobiliaria.database.postgree;

import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.database.interfaceDAO.ContratoLocacaoDAO;
import com.duarte.imobiliaria.model.*;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("postgre")
public class ContratoLocacaoPostgre implements ContratoLocacaoDAO {

    private static final String SQL_ENDERECO_IMOVEL = "e.rua, e.numero, e.complemento, e.bairro, e.cidade, e.estado";
    private static final String BUSCA_ID_PAINEL = "SELECT c.*, cl.nome AS nome_cliente, " +
            "i.id_proprietario, p.tipo AS tipo_proprietario, COALESCE(p.nome, p.razao_social) AS nome_proprietario, " +
            "i.id_imovel, " + SQL_ENDERECO_IMOVEL + " " +
            "FROM Contrato_Locacao c " +
            "JOIN Cliente cl ON c.id_cliente=cl.id_cliente " +
            "JOIN Imovel i ON c.id_imovel=i.id_imovel " +
            "JOIN Proprietario p ON i.id_proprietario=p.id_proprietario " +
            "JOIN Endereco e ON i.id_endereco=e.id_endereco " +
            "WHERE %s=? " +
            "ORDER BY data_inicio %s ";

    private final JdbcTemplate jdbcTemplate;

    public ContratoLocacaoPostgre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static void setAtributosGerais(ResultSet rs, ContratoLocacao c) throws SQLException {
        c.setIdLocacao(rs.getInt("id_locacao"));
        c.setPrazoMes(rs.getInt("prazo_em_meses"));
        c.setGarantia(rs.getString("garantia"));
        c.setValorCaucao(rs.getBigDecimal("valor_caucao"));
        c.setValorComissao(rs.getBigDecimal("valor_comissao"));
        c.setStatusContrato(rs.getString("status_contrato"));
        c.setDiaVencimento(rs.getInt("dia_vencimento"));
        c.setDataInicio(rs.getDate("data_inicio").toLocalDate());
        c.setValorAluguel(rs.getBigDecimal("valor_aluguel"));
        if (rs.getDate("data_rescisao") != null) c.setDataRescisao(rs.getDate("data_rescisao").toLocalDate());
        c.setMulta(rs.getBigDecimal("multa"));
        c.setMotivo(rs.getString("motivo"));
    }

    private static final RowMapper<ContratoLocacao> rowMapper = (rs, _) -> {
        ContratoLocacao c = new ContratoLocacao();
        setAtributosGerais(rs, c);
        c.setImovel(new Imovel(rs.getInt("id_imovel")));
        c.setCliente(new Cliente(rs.getInt("id_cliente")));
        c.setCorretor(new Corretor(rs.getInt("id_corretor")));
        return c;
    };

    private static final RowMapper<ContratoLocacao> rmPainel = (rs, _) -> {

        ContratoLocacao c = new ContratoLocacao();
        setAtributosGerais(rs, c);

        Imovel i = new Imovel();
            i.setIdImovel(rs.getInt("id_imovel"));

            Proprietario p = new Proprietario();
                p.setIdProprietario(rs.getInt("id_proprietario"));
                p.setTipo(rs.getString("tipo_proprietario").charAt(0));
                if (p.isFisico())
                    p.setNome(rs.getString("nome_proprietario"));
                else
                    p.setRazaoSocial(rs.getString("nome_proprietario"));
            i.setProprietario(p);

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

    public static RowMapper<ContratoLocacao> getRowMapper() {
        return rowMapper;
    }

    @Override
    public void salvar(ContratoLocacao c) {
        if (c.getIdLocacao() == null || c.getIdLocacao() == 0) {
            String sql = "INSERT INTO Contrato_Locacao (prazo_em_meses, garantia, valor_caucao, valor_comissao, dia_vencimento, valor_aluguel, id_imovel, id_cliente, id_corretor, id_fiador) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id_locacao"});
                ps.setInt(1, c.getPrazoMes());
                ps.setString(2, c.getGarantia());
                if (c.getValorCaucao() != null)
                    ps.setBigDecimal(3, c.getValorCaucao());
                else
                    ps.setNull(3, Types.NUMERIC);
                ps.setBigDecimal(4, c.getValorComissao());
                ps.setInt(5, c.getDiaVencimento());
                ps.setBigDecimal(6, c.getValorAluguel());
                ps.setInt(7, c.getImovel().getIdImovel());
                ps.setInt(8, c.getCliente().getIdCliente());
                ps.setInt(9, c.getCorretor().getIdCorretor());
                Integer idFiador = c.getFiador() == null ? null : c.getFiador().getIdFiador();
                if (idFiador != null)
                    ps.setInt(10, idFiador);
                else
                    ps.setNull(10, Types.INTEGER);
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                c.setIdLocacao(keyHolder.getKey().intValue());
            }
        } else {
            String sql = "UPDATE Contrato_Locacao SET prazo_em_meses=?, garantia=?, valor_caucao=?, valor_comissao=?, status_contrato=?, dia_vencimento=?, data_inicio=?, valor_aluguel=?, data_rescisao=?, multa=?, motivo=?, id_imovel=?, id_cliente=? WHERE id_locacao=?";
            Date dataRescisao = c.isRescindido() ? Date.valueOf(c.getDataRescisao()) : null;
            BigDecimal multa = c.isRescindido() ? c.getMulta() : null;
            String motivo = c.isRescindido() ? c.getMotivo() : null;
            jdbcTemplate.update(sql,
                    c.getPrazoMes(), c.getGarantia(), c.getValorCaucao(),
                    c.getValorComissao(), c.getStatusContrato(), c.getDiaVencimento(),
                    Date.valueOf(c.getDataInicio()), c.getValorAluguel(),
                    dataRescisao, multa, motivo, c.getImovel().getIdImovel(),
                    c.getCliente().getIdCliente(), c.getIdLocacao());
        }
    }

    @Override
    public Optional<ContratoLocacao> buscarPorId(int id) {
        String sql = "SELECT * FROM Contrato_Locacao WHERE id_locacao = ?";
        List<ContratoLocacao> resultados = jdbcTemplate.query(sql, rowMapper, id);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public List<ContratoLocacao> buscarPorId(int id, TipoUsuarioLogado t, boolean maisRecente) {
        if (t == null) return List.of();

        String sql;
        String ordem = maisRecente ? "DESC" : "ASC";
        if (t == TipoUsuarioLogado.CLIENTE)
            sql = String.format(BUSCA_ID_PAINEL, "c.id_cliente", ordem);
        else if (t == TipoUsuarioLogado.PROPRIETARIO)
            sql = String.format(BUSCA_ID_PAINEL, "i.id_proprietario", ordem);
        else
            sql = String.format(BUSCA_ID_PAINEL, "c.id_corretor", ordem);

        return jdbcTemplate.query(sql, rmPainel, id);
    }

    @Override
    public boolean rescindir(int idLocacao, LocalDate dataRescisao, BigDecimal multa, String motivo) {

        String sql = "UPDATE Contrato_Locacao SET status_contrato='ENCERRADO', data_rescisao=?, multa=?, motivo=? " +
                "WHERE id_locacao=? AND status_contrato='ATIVO'";
        return jdbcTemplate.update(sql, Date.valueOf(dataRescisao), multa, motivo, idLocacao) == 1;
    }

    @Override
    public List<ContratoLocacao> listarTodos() {
        return jdbcTemplate.query("SELECT * FROM Contrato_Locacao", rowMapper);
    }
}