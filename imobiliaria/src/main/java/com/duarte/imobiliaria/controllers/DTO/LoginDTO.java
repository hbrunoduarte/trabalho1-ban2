package com.duarte.imobiliaria.controllers.DTO;

import com.duarte.imobiliaria.formsValidation.groups.ValidacaoAvancada;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoBasica;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoFormato;
import com.duarte.imobiliaria.formsValidation.validationAnnotations.LoginIdentificador;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.*;

@GroupSequence({ValidacaoBasica.class, ValidacaoFormato.class, ValidacaoAvancada.class, LoginDTO.class})
public class LoginDTO {

    @NotNull(message = "{erro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    private Integer tipo;

    @NotBlank(message = "{erro.login.obrigatorio}", groups = ValidacaoBasica.class)
    @LoginIdentificador(groups = ValidacaoFormato.class)
    private String login;

    @NotBlank(message = "{erro.senha.obrigatorio}", groups = ValidacaoBasica.class)
    private String senha;

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
