package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.controllers.DTO.EnderecoDTO;
import com.duarte.imobiliaria.database.interfaceDAO.EnderecoDAO;
import com.duarte.imobiliaria.model.Endereco;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api/enderecos")
public class EnderecoController {

    private final EnderecoDAO enderecoDAO;

    public EnderecoController(EnderecoDAO enderecoDAO) {
        this.enderecoDAO = enderecoDAO;
    }

    @PostMapping
    public ResponseEntity<?> salvar(@Valid @RequestBody EnderecoDTO dto) {
        String complemento = dto.getComplemento() == null || dto.getComplemento().isBlank()
                ? null : dto.getComplemento().trim();

        Endereco endereco = new Endereco(
            dto.getCep(),
            dto.getEstado().toUpperCase(Locale.ROOT),
            dto.getCidade().trim(),
            dto.getBairro().trim(),
            dto.getNumero(),
            dto.getRua().trim(),
            complemento
        );

        enderecoDAO.salvar(endereco);

        return ResponseEntity.status(HttpStatus.CREATED).body(endereco);
    }

    @DeleteMapping("/{idEndereco}")
    public ResponseEntity<?> remover(@PathVariable int idEndereco) {
        enderecoDAO.remover(idEndereco);
        return ResponseEntity.ok().build();
    }
}