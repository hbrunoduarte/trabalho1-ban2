package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.formsValidation.CadastroForm;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cadastro")
public class CadastroController {

    @PostMapping
    public ResponseEntity<?> checkCadastroInfo(@Valid @RequestBody CadastroForm cadastroForm) {
        return ResponseEntity.ok().body("Cadastro realizado");
    }

}
