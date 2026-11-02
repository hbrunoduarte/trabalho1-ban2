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

    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        return ResponseEntity.ok(clienteDAO.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable int id) {
        Optional<Cliente> cliente = clienteDAO.buscarPorId(id);
        return cliente.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<Cliente> buscarPorCpf(@PathVariable String cpf) {
        Optional<Cliente> cliente = clienteDAO.buscarPorCpf(cpf);
        return cliente.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> salvar(@Valid @RequestBody ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setRendaMensal(Double.parseDouble(dto.getRendaMensal().replace(',', '.')));
        cliente.setProfissao(dto.getProfissao().trim());
        cliente.setTipoInteressado(dto.getTipoInteressado());
        cliente.setCpf(dto.getCpf());
        cliente.setEmail(dto.getEmail().trim());
        cliente.setSenha(dto.getSenha().trim());
        cliente.setNome(dto.getNome().trim());

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
