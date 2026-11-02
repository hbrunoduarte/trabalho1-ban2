package com.duarte.imobiliaria.database.interfaceDAO;

import com.duarte.imobiliaria.model.Foto;

import java.util.List;
import java.util.Optional;

public interface FotoDAO {
    void salvar(Foto foto);
    void salvar(List<Foto> fotos);
    Optional<Foto> buscarPorId(int id);
    List<Integer> buscarIdsPorImovel(int idImovel);
}
