package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.controllers.DTO.VisitaAgendadaDTO;
import com.duarte.imobiliaria.database.interfaceDAO.CorretorDAO;
import com.duarte.imobiliaria.database.interfaceDAO.VisitaAgendadaDAO;
import com.duarte.imobiliaria.model.Corretor;
import com.duarte.imobiliaria.model.VisitaAgendada;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/visitas")
public class VisitaAgendadaController {

    private final VisitaAgendadaDAO visitaDAO;
    private final CorretorDAO corretorDAO;

    public VisitaAgendadaController(VisitaAgendadaDAO visitaDAO, CorretorDAO corretorDAO) {
        this.visitaDAO = visitaDAO;
        this.corretorDAO = corretorDAO;
    }

    @GetMapping("/imovel/{idImovel}")
    public ResponseEntity<List<VisitaAgendadaDTO>> listarPorImovel(@PathVariable int idImovel) {

        List<VisitaAgendada> visitas = visitaDAO.listarPorImovel(idImovel, false, false);
        if (visitas.isEmpty()) return ResponseEntity.noContent().build();

        List<VisitaAgendadaDTO> dto = visitas.stream().map(v -> new VisitaAgendadaDTO(v.getData(), v.getHorario())).toList();

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<VisitaAgendada> agendarVisita(@RequestBody VisitaAgendada visita) {

        Optional<Corretor> resposta = corretorDAO.proximoParaVisita(visita);
        if (resposta.isEmpty()) return ResponseEntity.internalServerError().build();

        visita.setCorretor(resposta.get());
        visitaDAO.salvar(visita);

        return ResponseEntity.status(HttpStatus.CREATED).body(visita);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarVisita(@PathVariable int id) {
        if (visitaDAO.excluir(id)) return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }
}