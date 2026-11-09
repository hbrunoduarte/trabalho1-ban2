package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.controllers.DTO.FiltroPublicacaoDTO;
import com.duarte.imobiliaria.controllers.DTO.PublicacaoDetalheDTO;
import com.duarte.imobiliaria.controllers.DTO.PublicacaoPesquisaDTO;
import com.duarte.imobiliaria.controllers.DTO.VisitaAgendadaDTO;
import com.duarte.imobiliaria.database.interfaceDAO.FotoDAO;
import com.duarte.imobiliaria.database.interfaceDAO.PublicacaoDAO;
import com.duarte.imobiliaria.database.interfaceDAO.TelefoneDAO;
import com.duarte.imobiliaria.database.interfaceDAO.VisitaAgendadaDAO;
import com.duarte.imobiliaria.model.TelefoneProprietario;
import com.duarte.imobiliaria.model.VisitaAgendada;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/publicacoes")
public class PublicacaoController {

    private final PublicacaoDAO publicacaoDAO;
    private final FotoDAO fotoDAO;
    private final TelefoneDAO telefoneDAO;
    private final VisitaAgendadaDAO visitaAgendadaDAO;

    public PublicacaoController(PublicacaoDAO publicacaoDAO, FotoDAO fotoDAO, TelefoneDAO telefoneDAO, VisitaAgendadaDAO visitaAgendadaDAO) {
        this.publicacaoDAO = publicacaoDAO;
        this.fotoDAO = fotoDAO;
        this.telefoneDAO = telefoneDAO;
        this.visitaAgendadaDAO = visitaAgendadaDAO;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PublicacaoDetalheDTO> buscarPorId(@PathVariable int id) {

        Optional<PublicacaoDetalheDTO> r1 = publicacaoDAO.buscarDetalhesPorId(id);
        if (r1.isEmpty()) return ResponseEntity.noContent().build();

        PublicacaoDetalheDTO dto = r1.get();
        dto.setIdsFotos(fotoDAO.buscarIdsPorImovel(dto.getIdImovel()));

        List<TelefoneProprietario> tp = telefoneDAO.buscarPorIdImovel(dto.getIdImovel());
        dto.setTelefonesProprietario(tp.stream().map(TelefoneProprietario::getTelefone).toList());

        List<VisitaAgendada> visitas = visitaAgendadaDAO.listarPorImovel(dto.getIdImovel(), true, false);
        dto.setVisitas(visitas.stream().filter(VisitaAgendada::isPotencialContrato).map(v -> new VisitaAgendadaDTO(v.getData(), v.getHorario())).toList());

        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<PublicacaoPesquisaDTO>> pesquisar(FiltroPublicacaoDTO filtro) {

        filtro.setCep(vazioParaNulo(filtro.getCep()));
        filtro.setEstado(vazioParaNulo(filtro.getEstado()));
        filtro.setCidade(vazioParaNulo(filtro.getCidade()));
        filtro.setBairro(vazioParaNulo(filtro.getBairro()));
        filtro.setRua(vazioParaNulo(filtro.getRua()));
        filtro.setTitulo(vazioParaNulo(filtro.getTitulo()));
        filtro.setTipoPublicacao(vazioParaNulo(filtro.getTipoPublicacao()));

        return ResponseEntity.ok(publicacaoDAO.filtrarPublicacoes(filtro));
    }

    @PutMapping("/acesso/{id}")
    public ResponseEntity<Void> atualizarAcesso(@PathVariable int id) {
        publicacaoDAO.incrementarAcessos(id);
        return ResponseEntity.ok().build();
    }

    private static String vazioParaNulo(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
