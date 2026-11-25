package com.duarte.imobiliaria.database.mongo;

import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.controllers.session.UsuarioLogado;
import com.duarte.imobiliaria.database.interfaceDAO.LoginDAO;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("mongo")
public class LoginMongo extends MongoDaoBase implements LoginDAO {

    public LoginMongo(MongoTemplate mongo) {
        super(mongo);
    }

    @Override
    public Optional<UsuarioLogado> autenticar(String login, String senha, TipoUsuarioLogado t) {
        String colecao;
        Criteria identificador;

        switch (t) {
            case PROPRIETARIO -> {
                colecao = PROPRIETARIOS;
                identificador = new Criteria().orOperator(
                        Criteria.where("email").is(login),
                        Criteria.where("cpf").is(login),
                        Criteria.where("cnpj").is(login));
            }
            case CLIENTE -> {
                colecao = CLIENTES;
                identificador = new Criteria().orOperator(
                        Criteria.where("email").is(login),
                        Criteria.where("cpf").is(login));
            }
            default -> {
                colecao = CORRETORES;
                identificador = new Criteria().orOperator(
                        Criteria.where("email").is(login),
                        Criteria.where("cpf").is(login));
            }
        }

        Query q = Query.query(new Criteria().andOperator(identificador, Criteria.where("senha").is(senha)));
        return um(colecao, q).map(d -> new UsuarioLogado(inteiro(d, "_id"), t));
    }
}
