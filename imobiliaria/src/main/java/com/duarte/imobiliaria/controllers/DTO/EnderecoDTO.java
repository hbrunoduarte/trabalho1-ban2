package com.duarte.imobiliaria.controllers.DTO;

import com.duarte.imobiliaria.formsValidation.groups.ValidacaoAvancada;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoBasica;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoFormato;
import com.duarte.imobiliaria.formsValidation.util.ValidacaoConstants;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.*;

@GroupSequence({ValidacaoBasica.class, ValidacaoFormato.class, ValidacaoAvancada.class, EnderecoDTO.class})
public class EnderecoDTO {

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Pattern(regexp = ValidacaoConstants.CEP_PATTERN, message = "{cadastro.cep.formato}", groups = ValidacaoFormato.class)
    private String cep;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Pattern(regexp = ValidacaoConstants.UF_PATTERN, message = "{cadastro.estado.formato}", groups = ValidacaoFormato.class)
    private String estado;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Size(max = ValidacaoConstants.CIDADE_MAX_SIZE, message = "{cadastro.cidade.size}", groups = ValidacaoAvancada.class)
    private String cidade;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Size(max = ValidacaoConstants.BAIRRO_MAX_SIZE, message = "{cadastro.bairro.size}", groups = ValidacaoAvancada.class)
    private String bairro;

    @NotNull(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Positive(message = "{cadastro.numero.erro}", groups = ValidacaoAvancada.class)
    @Max(value = ValidacaoConstants.NUMERO_ENDERECO_MAX, message = "{cadastro.numero.erro}", groups = ValidacaoAvancada.class)
    private Integer numero;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Size(max = ValidacaoConstants.RUA_MAX_SIZE, message = "{cadastro.rua.size}", groups = ValidacaoAvancada.class)
    private String rua;

    @Size(max = ValidacaoConstants.COMPLEMENTO_MAX_SIZE, message = "{cadastro.complemento.size}", groups = ValidacaoAvancada.class)
    private String complemento;

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
}
