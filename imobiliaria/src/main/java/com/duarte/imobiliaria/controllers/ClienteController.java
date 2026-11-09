package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.controllers.DTO.ClienteDTO;
import com.duarte.imobiliaria.database.interfaceDAO.ClienteDAO;
import com.duarte.imobiliaria.database.interfaceDAO.TelefoneDAO;
import com.duarte.imobiliaria.model.Cliente;
import com.duarte.imobiliaria.model.TelefoneCliente;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteDAO clienteDAO;
    private final TelefoneDAO telefoneDAO;

    public ClienteController(ClienteDAO clienteDAO, TelefoneDAO telefoneDAO) {
        this.clienteDAO = clienteDAO;
        this.telefoneDAO = telefoneDAO;
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> salvar(@Valid @RequestBody ClienteDTO dto) {

        Cliente cliente = new Cliente(dto);

        clienteDAO.salvar(cliente);
        telefoneDAO.salvarDoCliente(dto.getTelefones().stream()
                                    .map(t -> new TelefoneCliente(t, cliente)).toList());

        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id) {
        return clienteDAO.excluir(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
