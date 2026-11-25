package com.duarte.imobiliaria.database.mongo;

import com.duarte.imobiliaria.database.interfaceDAO.EnderecoDAO;
import com.duarte.imobiliaria.model.Endereco;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("mongo")
public class EnderecoMongo extends MongoDaoBase implements EnderecoDAO {

    public EnderecoMongo(MongoTemplate mongo) {
        super(mongo);
    }

    private static Document paraDocumento(Endereco e, int id) {
        return new Document("_id", id)
                .append("cep", e.getCep())
                .append("estado", e.getEstado())
                .append("cidade", e.getCidade())
                .append("bairro", e.getBairro())
                .append("numero", e.getNumero())
                .append("rua", e.getRua())
                .append("complemento", e.getComplemento());
    }

    @Override
    public void inserir(Endereco endereco) {
        int id = proximoId(ENDERECOS);
        mongo.insert(paraDocumento(endereco, id), ENDERECOS);
        endereco.setIdEndereco(id);
    }

    @Override
    public void salvar(Endereco endereco) {
        if (endereco.getIdEndereco() == null) {
            inserir(endereco);
            return;
        }
        mongo.save(paraDocumento(endereco, endereco.getIdEndereco()), ENDERECOS);
    }

    @Override
    public Optional<Endereco> buscarPorId(int id) {
        return umPorId(ENDERECOS, id).map(MongoDaoBase::endereco);
    }

    @Override
    public Optional<Endereco> buscarEndereco(int cep, int numero) {
        Query q = Query.query(Criteria.where("cep").is(String.format("%08d", cep)).and("numero").is(numero));
        return um(ENDERECOS, q).map(MongoDaoBase::endereco);
    }

    @Override
    public Optional<Endereco> buscarDadosCep(int cep) {
        return um(ENDERECOS, por("cep", String.format("%08d", cep))).map(d -> {
            Endereco e = new Endereco();
            e.setCep(texto(d, "cep"));
            e.setEstado(texto(d, "estado"));
            e.setCidade(texto(d, "cidade"));
            e.setBairro(texto(d, "bairro"));
            e.setRua(texto(d, "rua"));
            return e;
        });
    }

    @Override
    public void remover(int idEndereco) {
        mongo.remove(porId(idEndereco), ENDERECOS);
    }
}
