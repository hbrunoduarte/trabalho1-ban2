package com.duarte.imobiliaria.model;

import com.duarte.imobiliaria.controllers.DTO.PublicacaoCadastroDTO;

import java.time.LocalDate;

public class Publicacao {

    private Integer idPublicacao;
    private Integer idImovel;
    private Integer idFotoCapa;
    private String titulo;
    private String tipoPublicacao;
    private Boolean ativa;
    private LocalDate data;
    private Integer acessos;

    public Publicacao() {}

    public Publicacao(Integer idPublicacao) {
        this.idPublicacao = idPublicacao;
    }

    public Publicacao(Integer idPublicacao, Integer idImovel, Integer idFotoCapa, String titulo, String tipoPublicacao, Boolean ativa, LocalDate data, Integer acessos) {
        this.idPublicacao = idPublicacao;
        this.idImovel = idImovel;
        this.idFotoCapa = idFotoCapa;
        this.titulo = titulo;
        this.tipoPublicacao = tipoPublicacao;
        this.ativa = ativa;
        this.data = data;
        this.acessos = acessos;
    }

    public Publicacao(PublicacaoCadastroDTO dto) {
        idImovel = dto.idImovel();
        titulo = dto.titulo();
        tipoPublicacao = dto.tipoPublicacao();
    }

    public Publicacao(Integer idFotoCapa, PublicacaoCadastroDTO dto) {
        this(dto);
        this.idFotoCapa = idFotoCapa;
    }

    public Integer getIdPublicacao() {
        return idPublicacao;
    }

    public void setIdPublicacao(Integer idPublicacao) {
        this.idPublicacao = idPublicacao;
    }

    public Integer getIdImovel() {
        return idImovel;
    }

    public void setIdImovel(Integer idImovel) {
        this.idImovel = idImovel;
    }

    public Integer getIdFotoCapa() {
        return idFotoCapa;
    }

    public void setIdFotoCapa(Integer idFotoCapa) {
        this.idFotoCapa = idFotoCapa;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipoPublicacao() {
        return tipoPublicacao;
    }

    public void setTipoPublicacao(String tipoPublicacao) {
        this.tipoPublicacao = tipoPublicacao;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Integer getAcessos() {
        return acessos;
    }

    public void setAcessos(Integer acessos) {
        this.acessos = acessos;
    }
}
