package com.duarte.imobiliaria.database.mongo;

import com.duarte.imobiliaria.database.interfaceDAO.CorretorDAO;
import com.duarte.imobiliaria.model.Corretor;
import com.duarte.imobiliaria.model.VisitaAgendada;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongo")
public class CorretorMongo extends MongoDaoBase implements CorretorDAO {

    public CorretorMongo(MongoTemplate mongo) {
        super(mongo);
    }

    private static Corretor paraCorretor(Document d) {
        Corretor c = new Corretor();
        c.setIdCorretor(inteiro(d, "_id"));
        c.setNome(texto(d, "nome"));
        c.setEmail(texto(d, "email"));
        c.setSenha(texto(d, "senha"));
        c.setTelefone(texto(d, "telefone"));
        c.setCpf(texto(d, "cpf"));
        c.setPercentualComissao(real(d, "percentualComissao"));
        c.setDataAdmissao(data(d, "dataAdmissao"));
        c.setCreci(texto(d, "creci"));
        return c;
    }

    /**
     * Corretor sem outra visita "potencial" a menos de 1h do horário pedido;
     * desempate pelo que tem menos visitas no total e depois pelo menor id.
     */
    @Override
    public Optional<Corretor> proximoParaVisita(VisitaAgendada v) {
        LocalDateTime alvo = LocalDateTime.of(v.getData(), v.getHorario());

        record Candidato(int id, long total) {}

        List<Candidato> livres = varios(CORRETORES, new Query()).stream()
                .map(d -> inteiro(d, "_id"))
                .filter(id -> varios(VISITAS, por("corretorId", id)).stream()
                        .filter(x -> Boolean.TRUE.equals(bool(x, "potencialContrato")))
                        .noneMatch(x -> {
                            LocalDateTime t = LocalDateTime.of(data(x, "data"), hora(x, "horario"));
                            return Math.abs(Duration.between(t, alvo).toMinutes()) < 60;
                        }))
                .map(id -> new Candidato(id, mongo.count(por("corretorId", id), VISITAS)))
                .sorted(Comparator.comparingLong(Candidato::total).thenComparingInt(Candidato::id))
                .toList();

        if (livres.isEmpty()) return Optional.empty();
        return Optional.of(new Corretor(livres.getFirst().id()));
    }

    @Override
    public void salvar(Corretor c) {
        boolean novo = c.getIdCorretor() == null || c.getIdCorretor() == 0;
        int id = novo ? proximoId(CORRETORES) : c.getIdCorretor();

        Document d = new Document("_id", id)
                .append("nome", c.getNome())
                .append("email", c.getEmail())
                .append("senha", c.getSenha())
                .append("telefone", c.getTelefone())
                .append("cpf", c.getCpf())
                .append("percentualComissao", c.getPercentualComissao() == null ? null : c.getPercentualComissao().doubleValue())
                .append("dataAdmissao", c.getDataAdmissao() == null ? null : c.getDataAdmissao().toString())
                .append("creci", c.getCreci());

        if (novo) {
            mongo.insert(d, CORRETORES);
            c.setIdCorretor(id);
        } else {
            mongo.save(d, CORRETORES);
        }
    }

    @Override
    public Optional<Corretor> buscarPorId(int id) {
        return umPorId(CORRETORES, id).map(CorretorMongo::paraCorretor);
    }

    @Override
    public Optional<Corretor> buscarPorCpf(String cpf) {
        return um(CORRETORES, por("cpf", cpf)).map(CorretorMongo::paraCorretor);
    }

    @Override
    public Optional<Corretor> buscarPorCreci(String creci) {
        return um(CORRETORES, por("creci", creci)).map(CorretorMongo::paraCorretor);
    }

    @Override
    public List<Corretor> listarTodos() {
        return varios(CORRETORES, new Query().with(Sort.by("_id"))).stream().map(CorretorMongo::paraCorretor).toList();
    }

    @Override
    public boolean excluir(int id) {
        exigirSemReferencias("Corretor", id, new String[][]{
                {VISITAS, "corretorId"}, {LOCACOES, "corretorId"}, {VENDAS, "corretorId"}});
        return mongo.remove(porId(id), CORRETORES).getDeletedCount() > 0;
    }
}
