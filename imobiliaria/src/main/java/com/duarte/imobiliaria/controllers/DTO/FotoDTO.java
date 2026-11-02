package com.duarte.imobiliaria.controllers.DTO;

public class FotoDTO {

    private byte[] imagem;
    private String tipo;

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
