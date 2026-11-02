package com.duarte.imobiliaria.controllers.session;

import jakarta.servlet.http.HttpSession;

public record UsuarioLogado(Integer id, TipoUsuarioLogado tipo) {

    public static boolean isCorretor(HttpSession s) {
        UsuarioLogado u = (UsuarioLogado) s.getAttribute("usuario");
        return u != null && u.tipo() == TipoUsuarioLogado.CORRETOR;
    }

    public static boolean isProprietario(HttpSession s) {
        UsuarioLogado u = (UsuarioLogado) s.getAttribute("usuario");
        return u != null && u.tipo() == TipoUsuarioLogado.PROPRIETARIO;
    }

    public static boolean isCliente(HttpSession s) {
        UsuarioLogado u = (UsuarioLogado) s.getAttribute("usuario");
        return u != null && u.tipo() == TipoUsuarioLogado.CLIENTE;
    }

    public static int getIdUsuario(HttpSession s) {
        UsuarioLogado u = (UsuarioLogado) s.getAttribute("usuario");
        return u.id();
    }
}
