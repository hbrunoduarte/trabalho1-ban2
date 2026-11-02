package com.duarte.imobiliaria.controllers.DTO;

import jakarta.validation.Valid;

import java.util.List;

public class PerfilDTO {

    @Valid
    private ClienteDTO cliente;

    @Valid
    private ProprietarioDTO proprietario;

    @Valid
    private CorretorDTO corretor;

    @Valid
    private EnderecoDTO endereco;

    private List<@Valid TelefoneDTO> telefones;

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    public ProprietarioDTO getProprietario() {
        return proprietario;
    }

    public void setProprietario(ProprietarioDTO proprietario) {
        this.proprietario = proprietario;
    }

    public CorretorDTO getCorretor() {
        return corretor;
    }

    public void setCorretor(CorretorDTO corretor) {
        this.corretor = corretor;
    }

    public EnderecoDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoDTO endereco) {
        this.endereco = endereco;
    }

    public List<TelefoneDTO> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<TelefoneDTO> telefones) {
        this.telefones = telefones;
    }
}
