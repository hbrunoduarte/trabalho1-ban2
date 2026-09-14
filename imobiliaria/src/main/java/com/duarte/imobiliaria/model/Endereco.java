package com.duarte.imobiliaria.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Endereco {

    @NotNull
    @NotBlank
    private String rua;

    private int numero;

    @NotNull
    @NotBlank
    private String bairro;

    private String complemento;

    @NotNull
    @NotBlank
    private String cidade;

    @NotNull
    @NotBlank
    private String estado;

    @NotNull

    private int cep;

}
