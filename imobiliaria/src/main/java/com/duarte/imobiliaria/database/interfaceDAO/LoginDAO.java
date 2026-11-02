package com.duarte.imobiliaria.database.interfaceDAO;

import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.controllers.session.UsuarioLogado;

import java.util.Optional;

public interface LoginDAO {
    Optional<UsuarioLogado> autenticar(String login, String senha, TipoUsuarioLogado t);
}
