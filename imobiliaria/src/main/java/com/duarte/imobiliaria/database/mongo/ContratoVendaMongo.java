package com.duarte.imobiliaria.database.mongo;

import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.database.interfaceDAO.ContratoVendaDAO;
import com.duarte.imobiliaria.model.*;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongo")
public class ContratoVendaMongo extends MongoDaoBase implements ContratoVendaDAO {

    public ContratoVendaMongo(MongoTemplate mongo) {
        super(mongo);
    }

    private static void atributosGerais(Document d, ContratoVenda c) {
        c.setIdVenda(inteiro(d, "_id"));
        c.setData(data(d, "data"));
        c.setValorVenda(decimal(d, "valorVenda"));
        c.setValorSinal(decimal(d, "valorSinal"));
        c.setFormaPagamento(texto(d, "formaPagamento"));
        c.setValorFinanciado(decimal(d, "valorFinanciado"));
        c.setValorComissao(decimal(d, "valorComissao"));
    }

    private static ContratoVenda basico(Document d) {
        ContratoVenda c = new ContratoVenda();
        atributosGerais(d, c);
        c.setCliente(new Cliente(inteiro(d, "clienteId")));
        c.setProprietario(new Proprietario(inteiro(d, "proprietarioId")));
        c.setImovel(new Imovel(inteiro(d, "imovelId")));
        return c;
    }

    private ContratoVenda painel(Document d) {
        ContratoVenda c = new ContratoVenda();
        atributosGerais(d, c);
        c.setProprietario(proprietarioResumo(inteiro(d, "proprietarioId")));
        c.setImovel(imovelPainel(inteiro(d, "imovelId")));
        c.setCliente(clienteResumo(inteiro(d, "clienteId")));
        return c;
    }

    @Override
    public void salvar(ContratoVenda c) {
        boolean novo = c.getIdVenda() == null || c.getIdVenda() == 0;
        int id = novo ? proximoId(VENDAS) : c.getIdVenda();

        LocalDate data = c.getData() != null ? c.getData() : LocalDate.now();

        Document d = new Document("_id", id)
                .append("data", data.toString())
                .append("valorVenda", dec(c.getValorVenda()))
                .append("valorSinal", dec(c.getValorSinal()))
                .append("formaPagamento", c.getFormaPagamento())
                .append("valorFinanciado", dec(c.getValorFinanciado()))
                .append("valorComissao", dec(c.getValorComissao()))
                .append("clienteId", c.getCliente().getIdCliente())
                .append("proprietarioId", c.getProprietario().getIdProprietario())
                .append("imovelId", c.getImovel().getIdImovel())
                .append("corretorId", c.getCorretor() == null ? null : c.getCorretor().getIdCorretor());

        if (novo) {
            mongo.insert(d, VENDAS);
            c.setIdVenda(id);

            // O que no Postgres era feito pelos triggers do contrato:
            int idImovel = c.getImovel().getIdImovel();
            alterarStatusImovel(idImovel, STATUS_VENDIDO);
            arquivarVisitasDoImovel(idImovel);
            desativarPublicacoesDoImovel(idImovel);
        } else {
            mongo.save(d, VENDAS);
        }
    }

    @Override
    public Optional<ContratoVenda> buscarPorId(int id) {
        return umPorId(VENDAS, id).map(ContratoVendaMongo::basico);
    }

    @Override
    public List<ContratoVenda> buscarPorId(int id, TipoUsuarioLogado t, boolean maisRecente) {
        if (t == null) return List.of();

        Query q;
        if (t == TipoUsuarioLogado.CLIENTE) q = por("clienteId", id);
        else if (t == TipoUsuarioLogado.PROPRIETARIO) q = por("proprietarioId", id);
        else q = por("corretorId", id);

        Comparator<Document> cmp = Comparator.comparing(d -> data(d, "data"));
        return varios(VENDAS, q).stream()
                .sorted(maisRecente ? cmp.reversed() : cmp)
                .map(this::painel).toList();
    }

    @Override
    public List<ContratoVenda> listarTodos() {
        return varios(VENDAS, new Query().with(Sort.by("_id"))).stream()
                .map(ContratoVendaMongo::basico).toList();
    }
}
