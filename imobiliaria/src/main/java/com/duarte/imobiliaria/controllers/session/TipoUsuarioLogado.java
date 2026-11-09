package com.duarte.imobiliaria.controllers.session;

import jakarta.servlet.http.HttpSession;

public enum TipoUsuarioLogado {
    CLIENTE,
    PROPRIETARIO,
    CORRETOR;

    public static TipoUsuarioLogado getTipo(int n) {
        if (n < 0 || n >= TipoUsuarioLogado.values().length)
            throw new IllegalArgumentException();
        return TipoUsuarioLogado.values()[n];
    }
}
