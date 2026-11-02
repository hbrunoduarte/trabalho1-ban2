package com.duarte.imobiliaria.controllers.DTO;

public class FiltroPublicacaoDTO {

    private String cep;
    private String estado;
    private String cidade;
    private String bairro;
    private Integer numero;
    private String rua;
    private Integer tipoImovel;
    private String titulo;
    private String tipoPublicacao;
    private Float valorMin;
    private Float valorMax;

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public Integer getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(Integer tipoImovel) {
        this.tipoImovel = tipoImovel;
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

    public Float getValorMin() {
        return valorMin;
    }

    public void setValorMin(Float valorMin) {
        this.valorMin = valorMin;
    }

    public Float getValorMax() {
        return valorMax;
    }

    public void setValorMax(Float valorMax) {
        this.valorMax = valorMax;
    }
}
