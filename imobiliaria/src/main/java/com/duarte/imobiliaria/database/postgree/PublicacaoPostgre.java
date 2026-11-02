package com.duarte.imobiliaria.database.postgree;

import com.duarte.imobiliaria.controllers.DTO.FiltroPublicacaoDTO;
import com.duarte.imobiliaria.controllers.DTO.ImovelDTO;
import com.duarte.imobiliaria.controllers.DTO.PublicacaoDetalheDTO;
import com.duarte.imobiliaria.controllers.DTO.PublicacaoPesquisaDTO;
import com.duarte.imobiliaria.database.interfaceDAO.PublicacaoDAO;
import com.duarte.imobiliaria.model.Endereco;
import com.duarte.imobiliaria.model.Foto;
import com.duarte.imobiliaria.model.Imovel;
import com.duarte.imobiliaria.model.Publicacao;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("postgre")
public class PublicacaoPostgre implements PublicacaoDAO {

    private final JdbcTemplate jdbcTemplate;

    private final String consultaPadraoPesquisa = "SELECT p.id_publicacao, p.titulo, p.tipo_publicacao, ti.descricao, i.valor, e.estado, e.cidade, e.bairro, f.imagem " +
                                                    "FROM Publicacao p " +
                                                    "JOIN Imovel i ON p.id_imovel=i.id_imovel " +
                                                    "JOIN TipoImovel ti ON i.id_tipo_imovel=ti.id_tipo_imovel " +
                                                    "JOIN Endereco e ON i.id_endereco=e.id_endereco " +
                                                    "LEFT JOIN Foto f ON p.id_foto_capa=f.id_foto " +
                                                    "WHERE p.ativa=true ";

    private final int agrupamentoAnosPublicacoes = 3;
    private final String ordenacaoPublicacoes =
            "ORDER BY (EXTRACT(YEAR FROM p.data)::int / " + agrupamentoAnosPublicacoes + ") * " + agrupamentoAnosPublicacoes + " DESC, " +
            "p.acessos DESC";

    private final RowMapper<PublicacaoPesquisaDTO> rmPublicacaoPesquisaDTO = (rs, _) -> {
        PublicacaoPesquisaDTO ppDTO = new PublicacaoPesquisaDTO();
        ppDTO.setIdPublicacao(rs.getInt("id_publicacao"));
        ppDTO.setTitulo(rs.getString("titulo"));
        ppDTO.setFotoCapa(rs.getBytes("imagem"));
        ppDTO.setTipoImovel(rs.getString("descricao"));
        ppDTO.setValor(rs.getFloat("valor"));
        ppDTO.setTipoPublicacao(
                rs.getString("tipo_publicacao").equals("V") ? "Venda" : "Locacao");
        ppDTO.setEstado(rs.getString("estado"));
        ppDTO.setCidade(rs.getString("cidade"));
        ppDTO.setBairro(rs.getString("bairro"));
        return ppDTO;
    };

    private final RowMapper<Publicacao> rmPublicacao = (rs, _) -> {
        Publicacao p = new Publicacao();
        p.setIdPublicacao(rs.getInt("id_publicacao"));
        p.setTitulo(rs.getString("titulo"));
        p.setData(rs.getDate("data").toLocalDate());
        p.setAcessos(rs.getInt("acessos"));
        p.setIdFotoCapa(rs.getInt("id_foto_capa"));
        p.setIdImovel(rs.getInt("id_imovel"));
        return p;
    };

    public PublicacaoPostgre(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Publicacao> buscarPublicacaoImovel(int idImovel) {
        String sql = "SELECT * FROM Publicacao WHERE id_imovel=? AND ativa";
        List<Publicacao> resultado = jdbcTemplate.query(sql, rmPublicacao, idImovel);
        return resultado.isEmpty() ? Optional.empty() : Optional.of(resultado.getFirst());
    }

    @Override
    public void salvar(Publicacao p) {
        if (p.getIdPublicacao() == null || p.getIdPublicacao() == 0) {

            if (p.getData() == null) p.setData(LocalDate.now());
            if (p.getAcessos() == null) p.setAcessos(0);

            String sql = "INSERT INTO Publicacao (id_imovel, id_foto_capa, titulo, tipo_publicacao) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[] {"id_publicacao"});
                ps.setInt(1, p.getIdImovel());

                if (p.getIdFotoCapa() != null) {
                    ps.setInt(2, p.getIdFotoCapa());
                } else {
                    ps.setNull(2, Types.INTEGER);
                }

                ps.setString(3, p.getTitulo());
                ps.setString(4, p.getTipoPublicacao());
                return ps;
            }, keyHolder);

            if (keyHolder.getKey() != null) {
                p.setIdPublicacao(keyHolder.getKey().intValue());
            }

        } else {
            String sql = "UPDATE Publicacao SET id_imovel=?, id_foto_capa=?, titulo=?, tipo_publicacao=?, ativa=?, data=?, acessos=? WHERE id_publicacao=?";

            jdbcTemplate.update(sql,
                    p.getIdImovel(),
                    p.getIdFotoCapa(),
                    p.getTitulo(),
                    p.getTipoPublicacao(),
                    p.getAtiva(),
                    p.getData() != null ? Date.valueOf(p.getData()) : null,
                    p.getAcessos(),
                    p.getIdPublicacao()
            );
        }
    }

    @Override
    public Optional<PublicacaoDetalheDTO> buscarDetalhesPorId(int idPublicacao) {
        String sql = """
                SELECT
                    p.id_publicacao AS pub_id,
                    p.titulo AS pub_titulo,
                    p.tipo_publicacao AS pub_tipo,
                    p.data AS pub_data,
                    p.acessos AS pub_acessos,
                
                    i.id_imovel AS imovel_id,
                    ti.descricao AS imovel_tipo_descricao,
                    i.valor AS imovel_valor,
                    i.area_total AS imovel_area,
                    i.qtd_dormitorios AS imovel_quartos,
                    i.qtd_banheiros AS imovel_banheiros,
                    i.qtd_vagas AS imovel_vagas,
                    i.ano_construcao AS imovel_ano,
                    i.status AS imovel_status,
                    i.descricao AS imovel_descricao,
                
                    pr.id_proprietario AS prop_id,
                    pr.nome AS prop_nome,
                    pr.razao_social AS prop_razao,
                    pr.email AS prop_email,
                
                    e.id_endereco AS end_id,
                    e.cep AS end_cep,
                    e.estado AS end_estado,
                    e.cidade AS end_cidade,
                    e.bairro AS end_bairro,
                    e.numero AS end_numero,
                    e.rua AS end_rua,
                    e.complemento AS end_complemento
                
                FROM Publicacao p
                JOIN Imovel i ON p.id_imovel = i.id_imovel
                JOIN TipoImovel ti ON i.id_tipo_imovel = ti.id_tipo_imovel
                JOIN Endereco e ON i.id_endereco = e.id_endereco
                JOIN Proprietario pr ON i.id_proprietario = pr.id_proprietario
                WHERE p.id_publicacao = ? AND p.ativa""";

        List<PublicacaoDetalheDTO> resultados = jdbcTemplate.query(sql, (rs, _) -> {
            PublicacaoDetalheDTO dto = new PublicacaoDetalheDTO();

            dto.setIdPublicacao(rs.getInt("pub_id"));
            dto.setTitulo(rs.getString("pub_titulo"));
            dto.setTipoPublicacao(rs.getString("pub_tipo"));
            dto.setData(rs.getObject("pub_data", LocalDate.class));
            dto.setAcessos(rs.getInt("pub_acessos"));

            dto.setIdImovel(rs.getInt("imovel_id"));
            dto.setTipoImovel(rs.getString("imovel_tipo_descricao"));
            dto.setValor(rs.getBigDecimal("imovel_valor"));

            String nome = rs.getString("prop_nome");
            String razaoSocial = rs.getString("prop_razao");
            dto.setNomeProprietario(nome != null ? nome : razaoSocial);
            dto.setEmailProprietario(rs.getString("prop_email"));

            Endereco endereco = new Endereco();
                endereco.setIdEndereco(rs.getInt("end_id"));
                endereco.setCep(rs.getString("end_cep"));
                endereco.setEstado(rs.getString("end_estado"));
                endereco.setCidade(rs.getString("end_cidade"));
                endereco.setBairro(rs.getString("end_bairro"));
                endereco.setNumero(rs.getInt("end_numero"));
                endereco.setRua(rs.getString("end_rua"));
                endereco.setComplemento(rs.getString("end_complemento"));
            dto.setEndereco(endereco);

            ImovelDTO imovel = new ImovelDTO();
                imovel.setAreaTotal(rs.getFloat("imovel_area"));
                imovel.setQtdDormitorios(rs.getInt("imovel_quartos"));
                imovel.setQtdBanheiros(rs.getInt("imovel_banheiros"));
                imovel.setQtdVagas(rs.getInt("imovel_vagas"));
                imovel.setAnoConstrucao(rs.getInt("imovel_ano"));
                imovel.setStatus(rs.getString("imovel_status"));
                imovel.setDescricao(rs.getString("imovel_descricao"));
            dto.setImovel(imovel);

            return dto;
        }, idPublicacao);

        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public Optional<Publicacao> buscarPorId(int idPublicacao) {
        String sql = "SELECT * FROM Publicacao WHERE id_publicacao=?";
        List<Publicacao> resultados = jdbcTemplate.query(sql, rmPublicacao, idPublicacao);
        return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.getFirst());
    }

    @Override
    public List<PublicacaoPesquisaDTO> pesquisarTodas(int idProprietario) {

        String sql = "SELECT p.id_publicacao, p.titulo, p.tipo_publicacao, ti.descricao, e.estado, e.cidade, e.bairro, f.imagem " +
                        "FROM Publicacao p " +
                        "JOIN Imovel i ON p.id_imovel=i.id_imovel " +
                        "JOIN TipoImovel ti ON i.id_tipo_imovel=ti.id_tipo_imovel " +
                        "JOIN Endereco e ON i.id_endereco=e.id_endereco " +
                        "JOIN Proprietario pr ON pr.id_proprietario=i.id_proprietario " +
                        "LEFT JOIN Foto f ON p.id_foto_capa=f.id_foto " +
                        "WHERE p.ativa=true " +
                        "ORDER BY p.data DESC";

        return jdbcTemplate.query(sql, rmPublicacaoPesquisaDTO);
    }

    @Override
    public List<PublicacaoPesquisaDTO> listarAtivas() {
        String sql = consultaPadraoPesquisa + ordenacaoPublicacoes;
        return jdbcTemplate.query(sql, rmPublicacaoPesquisaDTO);
    }

    @Override
    public List<PublicacaoPesquisaDTO> filtrarPublicacoes(FiltroPublicacaoDTO filtro) {

        StringBuilder sql = new StringBuilder(consultaPadraoPesquisa);

        List<Object> parametros = new ArrayList<>();

        sql.append("AND i.valor>=? AND i.valor<=? ");
        parametros.add(filtro.getValorMin());
        parametros.add(filtro.getValorMax());

        if (filtro.getCep() != null) {

            sql.append("AND e.cep=? ");
            parametros.add(filtro.getCep());

        } else {
            if (filtro.getEstado() != null) {
                sql.append("AND e.estado ILIKE ? ");
                parametros.add("%" + filtro.getEstado() + "%");
            }
            if (filtro.getCidade() != null) {
                sql.append("AND e.cidade ILIKE ? ");
                parametros.add("%" + filtro.getCidade() + "%");
            }
            if (filtro.getBairro() != null) {
                sql.append("AND e.bairro ILIKE ? ");
                parametros.add("%" + filtro.getBairro() + "%");
            }
            if (filtro.getRua() != null) {
                sql.append("AND e.rua ILIKE ? ");
                parametros.add("%" + filtro.getRua() + "%");
            }
        }

        if (filtro.getNumero() != null) {
            sql.append("AND e.numero=? ");
            parametros.add(filtro.getNumero());
        }

        if (filtro.getTipoImovel() != null) {
            sql.append("AND i.id_tipo_imovel=? ");
            parametros.add(filtro.getTipoImovel());
        }

        if (filtro.getTipoPublicacao() != null) {
            sql.append("AND p.tipo_publicacao=? ");
            parametros.add(filtro.getTipoPublicacao());
        }

        if (filtro.getTitulo() != null) {
            sql.append("AND p.titulo ILIKE ? ");
            parametros.add("%" + filtro.getTitulo() + "%");
        }

        sql.append(ordenacaoPublicacoes);

        return jdbcTemplate.query(sql.toString(), rmPublicacaoPesquisaDTO, parametros.toArray());
    }

    @Override
    public void desativar(int idPublicacao) {
        jdbcTemplate.update("UPDATE Publicacao SET ativa=false WHERE id_publicacao=?", idPublicacao);
    }

    @Override
    public boolean excluir(int idPublicacao) {
        return jdbcTemplate.update("DELETE FROM Publicacao WHERE id_publicacao=?", idPublicacao) > 0;
    }

    @Override
    public void incrementarAcessos(int idPublicacao) {
        String sql = "UPDATE Publicacao SET acessos = acessos+1 WHERE id_publicacao=?";
        jdbcTemplate.update(sql, idPublicacao);
    }
}
