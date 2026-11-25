package com.duarte.imobiliaria.database.mongo;

import com.duarte.imobiliaria.controllers.DTO.FiltroReciboDTO;
import com.duarte.imobiliaria.database.interfaceDAO.ReciboDAO;
import com.duarte.imobiliaria.model.ContratoLocacao;
import com.duarte.imobiliaria.model.ReciboAluguel;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Profile("mongo")
public class ReciboMongo extends MongoDaoBase implements ReciboDAO {

    public ReciboMongo(MongoTemplate mongo) {
        super(mongo);
    }

    private static ReciboAluguel paraRecibo(Document d) {
        ReciboAluguel r = new ReciboAluguel();
        r.setIdRecibo(inteiro(d, "_id"));
        r.setDesconto(decimal(d, "desconto"));
        r.setAcrescimo(decimal(d, "acrescimo"));
        r.setValorLiquido(decimal(d, "valorLiquido"));
        r.setFormaPagamento(texto(d, "formaPagamento"));
        r.setDataVencimento(data(d, "dataVencimento"));
        r.setDataPagamento(data(d, "dataPagamento"));
        r.setValorAluguel(decimal(d, "valorAluguel"));
        r.setMes(inteiro(d, "mes"));
        r.setAno(inteiro(d, "ano"));
        r.setContratoLocacao(new ContratoLocacao(inteiro(d, "locacaoId")));
        return r;
    }

    private static final Sort ORDENACAO = Sort.by("ano", "mes");

    @Override
    public List<ReciboAluguel> filtrar(FiltroReciboDTO filtro) {
        Criteria c = Criteria.where("locacaoId").is(filtro.getIdLocacao());
        if (filtro.getAno() != null) c.and("ano").is(Integer.valueOf(filtro.getAno()));
        if (filtro.getSituacao() != null) {
            if (filtro.isPago()) c.and("dataPagamento").ne(null);
            else c.and("dataPagamento").is(null);
        }
        return varios(RECIBOS, Query.query(c).with(ORDENACAO)).stream().map(ReciboMongo::paraRecibo).toList();
    }

    @Override
    public List<ReciboAluguel> buscarPorLocacao(int idLocacao) {
        return varios(RECIBOS, por("locacaoId", idLocacao).with(ORDENACAO)).stream().map(ReciboMongo::paraRecibo).toList();
    }

    /**
     * Substitui a função fn_gerar_recibos() do Postgres: para cada contrato ATIVO cria os recibos
     * que faltam, do mês de início até o mês atual (respeitando o prazo). O valor de cada mês é o
     * do último reajuste até o vencimento, ou o valor inicial do contrato.
     */
    @Override
    public int gerarRecibosPendentes() {
        int criados = 0;
        YearMonth hoje = YearMonth.now();

        for (Document contrato : varios(LOCACOES, por("statusContrato", "ATIVO"))) {
            int idLocacao = inteiro(contrato, "_id");
            YearMonth inicio = YearMonth.from(data(contrato, "dataInicio"));
            int prazo = inteiro(contrato, "prazoEmMeses");
            int dia = Math.min(28, inteiro(contrato, "diaVencimento"));

            BigDecimal valorInicial = contrato.get("valorInicial") != null
                    ? decimal(contrato, "valorInicial") : decimal(contrato, "valorAluguel");

            List<Document> reajustes = varios(REAJUSTES, por("locacaoId", idLocacao).with(Sort.by("data")));

            Set<Integer> existentes = new HashSet<>();
            for (Document r : varios(RECIBOS, por("locacaoId", idLocacao)))
                existentes.add(inteiro(r, "ano") * 100 + inteiro(r, "mes"));

            for (int k = 0; k < prazo; k++) {
                YearMonth ym = inicio.plusMonths(k);
                if (ym.isAfter(hoje)) break;
                if (existentes.contains(ym.getYear() * 100 + ym.getMonthValue())) continue;

                LocalDate vencimento = ym.atDay(dia);
                BigDecimal valor = valorInicial;
                for (Document rj : reajustes) {
                    if (!data(rj, "data").isAfter(vencimento)) valor = decimal(rj, "novoValor");
                }

                try {
                    mongo.insert(new Document("_id", proximoId(RECIBOS))
                            .append("locacaoId", idLocacao)
                            .append("ano", ym.getYear())
                            .append("mes", ym.getMonthValue())
                            .append("desconto", dec(BigDecimal.ZERO))
                            .append("acrescimo", dec(BigDecimal.ZERO))
                            .append("valorLiquido", dec(valor))
                            .append("formaPagamento", null)
                            .append("dataVencimento", vencimento.toString())
                            .append("dataPagamento", null)
                            .append("valorAluguel", dec(valor)), RECIBOS);
                    criados++;
                } catch (DuplicateKeyException _) {
                    // já existe (execução concorrente): índice único (locacaoId, ano, mes)
                }
            }
        }
        return criados;
    }
}
