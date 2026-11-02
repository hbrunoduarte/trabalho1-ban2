package com.duarte.imobiliaria.controllers.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AutenticacaoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            return true;
        }

        HttpSession sessao = request.getSession(false);
        boolean logado = sessao != null && sessao.getAttribute("usuario") != null;

        if (!logado) {
            if (request.getRequestURI().startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"geral\":\"É preciso estar logado para acessar este recurso\"}");
            } else {
                response.sendRedirect("/login");
            }
            return false;
        }

        UsuarioLogado usuario = (UsuarioLogado) sessao.getAttribute("usuario");
        String uri = request.getRequestURI();

        if (uri.startsWith("/painel/corretor") && usuario.tipo() != TipoUsuarioLogado.CORRETOR) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"geral\":\"Acesso negado. Área exclusiva para corretores.\"}");
            return false;
        }

        if (uri.startsWith("/painel/proprietario") && usuario.tipo() != TipoUsuarioLogado.PROPRIETARIO) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"geral\":\"Acesso negado. Área exclusiva para proprietários.\"}");
            return false;
        }

        return true;
    }
}
