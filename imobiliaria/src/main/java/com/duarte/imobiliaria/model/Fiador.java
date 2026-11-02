package com.duarte.imobiliaria.model;

import com.duarte.imobiliaria.controllers.DTO.FiadorCadastroDTO;

public class Fiador {

    private Integer idFiador;
    private String cpf;
    private String nome;
    private byte[] comprovanteRenda;
    private Endereco endereco;

    public Fiador() {
    }

    public Fiador(Integer idFiador) {
        this.idFiador = idFiador;
    }

    public Fiador(Integer idFiador, String cpf, String nome, byte[] comprovanteRenda, Endereco endereco) {
        this.idFiador = idFiador;
        this.cpf = cpf;
        this.nome = nome;
        this.comprovanteRenda = comprovanteRenda;
        this.endereco = endereco;
    }

    public Fiador(FiadorCadastroDTO dto) {
        if (dto == null) return;
        cpf = dto.getCpf();
        nome = dto.getNome();
        comprovanteRenda = dto.getComprovanteRenda();
        endereco = new Endereco(dto.getEndereco());
    }

    public Integer getIdFiador() {
        return idFiador;
    }

    public void setIdFiador(Integer idFiador) {
        this.idFiador = idFiador;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public byte[] getComprovanteRenda() {
        return comprovanteRenda;
    }

    public void setComprovanteRenda(byte[] comprovanteRenda) {
        this.comprovanteRenda = comprovanteRenda;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

}
