package com.duarte.imobiliaria.model;

import com.duarte.imobiliaria.controllers.DTO.VisitaPainelDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.time.LocalTime;

public class VisitaAgendada {

    private Integer idVisita;
    private LocalDate data;
    private LocalTime horario;
    private Corretor corretor;
    private Cliente cliente;
    private Imovel imovel;
    private Boolean isArquivada;

    public VisitaAgendada() {
    }

    public VisitaAgendada(Integer idVisita) {
        this.idVisita = idVisita;
    }

    public VisitaAgendada(Integer idVisita, LocalDate data, LocalTime horario, Corretor corretor, Cliente cliente, Imovel imovel) {
        this.idVisita = idVisita;
        this.data = data;
        this.horario = horario;
        this.corretor = corretor;
        this.cliente = cliente;
        this.imovel = imovel;
    }

    public Integer getIdVisita() {
        return idVisita;
    }

    public void setIdVisita(Integer idVisita) {
        this.idVisita = idVisita;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
        this.horario = horario;
    }

    public Corretor getCorretor() {
        return corretor;
    }

    public void setCorretor(Corretor corretor) {
        this.corretor = corretor;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Imovel getImovel() {
        return imovel;
    }

    public void setImovel(Imovel imovel) {
        this.imovel = imovel;
    }

    public Boolean getArquivada() {
        return isArquivada;
    }

    public void setArquivada(Boolean arquivada) {
        isArquivada = arquivada;
    }

    @JsonIgnore
    public Boolean isPotencialContrato() {
        return !isArquivada;
    }

    @JsonIgnore
    public VisitaPainelDTO getPainelDTO() {
        Proprietario p = imovel.getProprietario();
        return new VisitaPainelDTO(
                idVisita,
                data,
                horario,
                p.isFisico() ? p.getNome() : p.getRazaoSocial(),
                cliente.getNome(),
                imovel.getEndereco().montarEndereco(),
                imovel.tipoContrato(),
                imovel.getValor(),
                isArquivada
        );
    }
}
