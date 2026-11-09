package com.duarte.imobiliaria.config;

import com.duarte.imobiliaria.controllers.session.AutenticacaoInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class ConfiguracaoWeb implements WebMvcConfigurer {

    @Autowired
    private AutenticacaoInterceptor autenticacaoInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(autenticacaoInterceptor)
                .addPathPatterns("/painel/**", "/api/**", "/busca", "/perfil")
                .excludePathPatterns(
                        "/api/login", "/api/login/logout",
                        "/api/clientes", "/api/proprietarios", "/api/corretores",
                        "/api/proprietarios/validar",
                        "/api/enderecos/**", "/api/imoveis/tipos");
    }
}