package com.duarte.imobiliaria.controllers.DTO;

import com.duarte.imobiliaria.formsValidation.groups.ValidacaoAvancada;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoBasica;
import com.duarte.imobiliaria.formsValidation.groups.ValidacaoFormato;
import com.duarte.imobiliaria.formsValidation.util.ValidacaoConstants;
import com.duarte.imobiliaria.formsValidation.validationAnnotations.Cpf;
import jakarta.validation.Valid;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

@GroupSequence({ValidacaoBasica.class, ValidacaoFormato.class, ValidacaoAvancada.class, FiadorCadastroDTO.class})
public class FiadorCadastroDTO {

    public static final int COMPROVANTE_MAX_BYTES = 5 * 1024 * 1024;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Size(min = ValidacaoConstants.NOME_MIN_SIZE, max = ValidacaoConstants.NOME_MAX_DB_SIZE, message = "{cadastro.nome.size}", groups = ValidacaoAvancada.class)
    private String nome;

    @NotBlank(message = "{cadastro.campo.obrigatorio}", groups = ValidacaoBasica.class)
    @Pattern(regexp = ValidacaoConstants.CPF_DIGITOS_PATTERN, message = "{cadastro.cpf.formato}", groups = ValidacaoFormato.class)
    @Cpf(message = "{cadastro.cpf.invalido}", groups = ValidacaoAvancada.class)
    private String cpf;

    // Enviado em Base64 no JSON (mesmo formato do FiadorController.FiadorDTO)
    @NotEmpty(message = "Anexe o comprovante de renda do fiador", groups = ValidacaoBasica.class)
    @Size(max = COMPROVANTE_MAX_BYTES, message = "O comprovante de renda deve ter no máximo 5 MB", groups = ValidacaoAvancada.class)
    private byte[] comprovanteRenda;

    @NotEmpty(message = "{cadastro.telefones.insuficientes}", groups = ValidacaoBasica.class)
    @Size(max = ValidacaoConstants.TELEFONES_MAX_SIZE, message = "{cadastro.telefones.size}", groups = ValidacaoAvancada.class)
    private List<@Valid TelefoneDTO> telefones;

    @NotNull(message = "{cadastro.endereco.obrigatorio}", groups = ValidacaoBasica.class)
    @Valid
    private EnderecoDTO endereco;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public byte[] getComprovanteRenda() {
        return comprovanteRenda;
    }

    public void setComprovanteRenda(byte[] comprovanteRenda) {
        this.comprovanteRenda = comprovanteRenda;
    }

    public List<TelefoneDTO> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<TelefoneDTO> telefones) {
        this.telefones = telefones;
    }

    public EnderecoDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoDTO endereco) {
        this.endereco = endereco;
    }
}
