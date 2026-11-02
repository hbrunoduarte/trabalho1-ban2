package com.duarte.imobiliaria.database.interfaceDAO;

import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.model.VisitaAgendada;

import java.util.List;
import java.util.Optional;

public interface VisitaAgendadaDAO {
    void salvar(VisitaAgendada visita);
    Optional<VisitaAgendada> buscarPorId(int idVisita);
    List<VisitaAgendada> buscarPorId(int id, boolean apenasAtuais, boolean maisRecente, TipoUsuarioLogado t);
    List<VisitaAgendada> listarPotenciaisContratos(int idCorretor);
    List<VisitaAgendada> listarPorImovel(int idImovel, boolean apenasAtuais, boolean maisRecente);
    void arquivar(int id);
    boolean excluir(int id);
}
