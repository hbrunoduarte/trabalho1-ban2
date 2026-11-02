package com.duarte.imobiliaria.model;

import com.duarte.imobiliaria.controllers.DTO.TelefoneDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class TelefoneProprietario {

    private Integer idTelefone;
    private String telefone;
    private Proprietario proprietario;

    public TelefoneProprietario() {}

    public TelefoneProprietario(Integer idTelefone) {
        this.idTelefone = idTelefone;
    }

    public TelefoneProprietario(Integer idTelefone, String telefone) {
        this.idTelefone = idTelefone;
        this.telefone = telefone;
    }

    public TelefoneProprietario(String telefone, Proprietario proprietario) {
        this.telefone = telefone;
        this.proprietario = proprietario;
    }

    public TelefoneProprietario(Integer idTelefone, String telefone, Proprietario proprietario) {
        this.idTelefone = idTelefone;
        this.telefone = telefone;
        this.proprietario = proprietario;
    }

    public TelefoneProprietario(TelefoneDTO dto) {
        this.idTelefone = dto.getIdTelefone();
        this.telefone = dto.getTelefone();
    }

    public TelefoneProprietario(Proprietario proprietario, TelefoneDTO dto) {
        this.proprietario = proprietario;
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

    public Proprietario getProprietario() {
        return proprietario;
    }

    public void setProprietario(Proprietario proprietario) {
        this.proprietario = proprietario;
    }

    @JsonIgnore
    public TelefoneDTO getDTO() {
        TelefoneDTO dto = new TelefoneDTO();
        dto.setIdTelefone(idTelefone);
        dto.setTelefone(telefone);
        return dto;
    }
}
