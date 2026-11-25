package com.duarte.imobiliaria.database.mongo;

import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.database.interfaceDAO.ContratoLocacaoDAO;
import com.duarte.imobiliaria.model.*;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongo")
public class ContratoLocacaoMongo extends MongoDaoBase implements ContratoLocacaoDAO {

    private static final String ATIVO = "ATIVO";
    private static final String ENCERRADO = "ENCERRADO";

    public ContratoLocacaoMongo(MongoTemplate mongo) {
        super(mongo);
    }

    private static void atributosGerais(Document d, ContratoLocacao c) {
        c.setIdLocacao(inteiro(d, "_id"));
        c.setPrazoMes(inteiro(d, "prazoEmMeses"));
        c.setGarantia(texto(d, "garantia"));
        c.setValorCaucao(decimal(d, "valorCaucao"));
        c.setValorComissao(decimal(d, "valorComissao"));
        c.setStatusContrato(texto(d, "statusContrato"));
        c.setDiaVencimento(inteiro(d, "diaVencimento"));
        c.setDataInicio(data(d, "dataInicio"));
        c.setValorAluguel(decimal(d, "valorAluguel"));
        c.setDataRescisao(data(d, "dataRescisao"));
        c.setMulta(decimal(d, "multa"));
        c.setMotivo(texto(d, "motivo"));
    }

    private static ContratoLocacao basico(Document d) {
        ContratoLocacao c = new ContratoLocacao();
        atributosGerais(d, c);
        c.setImovel(new Imovel(inteiro(d, "imovelId")));
        c.setCliente(new Cliente(inteiro(d, "clienteId")));
        c.setCorretor(new Corretor(inteiro(d, "corretorId")));
        return c;
    }

    private ContratoLocacao painel(Document d) {
        ContratoLocacao c = new ContratoLocacao();
        atributosGerais(d, c);
        c.setImovel(imovelPainel(inteiro(d, "imovelId")));
        c.setCliente(clienteResumo(inteiro(d, "clienteId")));
        return c;
    }

    @Override
    public void salvar(ContratoLocacao c) {
        if (c.getIdLocacao() == null || c.getIdLocacao() == 0) {
            int id = proximoId(LOCACOES);

            // data_inicio tinha DEFAULT no Postgres: primeiro dia do mês seguinte
            LocalDate inicio = c.getDataInicio() != null ? c.getDataInicio()
                    : LocalDate.now().withDayOfMonth(1).plusMonths(1);

            Document d = new Document("_id", id)
                    .append("prazoEmMeses", c.getPrazoMes())
                    .append("garantia", c.getGarantia());
            ponha(d, "valorCaucao", dec(c.getValorCaucao()));
            d.append("valorComissao", dec(c.getValorComissao()))
                    .append("statusContrato", ATIVO)
                    .append("diaVencimento", c.getDiaVencimento())
                    .append("dataInicio", inicio.toString())
                    .append("valorAluguel", dec(c.getValorAluguel()))
                    .append("valorInicial", dec(c.getValorAluguel()))
                    .append("imovelId", c.getImovel().getIdImovel())
                    .append("clienteId", c.getCliente().getIdCliente())
                    .append("corretorId", c.getCorretor().getIdCorretor());
            if (c.getFiador() != null) ponha(d, "fiadorId", c.getFiador().getIdFiador());

            mongo.insert(d, LOCACOES);
            c.setIdLocacao(id);
            c.setDataInicio(inicio);
            c.setStatusContrato(ATIVO);

            // O que no Postgres era feito pelos triggers do contrato:
            int idImovel = c.getImovel().getIdImovel();
            alterarStatusImovel(idImovel, STATUS_ALUGADO);
            arquivarVisitasDoImovel(idImovel);
            desativarPublicacoesDoImovel(idImovel);
        } else {
            Update u = new Update()
                    .set("prazoEmMeses", c.getPrazoMes())
                    .set("garantia", c.getGarantia())
                    .set("valorCaucao", dec(c.getValorCaucao()))
                    .set("valorComissao", dec(c.getValorComissao()))
                    .set("statusContrato", c.getStatusContrato())
                    .set("diaVencimento", c.getDiaVencimento())
                    .set("valorAluguel", dec(c.getValorAluguel()))
                    .set("imovelId", c.getImovel().getIdImovel())
                    .set("clienteId", c.getCliente().getIdCliente());
            if (c.getDataInicio() != null) u.set("dataInicio", c.getDataInicio().toString());

            if (c.isRescindido()) {
                u.set("dataRescisao", c.getDataRescisao().toString())
                        .set("multa", dec(c.getMulta()))
                        .set("motivo", c.getMotivo());
            } else {
                u.unset("dataRescisao").unset("multa").unset("motivo");
            }
            mongo.updateFirst(porId(c.getIdLocacao()), u, LOCACOES);
        }
    }

    @Override
    public Optional<ContratoLocacao> buscarPorId(int id) {
        return umPorId(LOCACOES, id).map(ContratoLocacaoMongo::basico);
    }

    @Override
    public List<ContratoLocacao> buscarPorId(int id, TipoUsuarioLogado t, boolean maisRecente) {
        if (t == null) return List.of();

        Query q;
        if (t == TipoUsuarioLogado.CLIENTE) q = por("clienteId", id);
        else if (t == TipoUsuarioLogado.PROPRIETARIO)
            q = Query.query(Criteria.where("imovelId").in(idsImoveisDoProprietario(id)));
        else q = por("corretorId", id);

        Comparator<Document> cmp = Comparator.comparing(d -> data(d, "dataInicio"));
        return varios(LOCACOES, q).stream()
                .sorted(maisRecente ? cmp.reversed() : cmp)
                .map(this::painel).toList();
    }

    @Override
    public boolean rescindir(int idLocacao, LocalDate dataRescisao, BigDecimal multa, String motivo) {
        Query q = Query.query(Criteria.where("_id").is(idLocacao).and("statusContrato").is(ATIVO));
        Update u = new Update()
                .set("statusContrato", ENCERRADO)
                .set("dataRescisao", dataRescisao.toString())
                .set("multa", dec(multa))
                .set("motivo", motivo);

        boolean ok = mongo.updateFirst(q, u, LOCACOES).getModifiedCount() == 1;

        // o imóvel volta a ficar disponível para locação
        if (ok) {
            umPorId(LOCACOES, idLocacao).ifPresent(d -> alterarStatusImovel(inteiro(d, "imovelId"), STATUS_LOCACAO));
        }
        return ok;
    }

    @Override
    public List<ContratoLocacao> listarTodos() {
        return varios(LOCACOES, new Query().with(Sort.by("_id"))).stream()
                .map(ContratoLocacaoMongo::basico).toList();
    }

    private static final String STATUS_LOCACAO = com.duarte.imobiliaria.database.interfaceDAO.ImovelDAO.STATUS_LOCACAO;
}
