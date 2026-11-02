package com.duarte.imobiliaria.model;

public class TipoImovel {

    private Integer idTipoImovel;
    private String descricao;

    public TipoImovel() {
    }

    public TipoImovel(Integer idTipoImovel) {
        this.idTipoImovel = idTipoImovel;
    }

    public TipoImovel(Integer idTipoImovel, String descricao) {
        this.idTipoImovel = idTipoImovel;
        this.descricao = descricao;
    }

    public Integer getIdTipoImovel() {
        return idTipoImovel;
    }

    public void setIdTipoImovel(Integer idTipoImovel) {
        this.idTipoImovel = idTipoImovel;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
