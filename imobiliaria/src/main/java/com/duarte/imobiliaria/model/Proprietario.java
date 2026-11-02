package com.duarte.imobiliaria.model;

import com.duarte.imobiliaria.controllers.DTO.ProprietarioDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Proprietario {

    private static final String REGEX_DADOS_BANCARIOS = "Bco:(\\d+)\\s+Ag:([a-zA-Z0-9]+)\\s+Cc:([a-zA-Z0-9]+)-([a-zA-Z0-9]+)";
    private static final String FORM_DADOS_BANCARIOS = "Bco:%d Ag:%s Cc:%s-%s";

    private Integer idProprietario;
    private Character tipo; // 'F' ou 'J'
    private String cpf;
    private String cnpj;
    private String email;
    private String senha;
    private String dadosBancarios;
    private String nome;
    private String razaoSocial;
    private Endereco endereco;

    public Proprietario() {}

    public Proprietario(Integer idProprietario) {
        this.idProprietario = idProprietario;
    }

    public Proprietario(Integer idProprietario, Character tipo, String cpf, String cnpj, String email, String senha, String dadosBancario, String nome, String razaoSocial, Endereco endereco) {
        this.idProprietario = idProprietario;
        this.tipo = tipo;
        this.cpf = cpf;
        this.cnpj = cnpj;
        this.email = email;
        this.senha = senha;
        this.dadosBancarios = dadosBancario;
        this.nome = nome;
        this.razaoSocial = razaoSocial;
        this.endereco = endereco;
    }

    public Proprietario(ProprietarioDTO dto) {
        this.tipo = dto.getTipo().charAt(0);
        this.cpf = dto.getCpf();
        this.cnpj = dto.getCnpj();
        this.email = dto.getEmail();
        this.senha = dto.getSenha();
        this.dadosBancarios = String.format(FORM_DADOS_BANCARIOS, CodigoBancos.getCodigoByOrdinal(dto.getBanco()), dto.getAgencia(), dto.getConta(), dto.getDv());
        this.nome = dto.getNome();
        this.razaoSocial = dto.getRazaoSocial();
        this.endereco = new Endereco(dto.getIdEndereco());
    }

    public Proprietario(Integer idProprietario, ProprietarioDTO dto) {
        this.idProprietario = idProprietario;
        this(dto);
    }

    public Integer getIdProprietario() {
        return idProprietario;
    }

    public void setIdProprietario(Integer idProprietario) {
        this.idProprietario = idProprietario;
    }

    public Character getTipo() {
        return tipo;
    }

    public void setTipo(Character tipo) {
        this.tipo = tipo;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
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

    public String getDadosBancarios() {
        return dadosBancarios;
    }

    public void setDadosBancarios(String dadosBancarios) {
        this.dadosBancarios = dadosBancarios;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    @JsonIgnore
    public ProprietarioDTO getDTO() {
        ProprietarioDTO dto = new ProprietarioDTO();
        dto.setNome(nome);
        dto.setRazaoSocial(razaoSocial);
        dto.setTipo(String.valueOf(tipo));
        dto.setCpf(cpf);
        dto.setCnpj(cnpj);
        dto.setEmail(email);
        dto.setSenha(senha);
        dto.setIdEndereco(endereco.getIdEndereco());

        Matcher matcher = Pattern.compile(REGEX_DADOS_BANCARIOS).matcher(dadosBancarios);
        if (!matcher.find()) throw new IllegalArgumentException("Dados bancários mal formatados: " + dadosBancarios);

        dto.setBanco(
            CodigoBancos.getOrdinalByCodigo(Integer.valueOf(matcher.group(1)))
        );
        dto.setAgencia(matcher.group(2));
        dto.setConta(matcher.group(3));
        dto.setDv(matcher.group(4));

        return dto;
    }

    @JsonIgnore
    public boolean isFisico() {
        return tipo != null && tipo == 'F';
    }
}
