package com.duarte.imobiliaria.model;

import com.duarte.imobiliaria.controllers.DTO.ImovelPainelDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

public class Imovel {

    private Integer idImovel;
    private Integer tipo;
    private Endereco endereco;
    private Proprietario proprietario;
    private Float areaTotal;
    private Integer qtdDormitorios;
    private Integer qtdBanheiros;
    private Integer qtdVagas;
    private Integer anoConstrucao;
    private String descricao;
    private BigDecimal valor;
    private String status;

    public Imovel() {}

    public Imovel(Integer idImovel) {
        this.idImovel = idImovel;
    }

    public Imovel(Integer idImovel, Integer tipo, Endereco endereco, Proprietario proprietario, Float areaTotal, Integer qtdDormitorios, Integer qtdBanheiros, Integer qtdVagas, Integer anoConstrucao, String descricao, BigDecimal valor, String status) {
        this.idImovel = idImovel;
        this.tipo = tipo;
        this.endereco = endereco;
        this.proprietario = proprietario;
        this.areaTotal = areaTotal;
        this.qtdDormitorios = qtdDormitorios;
        this.qtdBanheiros = qtdBanheiros;
        this.qtdVagas = qtdVagas;
        this.anoConstrucao = anoConstrucao;
        this.descricao = descricao;
        this.valor = valor;
        this.status = status;
    }

    public Integer getIdImovel() {
        return idImovel;
    }

    public void setIdImovel(Integer idImovel) {
        this.idImovel = idImovel;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Proprietario getProprietario() {
        return proprietario;
    }

    public void setProprietario(Proprietario proprietario) {
        this.proprietario = proprietario;
    }

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String tipoContrato() {
        if (isVenda())
            return "VENDA";
        else if (isLocacao())
            return "LOCAÇÃO";
        else return "";
    }

    @JsonIgnore
    public boolean isVenda() {
        return status != null && status.equalsIgnoreCase("disponível para venda");
    }

    @JsonIgnore
    public boolean isLocacao() {
        return status != null && status.equalsIgnoreCase("disponível para locação");
    }

    @JsonIgnore
    public ImovelPainelDTO getDTO(String tipoImovel, Integer idPublicacao) {
        return new ImovelPainelDTO(
                idImovel,
                tipoImovel,
                endereco.montarEndereco(),
                areaTotal,
                qtdDormitorios,
                qtdBanheiros,
                qtdVagas,
                anoConstrucao,
                descricao,
                valor,
                status,
                idPublicacao
        );
    }

    @Override
    public String toString() {
        return "Imovel{" +
                "idImovel=" + idImovel +
                ", tipo=" + tipo +
                ", endereco=" + endereco +
                ", proprietario=" + proprietario +
                ", areaTotal=" + areaTotal +
                ", qtdDormitorios=" + qtdDormitorios +
                ", qtdBanheiros=" + qtdBanheiros +
                ", qtdVagas=" + qtdVagas +
                ", anoConstrucao=" + anoConstrucao +
                ", descricao='" + descricao + '\'' +
                ", valor=" + valor +
                ", status='" + status + '\'' +
                '}';
    }
}
