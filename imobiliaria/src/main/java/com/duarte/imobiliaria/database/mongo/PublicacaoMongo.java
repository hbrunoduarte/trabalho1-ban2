package com.duarte.imobiliaria.database.mongo;

import com.duarte.imobiliaria.controllers.DTO.FiltroPublicacaoDTO;
import com.duarte.imobiliaria.controllers.DTO.ImovelDTO;
import com.duarte.imobiliaria.controllers.DTO.PublicacaoDetalheDTO;
import com.duarte.imobiliaria.controllers.DTO.PublicacaoPesquisaDTO;
import com.duarte.imobiliaria.database.interfaceDAO.PublicacaoDAO;
import com.duarte.imobiliaria.model.Endereco;
import com.duarte.imobiliaria.model.Publicacao;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
@Profile("mongo")
public class PublicacaoMongo extends MongoDaoBase implements PublicacaoDAO {

    private static final int AGRUPAMENTO_ANOS = 3;

    public PublicacaoMongo(MongoTemplate mongo) {
        super(mongo);
    }

    private static Publicacao paraPublicacao(Document d) {
        Publicacao p = new Publicacao();
        p.setIdPublicacao(inteiro(d, "_id"));
        p.setIdImovel(inteiro(d, "imovelId"));
        p.setIdFotoCapa(inteiro(d, "fotoCapaId"));
        p.setTitulo(texto(d, "titulo"));
        p.setTipoPublicacao(texto(d, "tipoPublicacao"));
        p.setAtiva(bool(d, "ativa"));
        p.setData(data(d, "data"));
        p.setAcessos(inteiro(d, "acessos"));
        return p;
    }

    private record Linha(Document pub, Document imovel, Document endereco, String tipoImovel) {}

    private Optional<Linha> montarLinha(Document pub) {
        Optional<Document> imovel = umPorId(IMOVEIS, inteiro(pub, "imovelId"));
        if (imovel.isEmpty()) return Optional.empty();
        Document i = imovel.get();
        Document end = umPorId(ENDERECOS, inteiro(i, "enderecoId")).orElse(new Document());
        String tipo = umPorId(TIPOS_IMOVEL, inteiro(i, "idTipoImovel")).map(t -> texto(t, "descricao")).orElse(null);
        return Optional.of(new Linha(pub, i, end, tipo));
    }

    private PublicacaoPesquisaDTO paraPesquisaDTO(Linha l) {
        PublicacaoPesquisaDTO dto = new PublicacaoPesquisaDTO();
        dto.setIdPublicacao(inteiro(l.pub(), "_id"));
        dto.setTitulo(texto(l.pub(), "titulo"));
        dto.setTipoImovel(l.tipoImovel());
        dto.setValor(decimal(l.imovel(), "valor").floatValue());
        dto.setTipoPublicacao("V".equals(texto(l.pub(), "tipoPublicacao")) ? "Venda" : "Locacao");
        dto.setEstado(texto(l.endereco(), "estado"));
        dto.setCidade(texto(l.endereco(), "cidade"));
        dto.setBairro(texto(l.endereco(), "bairro"));

        Integer idCapa = inteiro(l.pub(), "fotoCapaId");
        if (idCapa != null) {
            umPorId(FOTOS, idCapa).ifPresent(f -> dto.setFotoCapa(bytes(f, "imagem")));
        }
        return dto;
    }

    private List<Linha> linhasAtivas() {
        return varios(PUBLICACOES, por("ativa", true)).stream()
                .map(this::montarLinha).flatMap(Optional::stream).toList();
    }

    private static Comparator<Linha> ordenacao() {
        return Comparator
                .comparingInt((Linha l) -> (data(l.pub(), "data").getYear() / AGRUPAMENTO_ANOS) * AGRUPAMENTO_ANOS).reversed()
                .thenComparing(Comparator.comparingInt((Linha l) -> inteiro(l.pub(), "acessos")).reversed());
    }

    private static boolean contem(Object valor, String termo) {
        return valor != null && valor.toString().toLowerCase(Locale.ROOT).contains(termo.toLowerCase(Locale.ROOT));
    }

    @Override
    public void salvar(Publicacao p) {
        if (p.getIdPublicacao() == null || p.getIdPublicacao() == 0) {
            int id = proximoId(PUBLICACOES);
            if (p.getData() == null) p.setData(LocalDate.now());
            if (p.getAcessos() == null) p.setAcessos(0);

            Document d = new Document("_id", id)
                    .append("imovelId", p.getIdImovel())
                    .append("fotoCapaId", p.getIdFotoCapa())
                    .append("titulo", p.getTitulo())
                    .append("tipoPublicacao", p.getTipoPublicacao())
                    .append("ativa", true)
                    .append("data", LocalDate.now().toString())
                    .append("acessos", 0);
            mongo.insert(d, PUBLICACOES);
            p.setIdPublicacao(id);
        } else {
            Document d = new Document("_id", p.getIdPublicacao())
                    .append("imovelId", p.getIdImovel())
                    .append("fotoCapaId", p.getIdFotoCapa())
                    .append("titulo", p.getTitulo())
                    .append("tipoPublicacao", p.getTipoPublicacao())
                    .append("ativa", p.getAtiva() == null || p.getAtiva())
                    .append("data", p.getData() == null ? LocalDate.now().toString() : p.getData().toString())
                    .append("acessos", p.getAcessos() == null ? 0 : p.getAcessos());
            mongo.save(d, PUBLICACOES);
        }
    }

    @Override
    public Optional<Publicacao> buscarPorId(int idPublicacao) {
        return umPorId(PUBLICACOES, idPublicacao).map(PublicacaoMongo::paraPublicacao);
    }

    @Override
    public Optional<Publicacao> buscarPublicacaoImovel(int idImovel) {
        Query q = Query.query(Criteria.where("imovelId").is(idImovel).and("ativa").is(true));
        return um(PUBLICACOES, q).map(PublicacaoMongo::paraPublicacao);
    }

    @Override
    public Optional<PublicacaoDetalheDTO> buscarDetalhesPorId(int idPublicacao) {
        Document pub = umPorId(PUBLICACOES, idPublicacao).orElse(null);
        if (pub == null || !Boolean.TRUE.equals(bool(pub, "ativa"))) return Optional.empty();

        Linha l = montarLinha(pub).orElse(null);
        if (l == null) return Optional.empty();
        Document prop = umPorId(PROPRIETARIOS, inteiro(l.imovel(), "proprietarioId")).orElse(new Document());

        PublicacaoDetalheDTO dto = new PublicacaoDetalheDTO();
        dto.setIdPublicacao(inteiro(pub, "_id"));
        dto.setTitulo(texto(pub, "titulo"));
        dto.setTipoPublicacao(texto(pub, "tipoPublicacao"));
        dto.setData(data(pub, "data"));
        dto.setAcessos(inteiro(pub, "acessos"));

        dto.setIdImovel(inteiro(l.imovel(), "_id"));
        dto.setTipoImovel(l.tipoImovel());
        dto.setValor(decimal(l.imovel(), "valor"));

        String nome = texto(prop, "nome");
        dto.setNomeProprietario(nome != null ? nome : texto(prop, "razaoSocial"));
        dto.setEmailProprietario(texto(prop, "email"));

        Endereco e = endereco(l.endereco());
        dto.setEndereco(e);

        ImovelDTO im = new ImovelDTO();
        im.setAreaTotal(real(l.imovel(), "areaTotal"));
        im.setQtdDormitorios(inteiro(l.imovel(), "qtdDormitorios"));
        im.setQtdBanheiros(inteiro(l.imovel(), "qtdBanheiros"));
        im.setQtdVagas(inteiro(l.imovel(), "qtdVagas"));
        im.setAnoConstrucao(inteiro(l.imovel(), "anoConstrucao"));
        im.setStatus(texto(l.imovel(), "status"));
        im.setDescricao(texto(l.imovel(), "descricao"));
        dto.setImovel(im);

        return Optional.of(dto);
    }

    /** No Postgres o parâmetro idProprietario é ignorado (retorna todas as ativas, da mais nova). */
    @Override
    public List<PublicacaoPesquisaDTO> pesquisarTodas(int idProprietario) {
        return linhasAtivas().stream()
                .sorted(Comparator.comparing((Linha l) -> data(l.pub(), "data")).reversed())
                .map(this::paraPesquisaDTO).toList();
    }

    @Override
    public List<PublicacaoPesquisaDTO> listarAtivas() {
        return linhasAtivas().stream().sorted(ordenacao()).map(this::paraPesquisaDTO).toList();
    }

    @Override
    public List<PublicacaoPesquisaDTO> filtrarPublicacoes(FiltroPublicacaoDTO f) {
        return linhasAtivas().stream()
                .filter(l -> {
                    BigDecimal valor = decimal(l.imovel(), "valor");
                    if (f.getValorMin() != null && valor.floatValue() < f.getValorMin()) return false;
                    if (f.getValorMax() != null && valor.floatValue() > f.getValorMax()) return false;

                    Document e = l.endereco();
                    if (f.getCep() != null) {
                        if (!f.getCep().equals(texto(e, "cep"))) return false;
                    } else {
                        if (f.getEstado() != null && !contem(e.get("estado"), f.getEstado())) return false;
                        if (f.getCidade() != null && !contem(e.get("cidade"), f.getCidade())) return false;
                        if (f.getBairro() != null && !contem(e.get("bairro"), f.getBairro())) return false;
                        if (f.getRua() != null && !contem(e.get("rua"), f.getRua())) return false;
                    }
                    if (f.getNumero() != null && !f.getNumero().equals(inteiro(e, "numero"))) return false;
                    if (f.getTipoImovel() != null && !f.getTipoImovel().equals(inteiro(l.imovel(), "idTipoImovel"))) return false;
                    if (f.getTipoPublicacao() != null && !f.getTipoPublicacao().equals(texto(l.pub(), "tipoPublicacao"))) return false;
                    return f.getTitulo() == null || contem(l.pub().get("titulo"), f.getTitulo());
                })
                .sorted(ordenacao())
                .map(this::paraPesquisaDTO)
                .toList();
    }

    @Override
    public void desativar(int idPublicacao) {
        mongo.updateFirst(porId(idPublicacao), new Update().set("ativa", false), PUBLICACOES);
    }

    @Override
    public boolean excluir(int idPublicacao) {
        return mongo.remove(porId(idPublicacao), PUBLICACOES).getDeletedCount() > 0;
    }

    @Override
    public void incrementarAcessos(int idPublicacao) {
        mongo.updateFirst(porId(idPublicacao), new Update().inc("acessos", 1), PUBLICACOES);
    }
}
