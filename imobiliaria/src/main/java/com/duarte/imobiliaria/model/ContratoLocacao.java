package com.duarte.imobiliaria.model;

import com.duarte.imobiliaria.controllers.DTO.ContratoPainelDTO;
import com.duarte.imobiliaria.controllers.DTO.FirmarLocacaoDTO;
import com.duarte.imobiliaria.controllers.DTO.RescisaoSimulacaoDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ContratoLocacao {

    private Integer idLocacao;
    private Integer prazoMes;
    private String garantia;
    private BigDecimal valorCaucao;
    private BigDecimal valorComissao;
    private String statusContrato;
    private Integer diaVencimento;
    private LocalDate dataInicio;
    private BigDecimal valorAluguel;
    // Caso seja rescindido
    private LocalDate dataRescisao;
    private BigDecimal multa;
    private String motivo;
    // FKs
    private Imovel imovel;
    private Cliente cliente;
    private Corretor corretor;
    // Pode ser null
    private Fiador fiador;

    public ContratoLocacao() {
    }

    public ContratoLocacao(Integer idLocacao) {
        this.idLocacao = idLocacao;
    }

    public ContratoLocacao(FirmarLocacaoDTO dto) {
        this.prazoMes = dto.getPrazoMeses();
        this.garantia = dto.getGarantia();
        this.valorCaucao = dto.getValorCaucao();
        this.diaVencimento = dto.getDiaVencimento();
        this.fiador = new Fiador(dto.getFiador());
    }

    public ContratoLocacao(Corretor c, FirmarLocacaoDTO dto) {
        this(dto);
        corretor = c;
    }

    private static final int ALUGUEIS_DE_MULTA = 3;
    private static final String STATUS_ATIVO = "ATIVO";

    public Integer getIdLocacao() {
        return idLocacao;
    }

    public void setIdLocacao(Integer idLocacao) {
        this.idLocacao = idLocacao;
    }

    public Integer getPrazoMes() {
        return prazoMes;
    }

    public void setPrazoMes(Integer prazoMes) {
        this.prazoMes = prazoMes;
    }

    public String getGarantia() {
        return garantia;
    }

    public void setGarantia(String garantia) {
        this.garantia = garantia;
    }

    public BigDecimal getValorCaucao() {
        return valorCaucao;
    }

    public void setValorCaucao(BigDecimal valorCaucao) {
        this.valorCaucao = valorCaucao;
    }

    public BigDecimal getValorComissao() {
        return valorComissao;
    }

    public void setValorComissao(BigDecimal valorComissao) {
        this.valorComissao = valorComissao;
    }

    public String getStatusContrato() {
        return statusContrato;
    }

    public void setStatusContrato(String statusContrato) {
        this.statusContrato = statusContrato;
    }

    public Integer getDiaVencimento() {
        return diaVencimento;
    }

    public void setDiaVencimento(Integer diaVencimento) {
        this.diaVencimento = diaVencimento;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public BigDecimal getValorAluguel() {
        return valorAluguel;
    }

    public void setValorAluguel(BigDecimal valorAluguel) {
        this.valorAluguel = valorAluguel;
    }

    public LocalDate getDataRescisao() {
        return dataRescisao;
    }

    public void setDataRescisao(LocalDate dataRescisao) {
        this.dataRescisao = dataRescisao;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Imovel getImovel() {
        return imovel;
    }

    public void setImovel(Imovel imovel) {
        this.imovel = imovel;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Corretor getCorretor() {
        return corretor;
    }

    public void setCorretor(Corretor corretor) {
        this.corretor = corretor;
    }

    public Fiador getFiador() {
        return fiador;
    }

    public void setFiador(Fiador fiador) {
        this.fiador = fiador;
    }

    @JsonIgnore
    public boolean isRescindido() {
        return dataRescisao != null;
    }

    @JsonIgnore
    public boolean isAtivo() {
        return STATUS_ATIVO.equalsIgnoreCase(statusContrato);
    }

    @JsonIgnore
    public ContratoPainelDTO getDTO() {
        Proprietario p = imovel.getProprietario();
        return ContratoPainelDTO.locacao(
                idLocacao,
                dataInicio,
                cliente.getNome(),
                p.isFisico() ? p.getNome() : p.getRazaoSocial(),
                imovel.getEndereco().montarEndereco(),
                valorAluguel,
                valorComissao,
                garantia,
                valorCaucao,
                prazoMes,
                diaVencimento,
                statusContrato,
                dataRescisao,
                multa,
                motivo
        );
    }

    @JsonIgnore
    public RescisaoSimulacaoDTO simularRescisao() {
        LocalDate hoje = LocalDate.now();
        LocalDate dataRescisao = hoje.isBefore(dataInicio) ? dataInicio : hoje;

        int prazo = prazoMes;
        int cumpridos = (int) Math.min(prazo, ChronoUnit.MONTHS.between(dataInicio, dataRescisao));
        int restantes = prazo - cumpridos;

        BigDecimal multa = restantes <= 0
                ? BigDecimal.ZERO.setScale(2)
                : valorAluguel.multiply(BigDecimal.valueOf(ALUGUEIS_DE_MULTA))
                                        .multiply(BigDecimal.valueOf(restantes))
                                        .divide(BigDecimal.valueOf(prazo), 2, RoundingMode.HALF_UP);

        return new RescisaoSimulacaoDTO(dataRescisao, valorAluguel, prazo, cumpridos, restantes, multa);
    }
}
