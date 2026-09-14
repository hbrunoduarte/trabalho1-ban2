package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.formsValidation.LoginForm;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @PostMapping
    public ResponseEntity<?> checkLoginInfo(@Valid @RequestBody LoginForm loginForm) {
        return ResponseEntity.ok().body("Login realizado");
    }

}
