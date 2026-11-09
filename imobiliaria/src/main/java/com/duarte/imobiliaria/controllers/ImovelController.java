package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.database.interfaceDAO.ImovelDAO;
import com.duarte.imobiliaria.database.interfaceDAO.TipoImovelDAO;
import com.duarte.imobiliaria.model.Imovel;
import com.duarte.imobiliaria.model.TipoImovel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/imoveis")
public class ImovelController {

    private final ImovelDAO imovelDAO;
    private final TipoImovelDAO tipoImovelDAO;

    public ImovelController(ImovelDAO imovelDAO, TipoImovelDAO tipoImovelDAO) {
        this.imovelDAO = imovelDAO;
        this.tipoImovelDAO = tipoImovelDAO;
    }

    @GetMapping("/maior_preco/")
    public ResponseEntity<?> getMaiorPreco() {
        try {
            Optional<Float> resposta = imovelDAO.buscarMaiorPreco(null);
            return resposta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
        } catch (SQLException _) {
            return ResponseEntity.badRequest().body(java.util.Map.of("geral", "Não foi possível obter o maior preço"));
        }
    }

    @GetMapping("/maior_preco/{tipoPublicacao}")
    public ResponseEntity<?> getMaiorPreco(@PathVariable String tipoPublicacao) {
        try {
            Optional<Float> resposta = imovelDAO.buscarMaiorPreco(tipoPublicacao);
            return resposta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
        } catch (SQLException _) {
            return ResponseEntity.badRequest().body(java.util.Map.of("geral", "Não foi possível obter o maior preço"));
        }
    }

    @GetMapping("/tipos")
    public ResponseEntity<List<TipoImovel>> getTiposImoveis() {
        return ResponseEntity.ok(tipoImovelDAO.listarTodos());
    }
}
