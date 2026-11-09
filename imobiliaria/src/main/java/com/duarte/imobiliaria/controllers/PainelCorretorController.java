package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.controllers.DTO.ContratoPainelDTO;
import com.duarte.imobiliaria.controllers.DTO.FirmarLocacaoDTO;
import com.duarte.imobiliaria.controllers.DTO.FirmarVendaDTO;
import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.database.interfaceDAO.ClienteDAO;
import com.duarte.imobiliaria.database.interfaceDAO.ContratoLocacaoDAO;
import com.duarte.imobiliaria.database.interfaceDAO.ContratoVendaDAO;
import com.duarte.imobiliaria.database.interfaceDAO.CorretorDAO;
import com.duarte.imobiliaria.database.interfaceDAO.EnderecoDAO;
import com.duarte.imobiliaria.database.interfaceDAO.FiadorDAO;
import com.duarte.imobiliaria.database.interfaceDAO.ImovelDAO;
import com.duarte.imobiliaria.database.interfaceDAO.TelefoneDAO;
import com.duarte.imobiliaria.database.interfaceDAO.VisitaAgendadaDAO;
import com.duarte.imobiliaria.model.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.duarte.imobiliaria.controllers.session.UsuarioLogado.getIdUsuario;
import static com.duarte.imobiliaria.controllers.session.UsuarioLogado.isCorretor;

@RestController
@RequestMapping("/api/painel/corretor")
public class PainelCorretorController {

    private static final String GARANTIA_FIADOR = "FIADOR";
    private static final String GARANTIA_CAUCAO = "DEPOSITO CAUCAO";
    private static final String STATUS_CONTRATO_ATIVO = "ATIVO";

    private final VisitaAgendadaDAO visitaDAO;
    private final ImovelDAO imovelDAO;
    private final CorretorDAO corretorDAO;
    private final ContratoLocacaoDAO contratoLocacaoDAO;
    private final ContratoVendaDAO contratoVendaDAO;
    private final EnderecoDAO enderecoDAO;
    private final FiadorDAO fiadorDAO;
    private final TelefoneDAO telefoneDAO;
    private final MessageSource messageSource;

    public PainelCorretorController(VisitaAgendadaDAO visitaDAO, ImovelDAO imovelDAO, CorretorDAO corretorDAO, ContratoLocacaoDAO contratoLocacaoDAO, ContratoVendaDAO contratoVendaDAO, EnderecoDAO enderecoDAO, FiadorDAO fiadorDAO, TelefoneDAO telefoneDAO, MessageSource messageSource) {
        this.visitaDAO = visitaDAO;
        this.imovelDAO = imovelDAO;
        this.corretorDAO = corretorDAO;
        this.contratoLocacaoDAO = contratoLocacaoDAO;
        this.contratoVendaDAO = contratoVendaDAO;
        this.enderecoDAO = enderecoDAO;
        this.fiadorDAO = fiadorDAO;
        this.telefoneDAO = telefoneDAO;
        this.messageSource = messageSource;
    }

    @GetMapping("/validarvisitas")
    public ResponseEntity<?> getPotenciaisContratos(HttpSession sessao) {

        if (!isCorretor(sessao)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<VisitaAgendada> visitas = visitaDAO.listarPotenciaisContratos(getIdUsuario(sessao));
        if (visitas.isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok(visitas.stream().map(VisitaAgendada::getPainelDTO).toList());
    }

    @GetMapping("/visitas")
    public ResponseEntity<?> getTodasVisitas(HttpSession sessao) {

        if (!isCorretor(sessao)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<VisitaAgendada> visitas = visitaDAO.buscarPorId(getIdUsuario(sessao), false, false, TipoUsuarioLogado.CORRETOR);
        if (visitas.isEmpty()) return ResponseEntity.noContent().build();

        return ResponseEntity.ok(visitas.stream().map(VisitaAgendada::getPainelDTO).toList());
    }

    @PutMapping("/visitas/{idVisita}/arquivar")
    public ResponseEntity<?> arquivarVisita(@PathVariable int idVisita, HttpSession s) {

        if (!isCorretor(s)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        visitaDAO.arquivar(idVisita);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/contratos")
    public ResponseEntity<?> getContratos(HttpSession s) {

        if (!isCorretor(s)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<ContratoLocacao> locacoes = contratoLocacaoDAO.buscarPorId(getIdUsuario(s), TipoUsuarioLogado.CORRETOR, true);
        List<ContratoVenda> vendas = contratoVendaDAO.buscarPorId(getIdUsuario(s), TipoUsuarioLogado.CORRETOR, true);

        if (locacoes.isEmpty() && vendas.isEmpty()) return ResponseEntity.noContent().build();

        List<ContratoPainelDTO> contratos = new ArrayList<>(locacoes.size() + vendas.size());
        contratos.addAll(vendas.stream().map(ContratoVenda::getDTO).toList());
        contratos.addAll(locacoes.stream().map(ContratoLocacao::getDTO).toList());

        return ResponseEntity.ok(contratos);
    }

    @Transactional
    @PostMapping("/contratos/locacao")
    public ResponseEntity<?> salvarContratoLocacao(@Valid @RequestBody FirmarLocacaoDTO dto, HttpSession s) {

        if (!isCorretor(s)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (!dto.isContratoValido()) {
            return ResponseEntity.badRequest().body(dto.getMensagemErro(messageSource));
        }

        ContratoLocacao contrato = new ContratoLocacao(new Corretor(getIdUsuario(s)), dto);

        Optional<VisitaAgendada> respostaVisita = visitaDAO.buscarPorId(dto.getIdVisita());
        if (respostaVisita.isEmpty()) return ResponseEntity.badRequest().build();
        VisitaAgendada visita = respostaVisita.get();

        Optional<Imovel> respostaImovel = imovelDAO.buscarPorId(visita.getImovel().getIdImovel());
        if (respostaImovel.isEmpty()) return ResponseEntity.internalServerError().build();
        Imovel imovel = respostaImovel.get();

        if (!imovel.isLocacao()) return ResponseEntity.badRequest().build();

        Optional<Corretor> respostaCorretor = corretorDAO.buscarPorId(getIdUsuario(s));
        if (respostaCorretor.isEmpty()) return ResponseEntity.internalServerError().build();
        Corretor corretor = respostaCorretor.get();

        if (dto.hasFiador()) { // Cadastro do Fiador (ou pega do banco)

            Fiador fiador = contrato.getFiador();

            Optional<Fiador> fiadorBanco = fiadorDAO.buscarPorCpf(fiador.getCpf());

            if (fiadorBanco.isEmpty()) {
                enderecoDAO.salvar(fiador.getEndereco());
                fiadorDAO.salvar(fiador);
                telefoneDAO.salvarDoFiador(
                        dto.getFiador().getTelefones().stream().map(t -> new TelefoneFiador(fiador, t)).toList()
                );
            } else {
                contrato.setFiador(fiadorBanco.get());
            }
        }

        contrato.setValorComissao(imovel.getValor().multiply(BigDecimal.valueOf(corretor.getPercentualComissao())));
        contrato.setValorAluguel(imovel.getValor());
        contrato.setCliente(visita.getCliente());
        contrato.setImovel(visita.getImovel());

        contratoLocacaoDAO.salvar(contrato);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/contratos/venda")
    public ResponseEntity<?> salvarContratoVenda(@Valid @RequestBody FirmarVendaDTO dto, HttpSession s) {

        if (!isCorretor(s)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (!dto.isContratoValido()) {
            return ResponseEntity.badRequest().body(dto.getMensagemErro(messageSource));
        }

        ContratoVenda contrato = new ContratoVenda(new Corretor(getIdUsuario(s)), dto);

        Optional<VisitaAgendada> respostaVisita = visitaDAO.buscarPorId(dto.getIdVisita());
        if (respostaVisita.isEmpty()) return ResponseEntity.badRequest().build();
        VisitaAgendada visita = respostaVisita.get();

        Optional<Imovel> respostaImovel = imovelDAO.buscarPorId(visita.getImovel().getIdImovel());
        if (respostaImovel.isEmpty()) return ResponseEntity.internalServerError().build();
        Imovel imovel = respostaImovel.get();

        if (!imovel.isVenda()) return ResponseEntity.badRequest().build();

        Optional<Corretor> respostaCorretor = corretorDAO.buscarPorId(getIdUsuario(s));
        if (respostaCorretor.isEmpty()) return ResponseEntity.internalServerError().build();
        Corretor corretor = respostaCorretor.get();

        contrato.setValorVenda(imovel.getValor());
        contrato.setValorComissao(imovel.getValor().multiply(BigDecimal.valueOf(corretor.getPercentualComissao())));
        contrato.setProprietario(imovel.getProprietario());
        contrato.setCliente(visita.getCliente());
        contrato.setImovel(visita.getImovel());

        contratoVendaDAO.salvar(contrato);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
