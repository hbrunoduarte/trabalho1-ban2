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

    @GetMapping
    public ResponseEntity<List<Corretor>> listarTodos() {
        return ResponseEntity.ok(corretorDAO.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Corretor> buscarPorId(@PathVariable int id) {
        Optional<Corretor> corretor = corretorDAO.buscarPorId(id);
        return corretor.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{creci}")
    public ResponseEntity<Corretor> buscarPorCreci(@PathVariable String creci) {
        Optional<Corretor> corretor = corretorDAO.buscarPorCreci(creci);
        return corretor.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> salvar(@Valid @RequestBody CorretorDTO dto) {

        Corretor c = new Corretor();
        c.setNome(dto.getNome());
        c.setCpf(dto.getCpf());
        c.setCreci(dto.getCreci());
        c.setEmail(dto.getEmail());
        c.setSenha(dto.getSenha());
        c.setDataAdmissao(dto.getDataAdmissao());
        c.setTelefone(dto.getTelefone());
        c.setPercentualComissao(dto.getPercentualComissao());

        corretorDAO.salvar(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(c);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id) {
        return corretorDAO.excluir(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}