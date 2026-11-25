package com.duarte.imobiliaria.database.mongo;

import com.duarte.imobiliaria.database.interfaceDAO.ClienteDAO;
import com.duarte.imobiliaria.model.Cliente;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongo")
public class ClienteMongo extends MongoDaoBase implements ClienteDAO {

    public ClienteMongo(MongoTemplate mongo) {
        super(mongo);
    }

    private static Cliente paraCliente(Document d) {
        Cliente c = new Cliente();
        c.setIdCliente(inteiro(d, "_id"));
        c.setRendaMensal(d.get("rendaMensal") instanceof Number n ? n.doubleValue() : null);
        c.setProfissao(texto(d, "profissao"));
        c.setTipoInteressado(inteiro(d, "tipoImovelInteresse"));
        c.setCpf(texto(d, "cpf"));
        c.setEmail(texto(d, "email"));
        c.setSenha(texto(d, "senha"));
        c.setNome(texto(d, "nome"));
        return c;
    }

    @Override
    public void salvar(Cliente c) {
        boolean novo = c.getIdCliente() == null || c.getIdCliente() == 0;
        int id = novo ? proximoId(CLIENTES) : c.getIdCliente();

        Document d = new Document("_id", id)
                .append("nome", c.getNome())
                .append("cpf", c.getCpf())
                .append("email", c.getEmail())
                .append("senha", c.getSenha())
                .append("rendaMensal", c.getRendaMensal())
                .append("profissao", c.getProfissao())
                .append("tipoImovelInteresse", c.getTipoInteressado());

        if (novo) {
            mongo.insert(d, CLIENTES);
            c.setIdCliente(id);
        } else {
            mongo.save(d, CLIENTES);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(int id) {
        return umPorId(CLIENTES, id).map(ClienteMongo::paraCliente);
    }

    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        return um(CLIENTES, por("cpf", cpf)).map(ClienteMongo::paraCliente);
    }

    @Override
    public List<Cliente> listarTodos() {
        return varios(CLIENTES, new Query().with(Sort.by("_id"))).stream().map(ClienteMongo::paraCliente).toList();
    }

    @Override
    public boolean excluir(int id) {
        exigirSemReferencias("Cliente", id, new String[][]{
                {VISITAS, "clienteId"}, {LOCACOES, "clienteId"}, {VENDAS, "clienteId"}});
        mongo.remove(Query.query(Criteria.where("tipoDono").is("CLIENTE").and("donoId").is(id)), TELEFONES);
        return mongo.remove(porId(id), CLIENTES).getDeletedCount() > 0;
    }
}
