package com.duarte.imobiliaria.model;

import com.duarte.imobiliaria.controllers.DTO.EnderecoDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Endereco {

    private Integer idEndereco;
    private String cep;
    private String estado;
    private String cidade;
    private String bairro;
    private Integer numero;
    private String rua;
    private String complemento;

    public Endereco() {
    }

    public Endereco(Integer idEndereco) {
        this.idEndereco = idEndereco;
    }

    public Endereco(String cep, String estado, String cidade, String bairro, Integer numero, String rua, String complemento) {
        this.cep = cep;
        this.estado = estado;
        this.cidade = cidade;
        this.bairro = bairro;
        this.numero = numero;
        this.rua = rua;
        this.complemento = complemento;
    }

    public Endereco(Integer idEndereco, String cep, String estado, String cidade, String bairro, Integer numero, String rua, String complemento) {
        this.idEndereco = idEndereco;
        this.cep = cep;
        this.estado = estado;
        this.cidade = cidade;
        this.bairro = bairro;
        this.numero = numero;
        this.rua = rua;
        this.complemento = complemento;
    }

    public Endereco(EnderecoDTO dto) {
        cep = dto.getCep();
        estado = dto.getEstado();
        cidade = dto.getCidade();
        bairro = dto.getBairro();
        rua = dto.getRua();
        numero = dto.getNumero();
        complemento = dto.getComplemento();
    }

    public Integer getIdEndereco() {
        return idEndereco;
    }

    public void setIdEndereco(Integer idEndereco) {
        this.idEndereco = idEndereco;
    }

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

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    @JsonIgnore
    public EnderecoDTO getDTO() {
        EnderecoDTO dto = new EnderecoDTO();

        dto.setCep(cep);
        dto.setEstado(estado);
        dto.setCidade(cidade);
        dto.setBairro(bairro);
        dto.setRua(rua);
        dto.setNumero(numero);
        dto.setComplemento(complemento);

        return dto;
    }

    @JsonIgnore
    public String montarEndereco() {
        StringBuilder sb = new StringBuilder(rua).append(", ").append(numero);
        if (complemento != null && !complemento.isBlank()) sb.append(", ").append(complemento);
        return sb.append(", ").append(bairro)
                .append(", ").append(cidade).append(" - ").append(estado)
                .toString();
    }
}