package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.controllers.DTO.ProprietarioDTO;
import com.duarte.imobiliaria.database.interfaceDAO.ProprietarioDAO;
import com.duarte.imobiliaria.database.interfaceDAO.TelefoneDAO;
import com.duarte.imobiliaria.model.CodigoBancos;
import com.duarte.imobiliaria.model.Endereco;
import com.duarte.imobiliaria.model.Proprietario;
import com.duarte.imobiliaria.model.TelefoneProprietario;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/proprietarios")
public class ProprietarioController {

    private final ProprietarioDAO proprietarioDAO;
    private final TelefoneDAO telefoneDAO;
    private final Validator validator;

    public ProprietarioController(ProprietarioDAO proprietarioDAO, TelefoneDAO telefoneDAO, Validator validator) {
        this.proprietarioDAO = proprietarioDAO;
        this.telefoneDAO = telefoneDAO;
        this.validator = validator;
    }

    @Transactional
    @PostMapping
    public ResponseEntity<?> salvar(@Valid @RequestBody ProprietarioDTO dto) {

        Proprietario p = new Proprietario(dto);

        proprietarioDAO.salvar(p);
        telefoneDAO.salvarDoProprietario(dto.getTelefones().stream()
                                        .map(t -> new TelefoneProprietario(t, p)).toList());

        return ResponseEntity.status(HttpStatus.CREATED).body(p);
    }

    @PostMapping("/validar")
    public ResponseEntity<Map<String, String>> validar(@RequestBody ProprietarioDTO dto) {

        if (dto.getIdEndereco() == null) {
            dto.setIdEndereco(1);
        }

        Set<ConstraintViolation<ProprietarioDTO>> violacoes = validator.validate(dto);
        if (violacoes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        Map<String, String> erros = new LinkedHashMap<>();
        for (ConstraintViolation<ProprietarioDTO> v : violacoes) {
            String caminho = v.getPropertyPath().toString();

            int sufixo = caminho.indexOf(".<");
            if (sufixo >= 0) {
                caminho = caminho.substring(0, sufixo);
            }

            erros.putIfAbsent(caminho.isEmpty() ? "geral" : caminho, v.getMessage());
        }

        return ResponseEntity.badRequest().body(erros);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id) {
        return proprietarioDAO.excluir(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
}
