package com.duarte.imobiliaria.model;

import com.duarte.imobiliaria.controllers.DTO.FotoDTO;

public class Foto {

    private Integer idFoto;
    private String tipo;
    private byte[] imagem;
    private int idImovel;

    public Foto() {}

    public Foto(Integer idFoto) {
        this.idFoto = idFoto;
    }

    public Foto(Integer idFoto, String tipo, byte[] imagem, int idImovel) {
        this.idFoto = idFoto;
        this.tipo = tipo;
        this.imagem = imagem;
        this.idImovel = idImovel;
    }

    public Foto(FotoDTO dto) {
        tipo = dto.getTipo();
        imagem = dto.getImagem();
    }

    public Foto(Integer idImovel, FotoDTO dto) {
        this(dto);
        this.idImovel = idImovel;
    }

    public Integer getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(Integer idFoto) {
        this.idFoto = idFoto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public int getIdImovel() {
        return idImovel;
    }

    public void setIdImovel(int idImovel) {
        this.idImovel = idImovel;
    }
}
