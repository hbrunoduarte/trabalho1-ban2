package com.duarte.imobiliaria.database.postgree;

import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.database.interfaceDAO.VisitaAgendadaDAO;
import com.duarte.imobiliaria.model.*;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("postgre")
public class VisitaAgendadaPostgre implements VisitaAgendadaDAO {

    private static final String SQL_ENDERECO_IMOVEL = "e.rua, e.numero, e.complemento, e.bairro, e.cidade, e.estado";

    private static final String BUSCA_POR_ID_PAINEL =
            "SELECT v.*, i.status, i.valor, " +
            "c.nome AS nome_cliente, COALESCE(p.nome, p.razao_social) AS nome_proprietario, p.tipo AS tipo_proprietario, " +
            SQL_ENDERECO_IMOVEL + " " +
            "FROM Visita_Agendada v " +
            "JOIN Cliente c ON c.id_cliente = v.id_cliente " +
            "JOIN Imovel i ON i.id_imovel = v.id_imovel " +
            "JOIN Proprietario p ON p.id_proprietario = i.id_proprietario " +
            "JOIN Endereco e ON e.id_endereco = i.id_endereco " +
            "WHERE %s = ? %s %s ";

    private static final String CLAUSULA_ATUAIS = "AND data >= CURRENT_DATE";
    private static final String CLAUSULA_ANTIGAS = "AND data < CURRENT_DATE";
    private static final String CLAUSULA_POTENCIAL_CONTRATO = "AND v.potencial_contrato";
    private static final String CLAUSULA_ORDENACAO = "ORDER BY v.data %s, v.horario %s";


    private final JdbcTemplate jdbcTemplate;

    public VisitaAgendadaPostgre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<VisitaAgendada> rowMapper = (rs, rowNum) -> {
        VisitaAgendada v = new VisitaAgendada();
        v.setIdVisita(rs.getInt("id_visita"));
        v.setData(rs.getDate("data").toLocalDate());
        v.setHorario(rs.getTime("horario").toLocalTime());
        v.setCorretor(new Corretor(rs.getInt("id_corretor")));
        v.setCliente(new Cliente(rs.getInt("id_cliente")));
        v.setImovel(new Imovel(rs.getInt("id_imovel")));
        return v;
    };

    private final RowMapper<VisitaAgendada> rmVisitasPainel = (rs, _) -> {
        VisitaAgendada v = new VisitaAgendada();

        v.setCorretor(new Corretor(rs.getInt("id_corretor")));

        Cliente c = new Cliente();
            c.setIdCliente(rs.getInt("id_cliente"));
            c.setNome(rs.getString("nome_cliente"));
        v.setCliente(c);

        Imovel i = new Imovel();
            i.setIdImovel(rs.getInt("id_imovel"));
            i.setStatus(rs.getString("status"));
            i.setValor(rs.getBigDecimal("valor"));

            Proprietario p = new Proprietario();
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
        v.setImovel(i);

        v.setData(rs.getDate("data").toLocalDate());
        v.setHorario(rs.getTime("horario").toLocalTime());
        v.setIdVisita(rs.getInt("id_visita"));
        v.setArquivada(!rs.getBoolean("potencial_contrato"));

        return v;
    };

    @Override
    public void salvar(VisitaAgendada visita) {

        if (visita.getIdVisita() == null || visita.getIdVisita() == 0) {
            String sql = "INSERT INTO Visita_Agendada (data, horario, id_corretor, id_cliente, id_imovel) VALUES (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id_visita"});
                ps.setDate(1, Date.valueOf(visita.getData()));
                ps.setTime(2, Time.valueOf(visita.getHorario()));
                ps.setInt(3, visita.getCorretor().getIdCorretor());
                ps.setInt(4, visita.getCliente().getIdCliente());
                ps.setInt(5, visita.getImovel().getIdImovel());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                visita.setIdVisita(keyHolder.getKey().intValue());
            }
        } else {
            String sql = "UPDATE Visita_Agendada SET data=?, horario=?, id_corretor=?, id_cliente=?, id_imovel=? WHERE id_visita=?";
            jdbcTemplate.update(sql, Date.valueOf(visita.getData()),
                                    Time.valueOf(visita.getHorario()),
                                    visita.getCorretor().getIdCorretor(),
                                    visita.getCliente().getIdCliente(),
                                    visita.getImovel().getIdImovel(),
                                    visita.getIdVisita());
        }
    }

    @Override
    public List<VisitaAgendada> buscarPorId(int id, boolean apenasAtuais, boolean maisRecente, TipoUsuarioLogado t) {
        String sql;

        if (t == TipoUsuarioLogado.CLIENTE)
            sql = String.format(BUSCA_POR_ID_PAINEL, "v.id_cliente", apenasAtuais ? CLAUSULA_ATUAIS : "", "");
        else if (t == TipoUsuarioLogado.PROPRIETARIO)
            sql = String.format(BUSCA_POR_ID_PAINEL, "i.id_proprietario", apenasAtuais ? CLAUSULA_ATUAIS : "", "");
        else
            sql = String.format(BUSCA_POR_ID_PAINEL, "v.id_corretor", apenasAtuais ? CLAUSULA_ATUAIS : "", "");

        sql += formatSQLordenacao(maisRecente);

        return jdbcTemplate.query(sql, rmVisitasPainel, id);
    }

    @Override
    public Optional<VisitaAgendada> buscarPorId(int idVisita) {
        String sql = "SELECT * FROM Visita_Agendada WHERE id_visita=?";
        List<VisitaAgendada> resultado = jdbcTemplate.query(sql, rowMapper, idVisita);

        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.getFirst());
    }

    @Override
    public List<VisitaAgendada> listarPotenciaisContratos(int idCorretor) {
        String sql = String.format(BUSCA_POR_ID_PAINEL, "v.id_corretor", CLAUSULA_ANTIGAS, CLAUSULA_POTENCIAL_CONTRATO);
        sql += formatSQLordenacao(false);
        return jdbcTemplate.query(sql, rmVisitasPainel, idCorretor);
    }

    @Override
    public List<VisitaAgendada> listarPorImovel(int idImovel, boolean apenasAtuais, boolean maisRecente) {
        String sql = "SELECT * FROM Visita_Agendada v WHERE id_imovel = ? ";
        sql += apenasAtuais ? (CLAUSULA_ATUAIS + " ") : "";
        sql += formatSQLordenacao(maisRecente);
        return jdbcTemplate.query(sql, rowMapper, idImovel);
    }

    @Override
    public void arquivar(int id) {
        String sql = "UPDATE Visita_Agendada SET potencial_contrato=FALSE WHERE id_visita=?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean excluir(int id) {
        return jdbcTemplate.update("DELETE FROM Visita_Agendada WHERE id_visita = ?", id) > 0;
    }

    private static String formatSQLordenacao(boolean maisRecente) {
        return maisRecente ? String.format(CLAUSULA_ORDENACAO, "DESC", "DESC") : String.format(CLAUSULA_ORDENACAO, "ASC", "ASC");
    }
}
