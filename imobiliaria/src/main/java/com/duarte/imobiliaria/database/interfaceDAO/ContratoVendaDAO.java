package com.duarte.imobiliaria.database.interfaceDAO;

import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.model.ContratoVenda;

import java.util.List;
import java.util.Optional;

public interface ContratoVendaDAO {
    void salvar(ContratoVenda contrato);
    Optional<ContratoVenda> buscarPorId(int id);
    List<ContratoVenda> buscarPorId(int id, TipoUsuarioLogado t, boolean maisRecente);
    List<ContratoVenda> listarTodos();
}
