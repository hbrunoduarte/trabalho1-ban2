package com.duarte.imobiliaria.database.interfaceDAO;

import com.duarte.imobiliaria.controllers.DTO.FiltroPublicacaoDTO;
import com.duarte.imobiliaria.controllers.DTO.PublicacaoDetalheDTO;
import com.duarte.imobiliaria.controllers.DTO.PublicacaoPesquisaDTO;
import com.duarte.imobiliaria.model.Publicacao;

import java.util.List;
import java.util.Optional;

public interface PublicacaoDAO {
    void salvar(Publicacao publicacao);
    Optional<Publicacao> buscarPorId(int idPublicacao);
    Optional<Publicacao> buscarPublicacaoImovel(int idImovel);
    Optional<PublicacaoDetalheDTO> buscarDetalhesPorId(int idPublicacao);
    List<PublicacaoPesquisaDTO> pesquisarTodas(int idProprietario);
    List<PublicacaoPesquisaDTO> listarAtivas();
    List<PublicacaoPesquisaDTO> filtrarPublicacoes(FiltroPublicacaoDTO filtro);
    void desativar(int idPublicacao);
    boolean excluir(int idPublicacao);
    void incrementarAcessos(int idPublicacao);
}
