package com.duarte.imobiliaria.database.mongo;

import com.duarte.imobiliaria.database.interfaceDAO.ImovelDAO;
import com.duarte.imobiliaria.model.Imovel;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongo")
public class ImovelMongo extends MongoDaoBase implements ImovelDAO {

    public ImovelMongo(MongoTemplate mongo) {
        super(mongo);
    }

    @Override
    public void salvar(Imovel i) {
        boolean novo = i.getIdImovel() == null;
        int id = novo ? proximoId(IMOVEIS) : i.getIdImovel();

        Document d = new Document("_id", id)
                .append("idTipoImovel", i.getTipo())
                .append("enderecoId", i.getEndereco().getIdEndereco())
                .append("proprietarioId", i.getProprietario().getIdProprietario())
                .append("areaTotal", i.getAreaTotal() == null ? null : i.getAreaTotal().doubleValue())
                .append("qtdDormitorios", i.getQtdDormitorios())
                .append("qtdBanheiros", i.getQtdBanheiros())
                .append("qtdVagas", i.getQtdVagas())
                .append("anoConstrucao", i.getAnoConstrucao())
                .append("descricao", i.getDescricao())
                .append("valor", dec(i.getValor()))
                .append("status", i.getStatus());

        if (novo) {
            mongo.insert(d, IMOVEIS);
            i.setIdImovel(id);
        } else {
            mongo.save(d, IMOVEIS);
        }
    }

    @Override
    public Optional<Imovel> buscarPorId(int id) {
        return umPorId(IMOVEIS, id).map(MongoDaoBase::imovel);
    }

    @Override
    public Optional<Imovel> buscarPorEndereco(int cep, int numero) {
        Query qEnd = Query.query(Criteria.where("cep").is(String.format("%08d", cep)).and("numero").is(numero));
        List<Integer> idsEndereco = varios(ENDERECOS, qEnd).stream().map(d -> inteiro(d, "_id")).toList();
        if (idsEndereco.isEmpty()) return Optional.empty();
        return um(IMOVEIS, Query.query(Criteria.where("enderecoId").in(idsEndereco))).map(MongoDaoBase::imovel);
    }

    @Override
    public List<Imovel> buscarPorProprietario(int idProprietario) {
        return varios(IMOVEIS, por("proprietarioId", idProprietario).with(Sort.by("_id"))).stream()
                .map(MongoDaoBase::imovel).toList();
    }

    @Override
    public Optional<Float> buscarMaiorPreco(String tipoPublicacao) throws SQLException {
        Query q = new Query().with(Sort.by(Sort.Direction.DESC, "valor")).limit(1);

        if (tipoPublicacao != null && !tipoPublicacao.isBlank()) {
            if (tipoPublicacao.equals("V")) q.addCriteria(Criteria.where("status").is(STATUS_VENDA));
            else if (tipoPublicacao.equals("L")) q.addCriteria(Criteria.where("status").is(STATUS_LOCACAO));
            else throw new SQLException("Tipo de publicação inválido: " + tipoPublicacao);
        }

        // como no Postgres (MAX sem linhas -> 0)
        return Optional.of(um(IMOVEIS, q).map(d -> decimal(d, "valor").floatValue()).orElse(0f));
    }

    @Override
    public Optional<List<Imovel>> buscarPorTipo(int idTipoImovel) {
        List<Imovel> r = varios(IMOVEIS, por("idTipoImovel", idTipoImovel)).stream().map(MongoDaoBase::imovel).toList();
        return r.isEmpty() ? Optional.empty() : Optional.of(r);
    }

    @Override
    public List<Imovel> listarTodos() {
        return varios(IMOVEIS, new Query().with(Sort.by("_id"))).stream().map(MongoDaoBase::imovel).toList();
    }

    @Override
    public boolean excluir(int id) {
        exigirSemReferencias("Imóvel", id, new String[][]{
                {FOTOS, "imovelId"}, {PUBLICACOES, "imovelId"}, {VISITAS, "imovelId"},
                {LOCACOES, "imovelId"}, {VENDAS, "imovelId"}});
        return mongo.remove(porId(id), IMOVEIS).getDeletedCount() > 0;
    }
}
