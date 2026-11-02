package com.duarte.imobiliaria.model;

import com.duarte.imobiliaria.controllers.DTO.TelefoneDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class TelefoneFiador {

    private Integer idTelefone;
    private String telefone;
    private Fiador fiador;

    public TelefoneFiador() {
    }

    public TelefoneFiador(Integer idTelefone) {
        this.idTelefone = idTelefone;
    }

    public TelefoneFiador(Integer idTelefone, String telefone, Fiador fiador) {
        this.idTelefone = idTelefone;
        this.telefone = telefone;
        this.fiador = fiador;
    }

    public TelefoneFiador(String telefone, Fiador fiador) {
        this.telefone = telefone;
        this.fiador = fiador;
    }

    public TelefoneFiador(TelefoneDTO dto) {
        this.idTelefone = dto.getIdTelefone();
        this.telefone = dto.getTelefone();
    }

    public TelefoneFiador(Fiador fiador, TelefoneDTO dto) {
        this.fiador = fiador;
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

    public Fiador getFiador() {
        return fiador;
    }

    public void setFiador(Fiador fiador) {
        this.fiador = fiador;
    }

    @JsonIgnore
    public TelefoneDTO getDTO() {
        TelefoneDTO dto = new TelefoneDTO();
        dto.setIdTelefone(idTelefone);
        dto.setTelefone(telefone);
        return dto;
    }
}
