package com.duarte.imobiliaria.controllers;

import com.duarte.imobiliaria.controllers.DTO.*;
import com.duarte.imobiliaria.controllers.session.TipoUsuarioLogado;
import com.duarte.imobiliaria.controllers.session.UsuarioLogado;
import com.duarte.imobiliaria.database.interfaceDAO.*;
import com.duarte.imobiliaria.model.*;
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
import static com.duarte.imobiliaria.controllers.session.UsuarioLogado.isProprietario;

@RestController
@RequestMapping("/api/painel/proprietario")
public class PainelProprietarioController {

    private final EnderecoDAO enderecoDAO;
    private final VisitaAgendadaDAO visitaAgendadaDAO;
    private final ImovelDAO imovelDAO;
    private final TipoImovelDAO tipoImovelDAO;
    private final ContratoLocacaoDAO locacaoDAO;
    private final ContratoVendaDAO vendaDAO;
    private final FotoDAO fotoDAO;
    private final PublicacaoDAO publicacaoDAO;
    private final ReajusteDAO reajusteDAO;

    private final MessageSource messageSource;

    public PainelProprietarioController(EnderecoDAO enderecoDAO, VisitaAgendadaDAO visitaAgendadaDAO, ImovelDAO imovelDAO, TipoImovelDAO tipoImovelDAO, ContratoLocacaoDAO locacaoDAO, ContratoVendaDAO vendaDAO, FotoDAO fotoDAO, PublicacaoDAO publicacaoDAO, ReajusteDAO reajusteDAO, MessageSource messageSource) {
        this.enderecoDAO = enderecoDAO;
        this.visitaAgendadaDAO = visitaAgendadaDAO;
        this.imovelDAO = imovelDAO;
        this.tipoImovelDAO = tipoImovelDAO;
        this.locacaoDAO = locacaoDAO;
        this.vendaDAO = vendaDAO;
        this.fotoDAO = fotoDAO;
        this.publicacaoDAO = publicacaoDAO;
        this.reajusteDAO = reajusteDAO;
        this.messageSource = messageSource;
    }

    @GetMapping("/visitas")
    public ResponseEntity<List<VisitaPainelDTO>> listarVisitas(HttpSession sessao) {

        if (!isProprietario(sessao)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<VisitaAgendada> visitas = visitaAgendadaDAO.buscarPorId(getIdUsuario(sessao), true, false, TipoUsuarioLogado.PROPRIETARIO);

        return ResponseEntity.ok(visitas.stream().filter(VisitaAgendada::isPotencialContrato).map(VisitaAgendada::getPainelDTO).toList());
    }

    @GetMapping("/contratos")
    public ResponseEntity<List<ContratoPainelDTO>> listarContratos(HttpSession sessao) {

        if (!isProprietario(sessao)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<ContratoLocacao> locacoes = locacaoDAO.buscarPorId(getIdUsuario(sessao), TipoUsuarioLogado.PROPRIETARIO, true);
        List<ContratoVenda> vendas = vendaDAO.buscarPorId(getIdUsuario(sessao), TipoUsuarioLogado.PROPRIETARIO, true);

        List<ContratoPainelDTO> contratos = new ArrayList<>(locacoes.size() + vendas.size());
        contratos.addAll(locacoes.stream().map(ContratoLocacao::getDTO).toList());
        contratos.addAll(vendas.stream().map(ContratoVenda::getDTO).toList());

        return ResponseEntity.ok(contratos);
    }

    @GetMapping("/imoveis")
    public ResponseEntity<List<ImovelPainelDTO>> listarImoveis(HttpSession sessao) {

        if (!isProprietario(sessao)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<TipoImovel> tiposImoveis = tipoImovelDAO.listarTodos();
        List<Imovel> imoveis = imovelDAO.buscarPorProprietario(getIdUsuario(sessao));

        List<ImovelPainelDTO> dtos = new ArrayList<>(imoveis.size());

        for (Imovel i : imoveis) {
            Optional<Endereco> e = enderecoDAO.buscarPorId(i.getEndereco().getIdEndereco());
            if (e.isEmpty()) return ResponseEntity.internalServerError().build();
            i.setEndereco(e.get());

            Optional<Publicacao> p = publicacaoDAO.buscarPublicacaoImovel(i.getIdImovel());

            dtos.add(
                    i.getDTO(
                            tiposImoveis.get(i.getTipo() - 1).getDescricao(),
                            p.map(Publicacao::getIdPublicacao).orElse(null)
                    )
            );
        }

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/imoveis")
    public ResponseEntity<?> cadastrarImovel(@RequestBody Imovel imovel, HttpSession sessao) {

        if (!isProprietario(sessao)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        imovel.setProprietario(new Proprietario(getIdUsuario(sessao)));
        imovelDAO.salvar(imovel);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/publicacoes")
    public ResponseEntity<?> criarPublicacao(@RequestBody PublicacaoCadastroDTO dto, HttpSession sessao) {

        if (!isProprietario(sessao)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Foto> fotos = dto.fotos().stream()
                            .map(f -> new Foto(dto.idImovel(), f)).toList();
        fotoDAO.salvar(fotos);

        publicacaoDAO.salvar(new Publicacao(fotos.get(dto.indiceCapa()).getIdFoto(), dto));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Transactional
    @PostMapping("/contratos/{idLocacao}/reajustes")
    public ResponseEntity<?> fazerReajuste(@PathVariable int idLocacao, @Valid @RequestBody ReajusteDTO dto, HttpSession s) {

        if (!isProprietario(s)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Optional<ContratoLocacao> locacao = locacaoDAO.buscarPorId(idLocacao);
        if (locacao.isEmpty()) return erro(HttpStatus.NOT_FOUND, "geral", "reajuste.contrato.nao.encontrado");
        ContratoLocacao contrato = locacao.get();

        Optional<Imovel> imovel = imovelDAO.buscarPorId(contrato.getImovel().getIdImovel());
        if (imovel.isEmpty()) return ResponseEntity.internalServerError().build();

        if (imovel.get().getProprietario().getIdProprietario() != getIdUsuario(s))
            return erro(HttpStatus.FORBIDDEN, "geral", "reajuste.contrato.nao.encontrado");

        if (!contrato.isAtivo()) return erro(HttpStatus.CONFLICT, "geral", "reajuste.contrato.inativo");

        if (dto.getNovoValor().compareTo(contrato.getValorAluguel()) <= 0)
            return erro(HttpStatus.BAD_REQUEST, "novoValor", "reajuste.valor.menor");

        contrato.setValorAluguel(dto.getNovoValor());
        locacaoDAO.salvar(contrato);
        reajusteDAO.salvar(new ReajusteAluguel(contrato, dto));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private ResponseEntity<Map<String, String>> erro(HttpStatus status, String campo, String chaveMensagem) {
        String mensagem = messageSource.getMessage(chaveMensagem, null, chaveMensagem, LocaleContextHolder.getLocale());
        return ResponseEntity.status(status).body(Map.of(campo, mensagem));
    }
}
