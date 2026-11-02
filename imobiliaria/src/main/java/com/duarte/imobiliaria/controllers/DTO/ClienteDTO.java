package com.duarte.imobiliaria.controllers.DTO;

import com.duarte.imobiliaria.formsValidation.groups.ValidacaoAvancada;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoBasica;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoFormato;
import com.duarte.imobiliaria.formsValidation.util.ValidacaoConstants;
import com.duarte.imobiliaria.formsValidation.validationAnnotations.Cpf;
import com.duarte.imobiliaria.formsValidation.validationAnnotations.Senha;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.*;

import java.util.List;

@GroupSequence({ValidacaoBasica.class, ValidacaoFormato.class, ValidacaoAvancada.class, ClienteDTO.class})
public class ClienteDTO {

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Pattern(regexp = ValidacaoConstants.RENDA_PATTERN, message = "{cadastro.rendamensal.formato}", groups = ValidacaoFormato.class)
    private String rendaMensal;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Size(max = ValidacaoConstants.PROFISSAO_MAX_DB_SIZE, message = "{cadastro.profissao.size}", groups = ValidacaoAvancada.class)
    private String profissao;

    @NotNull(message = "{cadastro.tipoimovel.obrigatorio}", groups = ValidacaoBasica.class)
    @Positive(message = "{cadastro.tipoimovel.obrigatorio}", groups = ValidacaoAvancada.class)
    private Integer tipoInteressado;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Pattern(regexp = ValidacaoConstants.CPF_DIGITOS_PATTERN, message = "{cadastro.cpf.formato}", groups = ValidacaoFormato.class)
    @Cpf(message = "{cadastro.cpf.invalido}", groups = ValidacaoAvancada.class)
    private String cpf;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Email(message = "{cadastro.email.formato}", groups = ValidacaoFormato.class)
    @Size(max = ValidacaoConstants.EMAIL_MAX_SIZE, message = "{cadastro.email.size}", groups = ValidacaoAvancada.class)
    private String email;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Senha(message = "{cadastro.senha.formato}", groups = ValidacaoFormato.class)
    @Size(min = ValidacaoConstants.SENHA_MIN_SIZE, max = ValidacaoConstants.SENHA_MAX_SIZE, groups = ValidacaoAvancada.class)
    private String senha;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Size(min = ValidacaoConstants.NOME_MIN_SIZE, max = ValidacaoConstants.NOME_MAX_DB_SIZE, message = "{cadastro.nome.size}", groups = ValidacaoAvancada.class)
    private String nome;

    @NotEmpty(message = "{cadastro.telefones.insuficientes}", groups = ValidacaoBasica.class)
    @Size(max = ValidacaoConstants.TELEFONES_MAX_SIZE, message = "{cadastro.telefones.size}", groups = ValidacaoAvancada.class)
    private List<
            @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
            @Pattern(regexp = ValidacaoConstants.TELEFONE_PATTERN, message = "{cadastro.telefone.formato}", groups = ValidacaoFormato.class)
                    String> telefones;

    public String getRendaMensal() {
        return rendaMensal;
    }

    public void setRendaMensal(String rendaMensal) {
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

    public List<
            @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
            @Pattern(regexp = ValidacaoConstants.TELEFONE_PATTERN, message = "{cadastro.telefone.formato}", groups = ValidacaoFormato.class)
                    String> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<
            @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
            @Pattern(regexp = ValidacaoConstants.TELEFONE_PATTERN, message = "{cadastro.telefone.formato}", groups = ValidacaoFormato.class)
                    String> telefones) {
        this.telefones = telefones;
    }
}
