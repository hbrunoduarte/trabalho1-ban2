package com.duarte.imobiliaria.database.mongo;

import com.duarte.imobiliaria.database.interfaceDAO.FotoDAO;
import com.duarte.imobiliaria.model.Foto;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongo")
public class FotoMongo extends MongoDaoBase implements FotoDAO {

    public FotoMongo(MongoTemplate mongo) {
        super(mongo);
    }

    @Override
    public void salvar(Foto foto) {
        int id = proximoId(FOTOS);
        mongo.insert(new Document("_id", id)
                .append("imovelId", foto.getIdImovel())
                .append("tipo", foto.getTipo())
                .append("imagem", binario(foto.getImagem())), FOTOS);
        foto.setIdFoto(id);
    }

    @Override
    public void salvar(List<Foto> fotos) {
        if (fotos == null) return;
        for (Foto f : fotos) salvar(f);
    }

    @Override
    public Optional<Foto> buscarPorId(int id) {
        return umPorId(FOTOS, id).map(d -> {
            Foto f = new Foto();
            f.setIdFoto(inteiro(d, "_id"));
            f.setTipo(texto(d, "tipo"));
            f.setImagem(bytes(d, "imagem"));
            f.setIdImovel(inteiro(d, "imovelId"));
            return f;
        });
    }

    @Override
    public List<Integer> buscarIdsPorImovel(int idImovel) {
        var q = por("imovelId", idImovel).with(Sort.by("_id"));
        q.fields().include("_id");
        return varios(FOTOS, q).stream().map(d -> inteiro(d, "_id")).toList();
    }
}
