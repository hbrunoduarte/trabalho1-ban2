package com.duarte.imobiliaria.database.postgree;

import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.controllers.session.UsuarioLogado;
import com.duarte.imobiliaria.database.interfaceDAO.LoginDAO;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("postgre")
public class LoginPostgre implements LoginDAO {

    private final JdbcTemplate jdbcTemplate;
    private final String consultaPadrao = "SELECT %s FROM %s WHERE (email=? %s) AND senha=?";

    public LoginPostgre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<UsuarioLogado> autenticar(String login, String senha, TipoUsuarioLogado t) {
        String sql;

        switch (t) {
            case TipoUsuarioLogado.PROPRIETARIO:
                sql = String.format(consultaPadrao,
                        "id_proprietario",
                        "Proprietario",
                        "OR cpf=? OR cnpj=?");
                List<UsuarioLogado> r1 = jdbcTemplate.query(sql, getRowMapper("id_proprietario", t),
                                                                    login, login, login, senha);
                return r1.isEmpty() ? Optional.empty() : Optional.of(r1.getFirst());
            default:
                String idLabel = t == TipoUsuarioLogado.CLIENTE ? "id_cliente" : "id_corretor";
                sql = String.format(consultaPadrao, idLabel,
                        t == TipoUsuarioLogado.CLIENTE ? "Cliente" : "Corretor",
                        "OR cpf=?");
                List<UsuarioLogado> r2 = jdbcTemplate.query(sql, getRowMapper(idLabel, t),
                        login, login, senha);
                return r2.isEmpty() ? Optional.empty() : Optional.of(r2.getFirst());
        }
    }

    private RowMapper<UsuarioLogado> getRowMapper(String id, TipoUsuarioLogado t) {
        return (rs, _) -> new UsuarioLogado(rs.getInt(id), t);
    }
}
