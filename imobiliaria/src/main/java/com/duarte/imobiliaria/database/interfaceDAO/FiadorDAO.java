package com.duarte.imobiliaria.database.interfaceDAO;

import com.duarte.imobiliaria.model.Fiador;

import java.util.List;
import java.util.Optional;

public interface FiadorDAO {
    void salvar(Fiador fiador);
    Optional<Fiador> buscarPorId(int id);
    Optional<Fiador> buscarPorCpf(String cpf);
    boolean excluir(int id);
}
