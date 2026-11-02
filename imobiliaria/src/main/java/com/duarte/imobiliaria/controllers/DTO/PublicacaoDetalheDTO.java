package com.duarte.imobiliaria.controllers.DTO;

import com.duarte.imobiliaria.model.Endereco;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PublicacaoDetalheDTO {

    // Dados da Publicação (Origem: Publicacao)
    private Integer idPublicacao;
    private String titulo;
    private String tipoPublicacao;
    private LocalDate data;
    private Integer acessos;
    private Integer idImovel;
    private Integer idCorretor;

    // Dados extraídos do Imóvel
    private String tipoImovel;
    private BigDecimal valor;

    // Dados do Proprietário
    private String nomeProprietario;
    private String emailProprietario;
    private List<String> telefonesProprietario;

    // Estruturas Aninhadas
    private Endereco endereco;
    private ImovelDTO imovel;
    private List<Integer> idsFotos;
    private List<VisitaAgendadaDTO> visitas;

    public Integer getIdPublicacao() {
        return idPublicacao;
    }

    public void setIdPublicacao(Integer idPublicacao) {
        this.idPublicacao = idPublicacao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipoPublicacao() {
        return tipoPublicacao;
    }

    public void setTipoPublicacao(String tipoPublicacao) {
        this.tipoPublicacao = tipoPublicacao;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Integer getAcessos() {
        return acessos;
    }

    public void setAcessos(Integer acessos) {
        this.acessos = acessos;
    }

    public Integer getIdImovel() {
        return idImovel;
    }

    public void setIdImovel(Integer idImovel) {
        this.idImovel = idImovel;
    }

    public Integer getIdCorretor() {
        return idCorretor;
    }

    public void setIdCorretor(Integer idCorretor) {
        this.idCorretor = idCorretor;
    }

    public String getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(String tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getNomeProprietario() {
        return nomeProprietario;
    }

    public void setNomeProprietario(String nomeProprietario) {
        this.nomeProprietario = nomeProprietario;
    }

    public String getEmailProprietario() {
        return emailProprietario;
    }

    public void setEmailProprietario(String emailProprietario) {
        this.emailProprietario = emailProprietario;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public ImovelDTO getImovel() {
        return imovel;
    }

    public void setImovel(ImovelDTO imovel) {
        this.imovel = imovel;
    }

    public List<Integer> getIdsFotos() {
        return idsFotos;
    }

    public void setIdsFotos(List<Integer> idsFotos) {
        this.idsFotos = idsFotos;
    }

    public List<VisitaAgendadaDTO> getVisitas() {
        return visitas;
    }

    public void setVisitas(List<VisitaAgendadaDTO> visitas) {
        this.visitas = visitas;
    }

    public List<String> getTelefonesProprietario() {
        return telefonesProprietario;
    }

    public void setTelefonesProprietario(List<String> telefonesProprietario) {
        this.telefonesProprietario = telefonesProprietario;
    }
}
