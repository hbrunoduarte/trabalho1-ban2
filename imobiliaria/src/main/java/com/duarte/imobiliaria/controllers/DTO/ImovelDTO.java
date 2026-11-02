package com.duarte.imobiliaria.controllers.DTO;

public class ImovelDTO {

    private Float areaTotal;
    private Integer qtdDormitorios;
    private Integer qtdBanheiros;
    private Integer qtdVagas;
    private Integer anoConstrucao;
    private String status;
    private String descricao;

    public Float getAreaTotal() {
        return areaTotal;
    }

    public void setAreaTotal(Float areaTotal) {
        this.areaTotal = areaTotal;
    }

    public Integer getQtdDormitorios() {
        return qtdDormitorios;
    }

    public void setQtdDormitorios(Integer qtdDormitorios) {
        this.qtdDormitorios = qtdDormitorios;
    }

    public Integer getQtdBanheiros() {
        return qtdBanheiros;
    }

    public void setQtdBanheiros(Integer qtdBanheiros) {
        this.qtdBanheiros = qtdBanheiros;
    }

    public Integer getQtdVagas() {
        return qtdVagas;
    }

    public void setQtdVagas(Integer qtdVagas) {
        this.qtdVagas = qtdVagas;
    }

    public Integer getAnoConstrucao() {
        return anoConstrucao;
    }

    public void setAnoConstrucao(Integer anoConstrucao) {
        this.anoConstrucao = anoConstrucao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
