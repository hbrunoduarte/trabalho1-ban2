package com.duarte.imobiliaria.database.interfaceDAO;

import com.duarte.imobiliaria.model.ContratoLocacao;
import com.duarte.imobiliaria.model.ReajusteAluguel;

import java.util.List;

public interface ReajusteDAO {
    void salvar(ReajusteAluguel r);
    List<ReajusteAluguel> buscarPorIdLocacao(int idLocacao);
}
