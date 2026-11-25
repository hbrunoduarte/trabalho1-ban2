package com.duarte.imobiliaria.database.mongo;

import com.duarte.imobiliaria.database.interfaceDAO.TelefoneDAO;
import com.duarte.imobiliaria.model.*;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Os três tipos de telefone ficam na mesma coleção "telefones":
 * { _id, telefone, tipoDono: CLIENTE|PROPRIETARIO|FIADOR, donoId }.
 */
@Repository
@Profile("mongo")
public class TelefoneMongo extends MongoDaoBase implements TelefoneDAO {

    private static final String CLIENTE = "CLIENTE";
    private static final String PROPRIETARIO = "PROPRIETARIO";
    private static final String FIADOR = "FIADOR";

    public TelefoneMongo(MongoTemplate mongo) {
        super(mongo);
    }

    private void salvarTelefone(Integer idTelefone, String telefone, String tipoDono, Integer donoId,
                                java.util.function.Consumer<Integer> aoCriar) {
        if (idTelefone == null) {
            int id = proximoId(TELEFONES);
            mongo.insert(new Document("_id", id)
                    .append("telefone", telefone)
                    .append("tipoDono", tipoDono)
                    .append("donoId", donoId), TELEFONES);
            aoCriar.accept(id);
        } else {
            mongo.updateFirst(porId(idTelefone), new Update().set("telefone", telefone), TELEFONES);
        }
    }

    @Override
    public void salvarDoCliente(List<TelefoneCliente> telefones) {
        if (telefones == null) return;
        for (TelefoneCliente t : telefones)
            salvarTelefone(t.getIdTelefone(), t.getTelefone(), CLIENTE, t.getCliente().getIdCliente(), t::setIdTelefone);
    }

    @Override
    public void salvarDoProprietario(List<TelefoneProprietario> telefones) {
        if (telefones == null) return;
        for (TelefoneProprietario t : telefones)
            salvarTelefone(t.getIdTelefone(), t.getTelefone(), PROPRIETARIO, t.getProprietario().getIdProprietario(), t::setIdTelefone);
    }

    @Override
    public void salvarDoFiador(List<TelefoneFiador> telefones) {
        if (telefones == null) return;
        for (TelefoneFiador t : telefones)
            salvarTelefone(t.getIdTelefone(), t.getTelefone(), FIADOR, t.getFiador().getIdFiador(), t::setIdTelefone);
    }

    private boolean excluir(int idTelefone, String tipoDono) {
        Query q = Query.query(Criteria.where("_id").is(idTelefone).and("tipoDono").is(tipoDono));
        return mongo.remove(q, TELEFONES).getDeletedCount() > 0;
    }

    @Override
    public boolean excluirDoCliente(int idTelefone) {
        return excluir(idTelefone, CLIENTE);
    }

    @Override
    public boolean excluirDoProprietario(int idTelefone) {
        return excluir(idTelefone, PROPRIETARIO);
    }

    @Override
    public boolean excluirDoFiador(int idTelefone) {
        return excluir(idTelefone, FIADOR);
    }

    private List<Document> doDono(String tipoDono, int donoId) {
        Query q = Query.query(Criteria.where("tipoDono").is(tipoDono).and("donoId").is(donoId)).with(Sort.by("_id"));
        return varios(TELEFONES, q);
    }

    @Override
    public List<TelefoneCliente> listarDoCliente(int idCliente) {
        return doDono(CLIENTE, idCliente).stream()
                .map(d -> new TelefoneCliente(inteiro(d, "_id"), texto(d, "telefone"), new Cliente(inteiro(d, "donoId"))))
                .toList();
    }

    @Override
    public List<TelefoneProprietario> listarDoProprietario(int idProprietario) {
        return doDono(PROPRIETARIO, idProprietario).stream()
                .map(d -> new TelefoneProprietario(inteiro(d, "_id"), texto(d, "telefone"), new Proprietario(inteiro(d, "donoId"))))
                .toList();
    }

    @Override
    public List<TelefoneProprietario> buscarPorIdImovel(int idImovel) {
        return umPorId(IMOVEIS, idImovel)
                .map(i -> listarDoProprietario(inteiro(i, "proprietarioId")))
                .orElse(List.of());
    }

    @Override
    public List<TelefoneFiador> listarDoFiador(int idFiador) {
        return doDono(FIADOR, idFiador).stream()
                .map(d -> new TelefoneFiador(inteiro(d, "_id"), texto(d, "telefone"), new Fiador(inteiro(d, "donoId"))))
                .toList();
    }
}
