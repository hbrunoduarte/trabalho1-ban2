package com.duarte.imobiliaria.database.mongo;

import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.database.interfaceDAO.VisitaAgendadaDAO;
import com.duarte.imobiliaria.model.Cliente;
import com.duarte.imobiliaria.model.Corretor;
import com.duarte.imobiliaria.model.Imovel;
import com.duarte.imobiliaria.model.VisitaAgendada;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongo")
public class VisitaAgendadaMongo extends MongoDaoBase implements VisitaAgendadaDAO {

    public VisitaAgendadaMongo(MongoTemplate mongo) {
        super(mongo);
    }

    private static LocalDateTime momento(Document d) {
        return LocalDateTime.of(data(d, "data"), hora(d, "horario"));
    }

    private static VisitaAgendada basica(Document d) {
        VisitaAgendada v = new VisitaAgendada();
        v.setIdVisita(inteiro(d, "_id"));
        v.setData(data(d, "data"));
        v.setHorario(hora(d, "horario"));
        v.setCorretor(new Corretor(inteiro(d, "corretorId")));
        v.setCliente(new Cliente(inteiro(d, "clienteId")));
        v.setImovel(new Imovel(inteiro(d, "imovelId")));
        v.setArquivada(!Boolean.TRUE.equals(bool(d, "potencialContrato")));
        return v;
    }

    private VisitaAgendada painel(Document d) {
        VisitaAgendada v = new VisitaAgendada();
        v.setIdVisita(inteiro(d, "_id"));
        v.setData(data(d, "data"));
        v.setHorario(hora(d, "horario"));
        v.setCorretor(new Corretor(inteiro(d, "corretorId")));
        v.setCliente(clienteResumo(inteiro(d, "clienteId")));
        v.setImovel(imovelPainel(inteiro(d, "imovelId")));
        v.setArquivada(!Boolean.TRUE.equals(bool(d, "potencialContrato")));
        return v;
    }

    private static List<Document> ordenar(List<Document> docs, boolean maisRecente) {
        Comparator<Document> c = Comparator.comparing(VisitaAgendadaMongo::momento);
        return docs.stream().sorted(maisRecente ? c.reversed() : c).toList();
    }

    private static boolean atual(Document d) {
        return !momento(d).isBefore(LocalDateTime.now());
    }

    /** Regra que no Postgres era trigger: no máx. uma visita por hora para o corretor e para o imóvel. */
    private void validarHorarioLivre(VisitaAgendada v) {
        LocalDateTime alvo = LocalDateTime.of(v.getData(), v.getHorario());
        for (String[] campoId : new String[][]{
                {"corretorId", String.valueOf(v.getCorretor().getIdCorretor())},
                {"imovelId", String.valueOf(v.getImovel().getIdImovel())}}) {

            boolean conflito = varios(VISITAS, por(campoId[0], Integer.parseInt(campoId[1]))).stream()
                    .filter(x -> Boolean.TRUE.equals(bool(x, "potencialContrato")))
                    .filter(x -> v.getIdVisita() == null || !v.getIdVisita().equals(inteiro(x, "_id")))
                    .anyMatch(x -> Math.abs(Duration.between(momento(x), alvo).toMinutes()) < 60);

            if (conflito) {
                throw new DuplicateKeyException("Já existe visita agendada neste horário (" + campoId[0] + ")");
            }
        }
    }

    @Override
    public void salvar(VisitaAgendada v) {
        validarHorarioLivre(v);

        boolean novo = v.getIdVisita() == null || v.getIdVisita() == 0;
        if (novo) {
            int id = proximoId(VISITAS);
            mongo.insert(new Document("_id", id)
                    .append("data", v.getData().toString())
                    .append("horario", v.getHorario().toString())
                    .append("corretorId", v.getCorretor().getIdCorretor())
                    .append("clienteId", v.getCliente().getIdCliente())
                    .append("imovelId", v.getImovel().getIdImovel())
                    .append("potencialContrato", true), VISITAS);
            v.setIdVisita(id);
        } else {
            mongo.updateFirst(porId(v.getIdVisita()), new Update()
                    .set("data", v.getData().toString())
                    .set("horario", v.getHorario().toString())
                    .set("corretorId", v.getCorretor().getIdCorretor())
                    .set("clienteId", v.getCliente().getIdCliente())
                    .set("imovelId", v.getImovel().getIdImovel()), VISITAS);
        }
    }

    @Override
    public Optional<VisitaAgendada> buscarPorId(int idVisita) {
        return umPorId(VISITAS, idVisita).map(VisitaAgendadaMongo::basica);
    }

    @Override
    public List<VisitaAgendada> buscarPorId(int id, boolean apenasAtuais, boolean maisRecente, TipoUsuarioLogado t) {
        Query q;
        if (t == TipoUsuarioLogado.CLIENTE) {
            q = por("clienteId", id);
        } else if (t == TipoUsuarioLogado.PROPRIETARIO) {
            q = Query.query(Criteria.where("imovelId").in(idsImoveisDoProprietario(id)));
        } else {
            q = por("corretorId", id);
        }

        List<Document> docs = varios(VISITAS, q).stream()
                .filter(d -> !apenasAtuais || atual(d)).toList();
        return ordenar(docs, maisRecente).stream().map(this::painel).toList();
    }

    @Override
    public List<VisitaAgendada> listarPotenciaisContratos(int idCorretor) {
        List<Document> docs = varios(VISITAS, por("corretorId", idCorretor)).stream()
                .filter(d -> Boolean.TRUE.equals(bool(d, "potencialContrato")))
                .filter(d -> momento(d).isBefore(LocalDateTime.now()))
                .toList();
        return ordenar(docs, false).stream().map(this::painel).toList();
    }

    @Override
    public List<VisitaAgendada> listarPorImovel(int idImovel, boolean apenasAtuais, boolean maisRecente) {
        List<Document> docs = varios(VISITAS, por("imovelId", idImovel)).stream()
                .filter(d -> !apenasAtuais || atual(d)).toList();
        return ordenar(docs, maisRecente).stream().map(VisitaAgendadaMongo::basica).toList();
    }

    @Override
    public void arquivar(int id) {
        mongo.updateFirst(porId(id), new Update().set("potencialContrato", false), VISITAS);
    }

    @Override
    public boolean excluir(int id) {
        return mongo.remove(porId(id), VISITAS).getDeletedCount() > 0;
    }
}
