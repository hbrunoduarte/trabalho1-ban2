package com.duarte.imobiliaria.controllers.DTO;

import com.duarte.imobiliaria.formsValidation.groups.ValidacaoAvancada;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoBasica;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoFormato;
import com.duarte.imobiliaria.formsValidation.util.ValidacaoConstants;
import com.duarte.imobiliaria.formsValidation.validationAnnotations.Cpf;
import com.duarte.imobiliaria.formsValidation.validationAnnotations.Senha;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@GroupSequence({ValidacaoBasica.class, ValidacaoFormato.class, ValidacaoAvancada.class, CorretorDTO.class})
public class CorretorDTO {

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Size(min = ValidacaoConstants.NOME_MIN_SIZE, max = ValidacaoConstants.NOME_MAX_DB_SIZE, message = "{cadastro.nome.size}", groups = ValidacaoAvancada.class)
    private String nome;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Email(message = "{cadastro.email.formato}", groups = ValidacaoFormato.class)
    @Size(max = ValidacaoConstants.EMAIL_MAX_SIZE, message = "{cadastro.email.size}", groups = ValidacaoAvancada.class)
    private String email;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Senha(message = "{cadastro.senha.formato}", groups = ValidacaoFormato.class)
    @Size(min = ValidacaoConstants.SENHA_MIN_SIZE, max = ValidacaoConstants.SENHA_MAX_SIZE, groups = ValidacaoAvancada.class)
    private String senha;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Pattern(regexp = ValidacaoConstants.TELEFONE_PATTERN, message = "{cadastro.telefone.formato}", groups = ValidacaoFormato.class)
    private String telefone;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Pattern(regexp = ValidacaoConstants.CPF_DIGITOS_PATTERN, message = "{cadastro.cpf.formato}", groups = ValidacaoFormato.class)
    @Cpf(message = "{cadastro.cpf.invalido}", groups = ValidacaoAvancada.class)
    private String cpf;

    @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @DecimalMin(value = "0.0", message = "{cadastro.comissao.formato}", groups = ValidacaoAvancada.class)
    @DecimalMax(value = "1.0", message = "{cadastro.comissao.formato}", groups = ValidacaoAvancada.class)
    private Float percentualComissao;

    @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @PastOrPresent(message = "{cadastro.admissao.data}", groups = ValidacaoAvancada.class)
    private LocalDate dataAdmissao;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Pattern(regexp = ValidacaoConstants.CRECI_PATTERN, message = "{cadastro.creci.formato}", groups = ValidacaoFormato.class)
    private String creci;

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
}
