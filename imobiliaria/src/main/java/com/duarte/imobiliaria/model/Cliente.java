package com.duarte.imobiliaria.model;

import com.duarte.imobiliaria.controllers.DTO.ClienteDTO;
import org.springframework.expression.spel.CodeFlow;

public class Cliente {
    private Integer idCliente;
    private Double rendaMensal;
    private String profissao;
    private Integer tipoInteressado;
    private String cpf;
    private String email;
    private String senha;
    private String nome;

    public Cliente() {}

    public Cliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Cliente(Integer idCliente, Double rendaMensal, String profissao, Integer tipoInteressado, String cpf, String email, String senha, String nome) {
        this.idCliente = idCliente;
        this.rendaMensal = rendaMensal;
        this.profissao = profissao;
        this.tipoInteressado = tipoInteressado;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.nome = nome;
    }

    public Cliente(ClienteDTO dto) {
        nome = dto.getNome().trim();
        cpf = dto.getCpf();
        email = dto.getEmail().trim();
        senha = dto.getSenha().trim();
        tipoInteressado = dto.getTipoInteressado();
        profissao = dto.getProfissao().trim();
        rendaMensal = Double.parseDouble(dto.getRendaMensal().replace(',', '.'));
    }

    public Cliente(Integer idCliente, ClienteDTO dto) {
        this.idCliente = idCliente;
        this(dto);
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Double getRendaMensal() {
        return rendaMensal;
    }

    public void setRendaMensal(Double rendaMensal) {
        this.rendaMensal = rendaMensal;
    }

    public String getProfissao() {
        return profissao;
    }

    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }

    public Integer getTipoInteressado() {
        return tipoInteressado;
    }

    public void setTipoInteressado(Integer tipoInteressado) {
        this.tipoInteressado = tipoInteressado;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ClienteDTO getDTO() {
        ClienteDTO dto = new ClienteDTO();

        dto.setNome(nome);
        dto.setCpf(cpf);
        dto.setEmail(email);
        dto.setProfissao(profissao);
        dto.setRendaMensal(String.valueOf(rendaMensal));
        dto.setSenha(senha);
        dto.setTipoInteressado(tipoInteressado);

        return dto;
    }
}
