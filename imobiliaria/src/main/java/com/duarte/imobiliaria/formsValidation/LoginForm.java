package com.duarte.imobiliaria.formsValidation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoginForm {

    private boolean isInquilino;

    @NotNull(message = "{erro.login.obrigatorio}")
    @NotBlank(message = "{erro.login.obrigatorio}")
    private String login;

    @NotNull(message = "{erro.senha.obrigatorio}")
    @NotBlank(message = "{erro.senha.obrigatorio}")
    private String senha;

    public boolean isInquilino() {
        return isInquilino;
    }

    public void setInquilino(boolean inquilino) {
        isInquilino = inquilino;
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

    @Override
    public String toString() {
        return "LoginForm{" +
                "isInquilino=" + isInquilino +
                ", login='" + login + '\'' +
                ", senha='" + senha + '\'' +
                '}';
    }
}
