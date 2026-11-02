package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.controllers.session.UsuarioLogado;
import jakarta.servlet.http.HttpSession;
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

    @GetMapping("/cadastro/corretor")
    public String abrirPaginaCadastroCorretor() {
        return "cadastro-corretor";
    }

    @GetMapping("/busca")
    public String abrirPaginaBusca() {
        return "pesquisa";
    }

    @GetMapping("/publicacao/{id}")
    public String abrirPaginaPublicacao() {
        return "detalhe";
    }

    @GetMapping("/perfil")
    public String abrirPaginaPerfil() {
        return "perfil";
    }

    @GetMapping("/painel")
    public String abrirPaginaPainel(HttpSession sessao) {

        UsuarioLogado u = (UsuarioLogado) sessao.getAttribute("usuario");
        if (u == null || u.tipo() == null) return "login";

        return switch (u.tipo()) {
            case TipoUsuarioLogado.CLIENTE -> "painel-cliente";
            case TipoUsuarioLogado.PROPRIETARIO -> "painel-proprietario";
            case TipoUsuarioLogado.CORRETOR -> "painel-corretor";
        };
    }
}