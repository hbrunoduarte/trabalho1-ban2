package com.duarte.imobiliaria.model;

import com.duarte.imobiliaria.controllers.DTO.TelefoneDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class TelefoneCliente {

    private Integer idTelefone;
    private String telefone;
    private Cliente cliente;

    public TelefoneCliente() {
    }

    public TelefoneCliente(Integer idTelefone) {
        this.idTelefone = idTelefone;
    }

    public TelefoneCliente(Integer idTelefone, String telefone, Cliente cliente) {
        this.idTelefone = idTelefone;
        this.telefone = telefone;
        this.cliente = cliente;
    }

    public TelefoneCliente(String telefone, Cliente cliente) {
        this.telefone = telefone;
        this.cliente = cliente;
    }

    public TelefoneCliente(TelefoneDTO dto) {
        this.idTelefone = dto.getIdTelefone();
        this.telefone = dto.getTelefone();
    }

    public TelefoneCliente(Cliente cliente, TelefoneDTO dto) {
        this.cliente = cliente;
        this(dto);
    }

    public Integer getIdTelefone() {
        return idTelefone;
    }

    public void setIdTelefone(Integer idTelefone) {
        this.idTelefone = idTelefone;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @JsonIgnore
    public TelefoneDTO getDTO() {
        TelefoneDTO dto = new TelefoneDTO();
        dto.setIdTelefone(idTelefone);
        dto.setTelefone(telefone);
        return dto;
    }
}
