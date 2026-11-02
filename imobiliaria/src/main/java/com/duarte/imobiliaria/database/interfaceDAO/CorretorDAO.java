package com.duarte.imobiliaria.database.interfaceDAO;

import com.duarte.imobiliaria.model.Corretor;
import com.duarte.imobiliaria.model.VisitaAgendada;

import java.util.List;
import java.util.Optional;

public interface CorretorDAO {
    void salvar(Corretor corretor);
    Optional<Corretor> proximoParaVisita(VisitaAgendada v);
    Optional<Corretor> buscarPorId(int id);
    Optional<Corretor> buscarPorCpf(String cpf);
    Optional<Corretor> buscarPorCreci(String creci);
    List<Corretor> listarTodos();
    boolean excluir(int id);
}
