package com.duarte.imobiliaria.database.mongo;

import com.duarte.imobiliaria.database.interfaceDAO.FiadorDAO;
import com.duarte.imobiliaria.model.Endereco;
import com.duarte.imobiliaria.model.Fiador;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("mongo")
public class FiadorMongo extends MongoDaoBase implements FiadorDAO {

    public FiadorMongo(MongoTemplate mongo) {
        super(mongo);
    }

    private static Fiador paraFiador(Document d) {
        Fiador f = new Fiador();
        f.setIdFiador(inteiro(d, "_id"));
        f.setCpf(texto(d, "cpf"));
        f.setNome(texto(d, "nome"));
        f.setComprovanteRenda(bytes(d, "comprovanteRenda"));
        f.setEndereco(new Endereco(inteiro(d, "enderecoId")));
        return f;
    }

    @Override
    public void salvar(Fiador f) {
        boolean novo = f.getIdFiador() == null || f.getIdFiador() == 0;
        int id = novo ? proximoId(FIADORES) : f.getIdFiador();

        Document d = new Document("_id", id)
                .append("cpf", f.getCpf())
                .append("nome", f.getNome())
                .append("comprovanteRenda", binario(f.getComprovanteRenda()))
                .append("enderecoId", f.getEndereco().getIdEndereco());

        if (novo) {
            mongo.insert(d, FIADORES);
            f.setIdFiador(id);
        } else {
            mongo.save(d, FIADORES);
        }
    }

    @Override
    public Optional<Fiador> buscarPorId(int id) {
        return umPorId(FIADORES, id).map(FiadorMongo::paraFiador);
    }

    @Override
    public Optional<Fiador> buscarPorCpf(String cpf) {
        return um(FIADORES, por("cpf", cpf)).map(FiadorMongo::paraFiador);
    }

    @Override
    public boolean excluir(int id) {
        exigirSemReferencias("Fiador", id, new String[][]{{LOCACOES, "fiadorId"}});
        mongo.remove(Query.query(Criteria.where("tipoDono").is("FIADOR").and("donoId").is(id)), TELEFONES);
        return mongo.remove(porId(id), FIADORES).getDeletedCount() > 0;
    }
}
