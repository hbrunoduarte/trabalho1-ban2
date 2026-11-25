package com.duarte.imobiliaria.database.mongo;

import com.duarte.imobiliaria.database.interfaceDAO.TipoImovelDAO;
import com.duarte.imobiliaria.model.TipoImovel;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Profile("mongo")
public class TipoImovelMongo extends MongoDaoBase implements TipoImovelDAO {

    public TipoImovelMongo(MongoTemplate mongo) {
        super(mongo);
    }

    @Override
    public List<TipoImovel> listarTodos() {
        return varios(TIPOS_IMOVEL, new Query().with(Sort.by("_id"))).stream()
                .map(d -> new TipoImovel(inteiro(d, "_id"), texto(d, "descricao")))
                .toList();
    }
}
