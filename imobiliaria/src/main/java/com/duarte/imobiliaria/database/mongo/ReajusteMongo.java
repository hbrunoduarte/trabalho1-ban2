package com.duarte.imobiliaria.database.mongo;

import com.duarte.imobiliaria.database.interfaceDAO.ReajusteDAO;
import com.duarte.imobiliaria.model.ContratoLocacao;
import com.duarte.imobiliaria.model.ReajusteAluguel;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Profile("mongo")
public class ReajusteMongo extends MongoDaoBase implements ReajusteDAO {

    public ReajusteMongo(MongoTemplate mongo) {
        super(mongo);
    }

    @Override
    public void salvar(ReajusteAluguel r) {
        int id = proximoId(REAJUSTES);
        LocalDate data = r.getData() != null ? r.getData() : LocalDate.now();
        mongo.insert(new Document("_id", id)
                .append("locacaoId", r.getContratoLocacao().getIdLocacao())
                .append("data", data.toString())
                .append("novoValor", dec(r.getNovoValor()))
                .append("indice", r.getIndiceUtilizado()), REAJUSTES);
        r.setIdReajuste(id);
    }

    @Override
    public List<ReajusteAluguel> buscarPorIdLocacao(int idLocacao) {
        return varios(REAJUSTES, por("locacaoId", idLocacao).with(Sort.by("data", "_id"))).stream().map(d -> {
            ReajusteAluguel r = new ReajusteAluguel();
            r.setIdReajuste(inteiro(d, "_id"));
            r.setData(data(d, "data"));
            r.setNovoValor(decimal(d, "novoValor"));
            r.setIndiceUtilizado(texto(d, "indice"));
            r.setContratoLocacao(new ContratoLocacao(inteiro(d, "locacaoId")));
            return r;
        }).toList();
    }
}
