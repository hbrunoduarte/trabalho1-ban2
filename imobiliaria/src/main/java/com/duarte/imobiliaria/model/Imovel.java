package com.duarte.imobiliaria.model;

import com.duarte.imobiliaria.controllers.DTO.ImovelPainelDTO;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoAvancada;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoBasica;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoFormato;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@GroupSequence({ValidacaoBasica.class, ValidacaoFormato.class, ValidacaoAvancada.class, Imovel.class})
public class Imovel {

    private Integer idImovel;

    @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Positive(message = "{cadastro.tipoimovel.obrigatorio}", groups = ValidacaoAvancada.class)
    private Integer tipo;

    @NotNull(message = "{cadastro.endereco.obrigatorio}", groups = ValidacaoBasica.class)
    private Endereco endereco;

    private Proprietario proprietario;

    @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Positive(message = "{imovel.area.invalida}", groups = ValidacaoAvancada.class)
    private Float areaTotal;

    @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @PositiveOrZero(message = "{imovel.quantidade.invalida}", groups = ValidacaoAvancada.class)
    private Integer qtdDormitorios;

    @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @PositiveOrZero(message = "{imovel.quantidade.invalida}", groups = ValidacaoAvancada.class)
    private Integer qtdBanheiros;

    @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @PositiveOrZero(message = "{imovel.quantidade.invalida}", groups = ValidacaoAvancada.class)
    private Integer qtdVagas;

    @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Min(value = 1500, message = "{imovel.ano.invalido}", groups = ValidacaoAvancada.class)
    @Max(value = 2100, message = "{imovel.ano.invalido}", groups = ValidacaoAvancada.class)
    private Integer anoConstrucao;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Size(max = 150, message = "{imovel.descricao.size}", groups = ValidacaoAvancada.class)
    private String descricao;

    @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Digits(integer = 13, fraction = 2, message = "{imovel.valor.invalido}", groups = ValidacaoFormato.class)
    @Positive(message = "{imovel.valor.invalido}", groups = ValidacaoAvancada.class)
    private BigDecimal valor;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Pattern(regexp = "indisponível|disponível para venda|disponível para locação", message = "{imovel.status.invalido}")
    private String status;

    public Imovel() {}

    public Imovel(Integer idImovel) {
        this.idImovel = idImovel;
    }

    public Imovel(Integer idImovel, Integer tipo, Endereco endereco, Proprietario proprietario, Float areaTotal, Integer qtdDormitorios, Integer qtdBanheiros, Integer qtdVagas, Integer anoConstrucao, String descricao, BigDecimal valor, String status) {
        this.idImovel = idImovel;
        this.tipo = tipo;
        this.endereco = endereco;
        this.proprietario = proprietario;
        this.areaTotal = areaTotal;
        this.qtdDormitorios = qtdDormitorios;
        this.qtdBanheiros = qtdBanheiros;
        this.qtdVagas = qtdVagas;
        this.anoConstrucao = anoConstrucao;
        this.descricao = descricao;
        this.valor = valor;
        this.status = status;
    }

    public Integer getIdImovel() {
        return idImovel;
    }

    public void setIdImovel(Integer idImovel) {
        this.idImovel = idImovel;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Proprietario getProprietario() {
        return proprietario;
    }

    public void setProprietario(Proprietario proprietario) {
        this.proprietario = proprietario;
    }

    public Float getAreaTotal() {
        return areaTotal;
    }

    public void setAreaTotal(Float areaTotal) {
        this.areaTotal = areaTotal;
    }

    public Integer getQtdDormitorios() {
        return qtdDormitorios;
    }

    public void setQtdDormitorios(Integer qtdDormitorios) {
        this.qtdDormitorios = qtdDormitorios;
    }

    public Integer getQtdBanheiros() {
        return qtdBanheiros;
    }

    public void setQtdBanheiros(Integer qtdBanheiros) {
        this.qtdBanheiros = qtdBanheiros;
    }

    public Integer getQtdVagas() {
        return qtdVagas;
    }

    public void setQtdVagas(Integer qtdVagas) {
        this.qtdVagas = qtdVagas;
    }

    public Integer getAnoConstrucao() {
        return anoConstrucao;
    }

    public void setAnoConstrucao(Integer anoConstrucao) {
        this.anoConstrucao = anoConstrucao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonIgnore
    public String tipoContrato() {
        if (isVenda())
            return "VENDA";
        else if (isLocacao())
            return "LOCAÇÃO";
        else return "";
    }

    @JsonIgnore
    public boolean isVenda() {
        return status != null && status.equalsIgnoreCase("disponível para venda");
    }

    @JsonIgnore
    public boolean isLocacao() {
        return status != null && status.equalsIgnoreCase("disponível para locação");
    }

    @JsonIgnore
    public ImovelPainelDTO getDTO(String tipoImovel, Integer idPublicacao) {
        return new ImovelPainelDTO(
                idImovel,
                tipoImovel,
                endereco.montarEndereco(),
                areaTotal,
                qtdDormitorios,
                qtdBanheiros,
                qtdVagas,
                anoConstrucao,
                descricao,
                valor,
                status,
                idPublicacao
        );
    }
}
