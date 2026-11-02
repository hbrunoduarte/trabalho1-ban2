package com.duarte.imobiliaria.database.interfaceDAO;

import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.model.ContratoLocacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContratoLocacaoDAO {
    void salvar(ContratoLocacao contrato);
    Optional<ContratoLocacao> buscarPorId(int id);
    List<ContratoLocacao> buscarPorId(int id, TipoUsuarioLogado t, boolean maisRecente);
    boolean rescindir(int idLocacao, LocalDate dataRescisao, BigDecimal multa, String motivo);
    List<ContratoLocacao> listarTodos();
}
