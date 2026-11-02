package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.database.interfaceDAO.ContratoVendaDAO;
import com.duarte.imobiliaria.model.ContratoVenda;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contratos/venda")
public class ContratoVendaController {

    private final ContratoVendaDAO contratoVendaDAO;

    public ContratoVendaController(ContratoVendaDAO contratoVendaDAO) {
        this.contratoVendaDAO = contratoVendaDAO;
    }

    @GetMapping
    public ResponseEntity<List<ContratoVenda>> listarTodos() {
        return ResponseEntity.ok(contratoVendaDAO.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoVenda> buscarPorId(@PathVariable int id) {
        Optional<ContratoVenda> contrato = contratoVendaDAO.buscarPorId(id);
        return contrato.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ContratoVenda> registrarVenda(@RequestBody ContratoVenda contrato) {
        // As restrições financeiras e a atualização de status do imóvel
        // são garantidas pelas triggers.
        contratoVendaDAO.salvar(contrato);
        return ResponseEntity.status(HttpStatus.CREATED).body(contrato);
    }
}
