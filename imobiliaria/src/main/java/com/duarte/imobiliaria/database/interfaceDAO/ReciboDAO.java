package com.duarte.imobiliaria.database.interfaceDAO;

import com.duarte.imobiliaria.controllers.DTO.FiltroReciboDTO;
import com.duarte.imobiliaria.model.ReciboAluguel;

import java.util.List;

public interface ReciboDAO {
    List<ReciboAluguel> filtrar(FiltroReciboDTO filtro);
    List<ReciboAluguel> buscarPorLocacao(int idLocacao);
}
