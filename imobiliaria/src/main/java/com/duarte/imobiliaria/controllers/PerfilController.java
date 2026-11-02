package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.controllers.DTO.*;
import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.controllers.session.UsuarioLogado;
import com.duarte.imobiliaria.database.interfaceDAO.*;
import com.duarte.imobiliaria.model.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/perfil")
public class PerfilController {

    private final ClienteDAO clienteDAO;
    private final ProprietarioDAO proprietarioDAO;
    private final CorretorDAO corretorDAO;
    private final TelefoneDAO telefoneDAO;
    private final EnderecoDAO enderecoDAO;

    public PerfilController(ClienteDAO clienteDAO, ProprietarioDAO proprietarioDAO, CorretorDAO corretorDAO, TelefoneDAO telefoneDAO, EnderecoDAO enderecoDAO) {
        this.clienteDAO = clienteDAO;
        this.proprietarioDAO = proprietarioDAO;
        this.corretorDAO = corretorDAO;
        this.telefoneDAO = telefoneDAO;
        this.enderecoDAO = enderecoDAO;
    }

    @GetMapping
    public ResponseEntity<?> getPerfil(HttpSession sessao) {

        UsuarioLogado u = (UsuarioLogado) sessao.getAttribute("usuario");

        if (u == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acesso negado. Faça login primeiro.");

        PerfilDTO perfil = new PerfilDTO();

        if (u.tipo() == TipoUsuarioLogado.CLIENTE) {

            Optional<Cliente> resposta = clienteDAO.buscarPorId(u.id());
            if (resposta.isEmpty()) return ResponseEntity.internalServerError().build();
            Cliente c = resposta.get();

            perfil.setCliente(c.getDTO());

            List<TelefoneCliente> tels = telefoneDAO.listarDoCliente(u.id());
            perfil.getCliente().setTelefones(
                    tels.stream().map(TelefoneCliente::getTelefone).toList()
            );

            perfil.setTelefones(
                    tels.stream().map(TelefoneCliente::getDTO).toList()
            );

        } else if (u.tipo() == TipoUsuarioLogado.PROPRIETARIO) {

            Optional<Proprietario> resposta = proprietarioDAO.buscarPorId(u.id());
            if (resposta.isEmpty()) return ResponseEntity.internalServerError().build();
            Proprietario p = resposta.get();

            Optional<Endereco> respostaEndereco = enderecoDAO.buscarPorId(p.getEndereco().getIdEndereco());
            if (respostaEndereco.isEmpty()) return ResponseEntity.internalServerError().build();

            perfil.setProprietario(p.getDTO());
            perfil.setEndereco(respostaEndereco.get().getDTO());

            List<TelefoneProprietario> tels = telefoneDAO.listarDoProprietario(u.id());
            perfil.getProprietario().setTelefones(
                    tels.stream().map(TelefoneProprietario::getTelefone).toList()
            );
            perfil.setTelefones(
                    tels.stream().map(TelefoneProprietario::getDTO).toList()
            );

        } else {

            Optional<Corretor> resposta = corretorDAO.buscarPorId(u.id());
            if (resposta.isEmpty()) return ResponseEntity.internalServerError().build();

            perfil.setCorretor(resposta.get().getDTO());
        }

        return ResponseEntity.ok(perfil);
    }

    @PostMapping
    public ResponseEntity<?> salvarPerfil(@Valid @RequestBody PerfilDTO perfil, HttpSession sessao) {

        UsuarioLogado u = (UsuarioLogado) sessao.getAttribute("usuario");
        if (u == null) return ResponseEntity.internalServerError().body("Um problema interno impossibilitou o salvamento dos dados. Tente novamente mais tarde.");

        if (u.tipo() == TipoUsuarioLogado.CLIENTE) {

            clienteDAO.salvar(new Cliente(u.id(), perfil.getCliente()));

        } else if (u.tipo() == TipoUsuarioLogado.PROPRIETARIO) {

            Optional<Endereco> resposta = enderecoDAO.buscarEndereco(Integer.parseInt(perfil.getEndereco().getCep()), perfil.getEndereco().getNumero());
            if (resposta.isEmpty()) return ResponseEntity.badRequest().build();

            ProprietarioDTO dto = perfil.getProprietario();
            dto.setIdEndereco(resposta.get().getIdEndereco());
            proprietarioDAO.salvar(new Proprietario(u.id(), dto));

        } else {
            corretorDAO.salvar(new Corretor(u.id(), perfil.getCorretor()));
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/telefone")
    public ResponseEntity<?> salvarTelefone(@RequestBody List<@Valid TelefoneDTO> telefones, HttpSession sessao) {

        UsuarioLogado u = (UsuarioLogado) sessao.getAttribute("usuario");
        if (u == null) return ResponseEntity.internalServerError().body("Um problema interno impossibilitou o salvamento dos dados. Tente novamente mais tarde.");

        if (u.tipo() == TipoUsuarioLogado.CLIENTE) {

            telefoneDAO.salvarDoCliente(
                telefones.stream().map(t -> new TelefoneCliente(new Cliente(u.id()), t)).toList()
            );

        } else if (u.tipo() == TipoUsuarioLogado.PROPRIETARIO) {

            telefoneDAO.salvarDoProprietario(
                telefones.stream().map(t -> new TelefoneProprietario(new Proprietario(u.id()), t)).toList()
            );

        } else return ResponseEntity.badRequest().build();

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/telefone/{idTelefone}")
    public ResponseEntity<?> removerTelefone(@PathVariable Integer idTelefone, HttpSession sessao) {

        UsuarioLogado u = (UsuarioLogado) sessao.getAttribute("usuario");
        if (u == null) return ResponseEntity.internalServerError().body("Um problema interno impossibilitou o salvamento dos dados. Tente novamente mais tarde.");

        if (u.tipo() == TipoUsuarioLogado.CLIENTE) {
            telefoneDAO.excluirDoCliente(idTelefone);

        } else if (u.tipo() == TipoUsuarioLogado.PROPRIETARIO) {
            telefoneDAO.excluirDoProprietario(idTelefone);

        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.ok().build();
    }

    @PostMapping("/endereco")
    public ResponseEntity<?> salvarEndereco(@Valid @RequestBody EnderecoDTO enderecoDTO, HttpSession sessao) {

        UsuarioLogado u = (UsuarioLogado) sessao.getAttribute("usuario");
        if (u == null) return ResponseEntity.internalServerError().body("Um problema interno impossibilitou o salvamento dos dados. Tente novamente mais tarde.");

        if (u.tipo() != TipoUsuarioLogado.PROPRIETARIO) return ResponseEntity.badRequest().build();

        Endereco e = new Endereco(enderecoDTO);
        enderecoDAO.salvar(e);
        proprietarioDAO.atualizarEndereco(u.id(), e.getIdEndereco());

        return ResponseEntity.ok().build();
    }
}
