package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.controllers.DTO.CorretorDTO;
import com.duarte.imobiliaria.database.interfaceDAO.CorretorDAO;
import com.duarte.imobiliaria.model.Corretor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/corretores")
public class CorretorController {

    private final CorretorDAO corretorDAO;

    public CorretorController(CorretorDAO corretorDAO) {
        this.corretorDAO = corretorDAO;
    }

    @PostMapping
    public ResponseEntity<?> salvar(@Valid @RequestBody CorretorDTO dto) {

        Corretor c = new Corretor(dto);

        corretorDAO.salvar(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(c);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id) {
        return corretorDAO.excluir(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}