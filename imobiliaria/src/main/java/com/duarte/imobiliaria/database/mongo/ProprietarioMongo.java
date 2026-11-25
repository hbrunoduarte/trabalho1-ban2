package com.duarte.imobiliaria.database.mongo;

import com.duarte.imobiliaria.database.interfaceDAO.ProprietarioDAO;
import com.duarte.imobiliaria.model.Endereco;
import com.duarte.imobiliaria.model.Proprietario;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongo")
public class ProprietarioMongo extends MongoDaoBase implements ProprietarioDAO {

    public ProprietarioMongo(MongoTemplate mongo) {
        super(mongo);
    }

    private static Proprietario paraProprietario(Document d) {
        Proprietario p = new Proprietario();
        p.setIdProprietario(inteiro(d, "_id"));
        p.setTipo(texto(d, "tipo").charAt(0));
        p.setCpf(texto(d, "cpf"));
        p.setCnpj(texto(d, "cnpj"));
        p.setEmail(texto(d, "email"));
        p.setSenha(texto(d, "senha"));
        p.setDadosBancarios(texto(d, "dadosBancarios"));
        p.setNome(texto(d, "nome"));
        p.setRazaoSocial(texto(d, "razaoSocial"));
        p.setEndereco(new Endereco(inteiro(d, "enderecoId")));
        return p;
    }

    @Override
    public void salvar(Proprietario p) {
        boolean novo = p.getIdProprietario() == null || p.getIdProprietario() == 0;
        int id = novo ? proximoId(PROPRIETARIOS) : p.getIdProprietario();

        // cpf/cnpj/nome/razaoSocial só entram se existirem: o índice único é parcial (type string)
        Document d = new Document("_id", id).append("tipo", p.getTipo().toString());
        ponha(d, "cpf", p.getCpf());
        ponha(d, "cnpj", p.getCnpj());
        d.append("email", p.getEmail())
                .append("senha", p.getSenha())
                .append("dadosBancarios", p.getDadosBancarios());
        ponha(d, "nome", p.getNome());
        ponha(d, "razaoSocial", p.getRazaoSocial());
        d.append("enderecoId", p.getEndereco() == null ? null : p.getEndereco().getIdEndereco());

        if (novo) {
            mongo.insert(d, PROPRIETARIOS);
            p.setIdProprietario(id);
        } else {
            mongo.save(d, PROPRIETARIOS);
        }
    }

    @Override
    public void atualizarEndereco(Integer idProprietario, Integer idEndereco) {
        if (idProprietario == null || idEndereco == null) return;
        mongo.updateFirst(porId(idProprietario), new Update().set("enderecoId", idEndereco), PROPRIETARIOS);
    }

    @Override
    public Optional<Proprietario> buscarPorId(int id) {
        return umPorId(PROPRIETARIOS, id).map(ProprietarioMongo::paraProprietario);
    }

    @Override
    public Optional<Proprietario> buscarPorCpf(String cpf) {
        return um(PROPRIETARIOS, por("cpf", cpf)).map(ProprietarioMongo::paraProprietario);
    }

    @Override
    public Optional<Proprietario> buscarPorCnpj(String cnpj) {
        return um(PROPRIETARIOS, por("cnpj", cnpj)).map(ProprietarioMongo::paraProprietario);
    }

    @Override
    public List<Proprietario> listarTodos() {
        return varios(PROPRIETARIOS, new Query().with(Sort.by("_id"))).stream()
                .map(ProprietarioMongo::paraProprietario).toList();
    }

    @Override
    public boolean excluir(int id) {
        exigirSemReferencias("Proprietário", id, new String[][]{
                {IMOVEIS, "proprietarioId"}, {VENDAS, "proprietarioId"}});
        mongo.remove(Query.query(Criteria.where("tipoDono").is("PROPRIETARIO").and("donoId").is(id)), TELEFONES);
        return mongo.remove(porId(id), PROPRIETARIOS).getDeletedCount() > 0;
    }
}
