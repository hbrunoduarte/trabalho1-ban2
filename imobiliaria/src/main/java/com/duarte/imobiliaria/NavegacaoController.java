package com.duarte.imobiliaria;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavegacaoController {

    // Essa linha "escuta" o href="/preLogin" que veio do link
    @GetMapping("/preLogin")
    public String abrirPaginaPreLogin() {
        return "preLogin"; 
    }

    @GetMapping("/login")
    public String abrirPaginaLogin() {
        return "login";
    }

}