package com.duarte.imobiliaria.controllers.DTO;

import com.duarte.imobiliaria.formsValidation.groups.ValidacaoAvancada;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoBasica;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoFormato;
import com.duarte.imobiliaria.formsValidation.util.ValidacaoConstants;
import com.duarte.imobiliaria.formsValidation.validationAnnotations.Cnpj;
import com.duarte.imobiliaria.formsValidation.validationAnnotations.Cpf;
import com.duarte.imobiliaria.formsValidation.validationAnnotations.Senha;
import com.duarte.imobiliaria.formsValidation.validationAnnotations.TipoPessoaConsistente;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.*;

import java.util.List;

@TipoPessoaConsistente(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
@GroupSequence({ValidacaoBasica.class, ValidacaoFormato.class, ValidacaoAvancada.class, ProprietarioDTO.class})
public class ProprietarioDTO {

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Pattern(regexp = "[FJ]", message = "{cadastro.tipopessoa.invalido}", groups = ValidacaoFormato.class)
    private String tipo;

    @Pattern(regexp = ValidacaoConstants.CPF_DIGITOS_PATTERN, message = "{cadastro.cpf.formato}", groups = ValidacaoFormato.class)
    @Cpf(message = "{cadastro.cpf.invalido}", groups = ValidacaoAvancada.class)
    private String cpf;

    @Pattern(regexp = ValidacaoConstants.CNPJ_DIGITOS_PATTERN, message = "{cadastro.cnpj.formato}", groups = ValidacaoFormato.class)
    @Cnpj(message = "{cadastro.cnpj.invalido}", groups = ValidacaoAvancada.class)
    private String cnpj;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Email(message = "{cadastro.email.formato}", groups = ValidacaoFormato.class)
    @Size(max = ValidacaoConstants.EMAIL_MAX_SIZE, message = "{cadastro.email.size}", groups = ValidacaoAvancada.class)
    private String email;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Senha(message = "{cadastro.senha.formato}", groups = ValidacaoFormato.class)
    @Size(min = ValidacaoConstants.SENHA_MIN_SIZE, max = ValidacaoConstants.SENHA_MAX_SIZE, groups = ValidacaoAvancada.class)
    private String senha;

    @Size(min = ValidacaoConstants.NOME_MIN_SIZE, max = ValidacaoConstants.NOME_MAX_DB_SIZE, message = "{cadastro.nome.size}", groups = ValidacaoAvancada.class)
    private String nome;

    @Size(min = ValidacaoConstants.NOME_MIN_SIZE, max = ValidacaoConstants.NOME_MAX_DB_SIZE, message = "{cadastro.razaosocial.size}", groups = ValidacaoAvancada.class)
    private String razaoSocial;

    @NotNull(message = "{cadastro.endereco.obrigatorio}", groups = ValidacaoBasica.class)
    @Positive(message = "{cadastro.endereco.obrigatorio}", groups = ValidacaoAvancada.class)
    private Integer idEndereco;

    @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Min(value = 0, message = "{cadastro.banco.invalido}", groups = ValidacaoAvancada.class)
    @Max(value = 9, message = "{cadastro.banco.invalido}", groups = ValidacaoAvancada.class)
    private Integer banco;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Pattern(regexp = ValidacaoConstants.AGENCIA_PATTERN, message = "{cadastro.agencia.formato}", groups = ValidacaoFormato.class)
    private String agencia;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Pattern(regexp = ValidacaoConstants.CONTA_PATTERN, message = "{cadastro.conta.formato}", groups = ValidacaoFormato.class)
    private String conta;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Pattern(regexp = ValidacaoConstants.DV_PATTERN, message = "{cadastro.dv.formato}", groups = ValidacaoFormato.class)
    private String dv;

    @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    private Boolean isCorrente;

    @NotEmpty(message = "{cadastro.telefones.insuficientes}", groups = ValidacaoBasica.class)
    @Size(max = ValidacaoConstants.TELEFONES_MAX_SIZE, message = "{cadastro.telefones.size}", groups = ValidacaoAvancada.class)
    private List<
            @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
            @Pattern(regexp = ValidacaoConstants.TELEFONE_PATTERN, message = "{cadastro.telefone.formato}", groups = ValidacaoFormato.class)
                    String> telefones;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
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

    public Integer getIdEndereco() {
        return idEndereco;
    }

    public void setIdEndereco(Integer idEndereco) {
        this.idEndereco = idEndereco;
    }

    public Integer getBanco() {
        return banco;
    }

    public void setBanco(Integer banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getDv() {
        return dv;
    }

    public void setDv(String dv) {
        this.dv = dv;
    }

    public Boolean getCorrente() {
        return isCorrente;
    }

    public void setCorrente(Boolean corrente) {
        isCorrente = corrente;
    }

    public boolean isPessoaFisica() {
        return tipo.equals("F");
    }

    public List<String> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<
            @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
            @Pattern(regexp = ValidacaoConstants.TELEFONE_PATTERN, message = "{cadastro.telefone.formato}", groups = ValidacaoFormato.class)
                    String> telefones) {
        this.telefones = telefones;
    }
}