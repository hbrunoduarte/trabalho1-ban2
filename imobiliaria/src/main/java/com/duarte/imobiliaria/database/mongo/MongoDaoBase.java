package com.duarte.imobiliaria.database.mongo;

import com.duarte.imobiliaria.model.*;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.Decimal128;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Base dos DAOs MongoDB: nomes das coleções, geração de ids (coleção "contadores"),
 * conversões Document <-> tipos Java e mapeadores compartilhados.
 *
 * Convenções do esquema:
 *  - ids são inteiros (_id), gerados por "contadores", como o SERIAL do Postgres;
 *  - datas são strings ISO "yyyy-MM-dd" e horários "HH:mm" (evita problemas de fuso);
 *  - dinheiro é Decimal128; binários (foto, comprovante) são BinData.
 */
abstract class MongoDaoBase {

    static final String TIPOS_IMOVEL = "tipos_imovel";
    static final String ENDERECOS = "enderecos";
    static final String PROPRIETARIOS = "proprietarios";
    static final String CLIENTES = "clientes";
    static final String CORRETORES = "corretores";
    static final String FIADORES = "fiadores";
    static final String TELEFONES = "telefones";
    static final String IMOVEIS = "imoveis";
    static final String FOTOS = "fotos";
    static final String PUBLICACOES = "publicacoes";
    static final String VISITAS = "visitas";
    static final String LOCACOES = "contratos_locacao";
    static final String VENDAS = "contratos_venda";
    static final String REAJUSTES = "reajustes";
    static final String RECIBOS = "recibos_aluguel";
    static final String CONTADORES = "contadores";

    static final String STATUS_ALUGADO = "alugado";
    static final String STATUS_VENDIDO = "vendido";

    protected final MongoTemplate mongo;

    protected MongoDaoBase(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    // ids

    protected int proximoId(String contador) {
        Document d = mongo.findAndModify(
                Query.query(Criteria.where("_id").is(contador)),
                new Update().inc("seq", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                Document.class, CONTADORES);
        return ((Number) d.get("seq")).intValue();
    }

    // consultas

    protected static Query porId(int id) {
        return Query.query(Criteria.where("_id").is(id));
    }

    protected static Query por(String campo, Object valor) {
        return Query.query(Criteria.where(campo).is(valor));
    }

    protected Optional<Document> um(String colecao, Query q) {
        return Optional.ofNullable(mongo.findOne(q, Document.class, colecao));
    }

    protected Optional<Document> umPorId(String colecao, int id) {
        return um(colecao, porId(id));
    }

    protected List<Document> varios(String colecao, Query q) {
        return mongo.find(q, Document.class, colecao);
    }

    protected boolean existe(String colecao, String campo, Object valor) {
        return mongo.exists(por(campo, valor), colecao);
    }

    // Simula a FK do Postgres: impede excluir algo que ainda é referenciado.
    protected void exigirSemReferencias(String descricao, int id, String[][] referencias) {
        for (String[] r : referencias) {
            if (existe(r[0], r[1], id)) {
                throw new DataIntegrityViolationException(
                        descricao + " " + id + " ainda é referenciado em " + r[0]);
            }
        }
    }

    // conversões

    static Integer inteiro(Document d, String k) {
        Object o = d.get(k);
        return o instanceof Number n ? n.intValue() : null;
    }

    static Float real(Document d, String k) {
        Object o = d.get(k);
        return o instanceof Number n ? n.floatValue() : null;
    }

    static Boolean bool(Document d, String k) {
        Object o = d.get(k);
        return o instanceof Boolean b ? b : null;
    }

    static String texto(Document d, String k) {
        Object o = d.get(k);
        return o == null ? null : o.toString();
    }

    static BigDecimal decimal(Document d, String k) {
        Object o = d.get(k);
        if (o instanceof Decimal128 x) return x.bigDecimalValue();
        if (o instanceof Number n) return new BigDecimal(n.toString());
        return null;
    }

    static Decimal128 dec(BigDecimal v) {
        return v == null ? null : new Decimal128(v);
    }

    static LocalDate data(Document d, String k) {
        Object o = d.get(k);
        return o == null ? null : LocalDate.parse(o.toString());
    }

    static LocalTime hora(Document d, String k) {
        Object o = d.get(k);
        return o == null ? null : LocalTime.parse(o.toString());
    }

    static byte[] bytes(Document d, String k) {
        Object o = d.get(k);
        if (o instanceof Binary b) return b.getData();
        if (o instanceof byte[] b) return b;
        return null;
    }

    static Binary binario(byte[] b) {
        return b == null ? null : new Binary(b);
    }

    // Só acrescenta o campo se houver valor (importa para índices únicos parciais)
    static Document ponha(Document d, String k, Object v) {
        if (v != null) d.append(k, v);
        return d;
    }

    // mapeadores

    static Endereco endereco(Document d) {
        Endereco e = new Endereco();
        e.setIdEndereco(inteiro(d, "_id"));
        e.setCep(texto(d, "cep"));
        e.setEstado(texto(d, "estado"));
        e.setCidade(texto(d, "cidade"));
        e.setBairro(texto(d, "bairro"));
        e.setNumero(inteiro(d, "numero"));
        e.setRua(texto(d, "rua"));
        e.setComplemento(texto(d, "complemento"));
        return e;
    }

    static Imovel imovel(Document d) {
        Imovel i = new Imovel();
        i.setIdImovel(inteiro(d, "_id"));
        i.setTipo(inteiro(d, "idTipoImovel"));
        i.setEndereco(new Endereco(inteiro(d, "enderecoId")));
        i.setProprietario(new Proprietario(inteiro(d, "proprietarioId")));
        i.setAreaTotal(real(d, "areaTotal"));
        i.setQtdDormitorios(inteiro(d, "qtdDormitorios"));
        i.setQtdBanheiros(inteiro(d, "qtdBanheiros"));
        i.setQtdVagas(inteiro(d, "qtdVagas"));
        i.setAnoConstrucao(inteiro(d, "anoConstrucao"));
        i.setDescricao(texto(d, "descricao"));
        i.setValor(decimal(d, "valor"));
        i.setStatus(texto(d, "status"));
        return i;
    }

    protected Proprietario proprietarioResumo(Integer id) {
        Proprietario p = new Proprietario(id);
        if (id == null) return p;
        umPorId(PROPRIETARIOS, id).ifPresent(d -> {
            p.setTipo(texto(d, "tipo").charAt(0));
            if (p.isFisico()) p.setNome(texto(d, "nome"));
            else p.setRazaoSocial(texto(d, "razaoSocial"));
        });
        return p;
    }

    protected Imovel imovelPainel(Integer idImovel) {
        Imovel i = new Imovel(idImovel);
        if (idImovel == null) return i;
        Document d = umPorId(IMOVEIS, idImovel).orElse(null);
        if (d == null) return i;

        i.setStatus(texto(d, "status"));
        i.setValor(decimal(d, "valor"));
        i.setProprietario(proprietarioResumo(inteiro(d, "proprietarioId")));

        Endereco e = new Endereco();
        Integer idEnd = inteiro(d, "enderecoId");
        if (idEnd != null) {
            Document de = umPorId(ENDERECOS, idEnd).orElse(null);
            if (de != null) {
                e.setEstado(texto(de, "estado"));
                e.setCidade(texto(de, "cidade"));
                e.setBairro(texto(de, "bairro"));
                e.setNumero(inteiro(de, "numero"));
                e.setRua(texto(de, "rua"));
                e.setComplemento(texto(de, "complemento"));
            }
        }
        i.setEndereco(e);
        return i;
    }

    protected Cliente clienteResumo(Integer id) {
        Cliente c = new Cliente(id);
        if (id != null) umPorId(CLIENTES, id).ifPresent(d -> c.setNome(texto(d, "nome")));
        return c;
    }

    protected List<Integer> idsImoveisDoProprietario(int idProprietario) {
        return varios(IMOVEIS, por("proprietarioId", idProprietario)).stream()
                .map(d -> inteiro(d, "_id")).toList();
    }

    // efeitos que eram triggers

    protected void arquivarVisitasDoImovel(int idImovel) {
        mongo.updateMulti(por("imovelId", idImovel), new Update().set("potencialContrato", false), VISITAS);
    }

    protected void desativarPublicacoesDoImovel(int idImovel) {
        mongo.updateMulti(por("imovelId", idImovel), new Update().set("ativa", false), PUBLICACOES);
    }

    protected void alterarStatusImovel(int idImovel, String status) {
        mongo.updateFirst(porId(idImovel), new Update().set("status", status), IMOVEIS);
    }
}
