package com.duarte.imobiliaria;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Controlador {

    @GetMapping("/perfil")
    public String mostrarPerfil(Model model) {
        // Simulando um dado que veio do Banco de Dados
        model.addAttribute("nomeDoUsuario", "Carlos");
        
        // Retorna o nome do arquivo HTML na pasta templates (sem a extensão .html)
        return "perfil"; 
    }
}
