package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.controllers.DTO.*;
import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.database.interfaceDAO.ContratoLocacaoDAO;
import com.duarte.imobiliaria.database.interfaceDAO.ContratoVendaDAO;
import com.duarte.imobiliaria.database.interfaceDAO.ReciboDAO;
import com.duarte.imobiliaria.database.interfaceDAO.VisitaAgendadaDAO;
import com.duarte.imobiliaria.model.ContratoLocacao;
import com.duarte.imobiliaria.model.ContratoVenda;
import com.duarte.imobiliaria.model.ReciboAluguel;
import com.duarte.imobiliaria.model.VisitaAgendada;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.duarte.imobiliaria.controllers.session.UsuarioLogado.getIdUsuario;
import static com.duarte.imobiliaria.controllers.session.UsuarioLogado.isCliente;

@RestController
@RequestMapping("/api/painel/cliente")
public class PainelClienteController {

    private final VisitaAgendadaDAO visitaDAO;
    private final ContratoVendaDAO vendaDAO;
    private final ContratoLocacaoDAO locacaoDAO;
    private final ReciboDAO reciboDAO;

    private final MessageSource messageSource;

    public PainelClienteController(VisitaAgendadaDAO visitaDAO, ContratoVendaDAO vendaDAO, ContratoLocacaoDAO locacaoDAO, ReciboDAO reciboDAO, MessageSource messageSource) {
        this.visitaDAO = visitaDAO;
        this.vendaDAO = vendaDAO;
        this.locacaoDAO = locacaoDAO;
        this.reciboDAO = reciboDAO;
        this.messageSource = messageSource;
    }

    @GetMapping("/visitas")
    public ResponseEntity<List<VisitaPainelDTO>> listarVisitas(HttpSession sessao) {

        if (!isCliente(sessao)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<VisitaAgendada> visitas = visitaDAO.buscarPorId(getIdUsuario(sessao), true, false, TipoUsuarioLogado.CLIENTE);
        return ResponseEntity.ok(visitas.stream().filter(VisitaAgendada::isPotencialContrato).map(VisitaAgendada::getPainelDTO).toList());
    }

    @GetMapping("/contratos")
    public ResponseEntity<List<ContratoPainelDTO>> listarContratos(HttpSession sessao) {

        if (!isCliente(sessao)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<ContratoLocacao> locacoes = locacaoDAO.buscarPorId(getIdUsuario(sessao), TipoUsuarioLogado.CLIENTE, true);
        List<ContratoVenda> vendas = vendaDAO.buscarPorId(getIdUsuario(sessao), TipoUsuarioLogado.CLIENTE, true);
        List<ContratoPainelDTO> contratos = new ArrayList<>(locacoes.size() + vendas.size());
        contratos.addAll(locacoes.stream().map(ContratoLocacao::getDTO).toList());
        contratos.addAll(vendas.stream().map(ContratoVenda::getDTO).toList());
        return ResponseEntity.ok(contratos);
    }

    @GetMapping("/contratos/locacao/{idLocacao}/recibos")
    public ResponseEntity<List<ReciboPainelDTO>> listarRecibos(@PathVariable int idLocacao, @Valid FiltroReciboDTO filtro, HttpSession sessao) {

        if (!isCliente(sessao)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<ContratoLocacao> locacao = locacaoDAO.buscarPorId(idLocacao);
        if (locacao.isEmpty()) return ResponseEntity.badRequest().build();
        if (locacao.get().getCliente().getIdCliente() != getIdUsuario(sessao)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        filtro.setIdLocacao(idLocacao);
        List<ReciboAluguel> recibos = reciboDAO.filtrar(filtro);

        return ResponseEntity.ok(recibos.stream().map(ReciboAluguel::getDTO).toList());
    }

    /** Mostra, antes da confirmação, a data e a multa que seriam aplicadas se o contrato fosse rescindido hoje. */
    @GetMapping("/contratos/locacao/{idLocacao}/rescisao")
    public ResponseEntity<?> simularRescisao(@PathVariable int idLocacao, HttpSession sessao) {

        if (!isCliente(sessao)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<ContratoLocacao> locacao = locacaoDAO.buscarPorId(idLocacao);
        ResponseEntity<Map<String, String>> recusa = recusarAcesso(locacao, sessao, "rescisao.contrato.nao.encontrado", "rescisao.contrato.inativo");
        if (recusa != null) return recusa;

        return ResponseEntity.ok(locacao.get().simularRescisao());
    }

    @Transactional
    @PostMapping("/contratos/locacao/{idLocacao}/rescisao")
    public ResponseEntity<?> rescindirContrato(@PathVariable int idLocacao, @Valid @RequestBody RescisaoDTO dto, HttpSession sessao) {

        if (!isCliente(sessao)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<ContratoLocacao> locacao = locacaoDAO.buscarPorId(idLocacao);
        ResponseEntity<Map<String, String>> recusa = recusarAcesso(locacao, sessao, "rescisao.contrato.nao.encontrado", "rescisao.contrato.inativo");
        if (recusa != null) return recusa;

        RescisaoSimulacaoDTO aplicada = locacao.get().simularRescisao();

        boolean rescindido = locacaoDAO.rescindir(idLocacao, aplicada.dataRescisao(), aplicada.multa(), dto.getMotivo().trim());
        if (!rescindido) return erro(HttpStatus.CONFLICT, "rescisao.contrato.inativo");

        return ResponseEntity.ok(aplicada);
    }


    private ResponseEntity<Map<String, String>> recusarAcesso(Optional<ContratoLocacao> locacao, HttpSession sessao, String chaveNaoEncontrado, String chaveInativo) {
        if (locacao.isEmpty()) return erro(HttpStatus.NOT_FOUND, chaveNaoEncontrado);

        ContratoLocacao c = locacao.get();
        if (c.getCliente().getIdCliente() != getIdUsuario(sessao)) return erro(HttpStatus.FORBIDDEN, chaveNaoEncontrado);
        if (!c.isAtivo()) return erro(HttpStatus.CONFLICT, chaveInativo);

        return null;
    }

    private ResponseEntity<Map<String, String>> erro(HttpStatus status, String chaveMensagem) {
        String mensagem = messageSource.getMessage(chaveMensagem, null, chaveMensagem, LocaleContextHolder.getLocale());
        return ResponseEntity.status(status).body(Map.of("geral", mensagem));
    }
}
