package com.duarte.imobiliaria.model;

import com.duarte.imobiliaria.controllers.DTO.CorretorDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;

public class Corretor {

    private Integer idCorretor;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String cpf;
    private Float percentualComissao;
    private LocalDate dataAdmissao;
    private String creci;

    public Corretor() {}

    public Corretor(Integer idCorretor) {
        this.idCorretor = idCorretor;
    }

    public Corretor(Integer idCorretor, String nome, String email, String senha, String telefone, String cpf, Float percentualComissao, LocalDate dataAdmissao, String creci) {
        this.idCorretor = idCorretor;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.cpf = cpf;
        this.percentualComissao = percentualComissao;
        this.dataAdmissao = dataAdmissao;
        this.creci = creci;
    }

    public Corretor(CorretorDTO dto) {
        this.nome = dto.getNome();
        this.email = dto.getEmail();
        this.senha = dto.getSenha();
        this.telefone = dto.getTelefone();
        this.cpf = dto.getCpf();
        this.percentualComissao = dto.getPercentualComissao();
        this.dataAdmissao = dto.getDataAdmissao();
        this.creci = dto.getCreci();
    }

    public Corretor(Integer idCorretor, CorretorDTO dto) {
        this.idCorretor = idCorretor;
        this(dto);
    }

    public Integer getIdCorretor() {
        return idCorretor;
    }

    public void setIdCorretor(Integer idCorretor) {
        this.idCorretor = idCorretor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Float getPercentualComissao() {
        return percentualComissao;
    }

    public void setPercentualComissao(Float percentualComissao) {
        this.percentualComissao = percentualComissao;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public String getCreci() {
        return creci;
    }

    public void setCreci(String creci) {
        this.creci = creci;
    }

    @JsonIgnore
    public CorretorDTO getDTO() {
        CorretorDTO dto = new CorretorDTO();

        dto.setNome(nome);
        dto.setDataAdmissao(dataAdmissao);
        dto.setCpf(cpf);
        dto.setEmail(email);
        dto.setSenha(senha);
        dto.setTelefone(telefone);
        dto.setCreci(creci);
        dto.setPercentualComissao(percentualComissao);

        return dto;
    }
}
