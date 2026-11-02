package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.controllers.DTO.LoginDTO;
import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.controllers.session.UsuarioLogado;
import com.duarte.imobiliaria.database.interfaceDAO.LoginDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final LoginDAO loginDAO;

    public LoginController(LoginDAO loginDAO) {
        this.loginDAO = loginDAO;
    }

    @PostMapping
    public ResponseEntity<?> checkLoginInfo(@Valid @RequestBody LoginDTO dto,
                                            HttpServletRequest request) {

        Optional<UsuarioLogado> resposta = loginDAO.autenticar(dto.getLogin(), dto.getSenha(), TipoUsuarioLogado.getTipo(dto.getTipo()));
        if (resposta.isEmpty())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("geral", "Identificador ou senha incorretos"));

        UsuarioLogado usuario = resposta.get();

        request.getSession().invalidate();
        HttpSession sessao = request.getSession(true);
        sessao.setAttribute("usuario", usuario);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession sessao) {
        sessao.invalidate();
        return ResponseEntity.noContent().build();
    }

}
