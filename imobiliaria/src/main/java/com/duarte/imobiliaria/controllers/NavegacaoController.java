package com.duarte.imobiliaria.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavegacaoController {

    @GetMapping("/")
    public String abrirPaginaBoasVindas() {
        return "boasvindas";
    }

    @GetMapping("/login")
    public String abrirPaginaLogin() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String abrirPaginaCadastro() {
        return "cadastro";
    }

}