package com.duarte.imobiliaria.formsValidation;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public class CadastroForm {

    @NotNull(message = "{cadastro.campo.obrigatorio}")
    private Boolean isCliente;

    @NotNull(message = "{cadastro.campo.obrigatorio}")
    @NotBlank(message = "{cadastro.campo.obrigatorio}")
    @Size(min = ValidacaoConstants.NOME_MIN_SIZE, max = ValidacaoConstants.NOME_MAX_SIZE, message = "{cadastro.nome.size}")
    private String nome;

    @NotNull(message = "{cadastro.campo.obrigatorio}")
    @Past(message = "A data de nascimento deve estar no passado")
    private LocalDate dataNascimento;

    @NotBlank(message = "{cadastro.campo.obrigatorio}")
    @Pattern(regexp = "([0-9]{2}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[\\/]?[0-9]{4}[-]?[0-9]{2})|([0-9]{3}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[-]?[0-9]{2})",
            message = "Formato de CPF ou CNPJ inválido")
    private String cpf_cnpj;

    @Size(max = ValidacaoConstants.PROFISSAO_MAX_SIZE, message = "{cadastro.profissao.size}")
    private String profissao;

    @NotNull(message = "A renda mensal é obrigatória")
    @PositiveOrZero(message = "A renda mensal não pode ser negativa")
    private Float rendaMensal;

    @NotEmpty(message = "{cadastro.telefones.insuficientes}")
    @Size(max = ValidacaoConstants.TELEFONES_MAX_SIZE, message = "{cadastro.telefones.size}")
    private List<
            @NotBlank(message = "{cadastro.campo.obrigatorio}")
            @Pattern(regexp = "\\d{10,11}", message = "{cadastro.telefone.formato}")
                    String
            > telefones;

    @NotBlank(message = "{cadastro.campo.obrigatorio}")
    @Email(message = "O formato do e-mail é inválido")
    private String email;

    @NotBlank(message = "{cadastro.campo.obrigatorio}")
    @Size(min = ValidacaoConstants.SENHA_MIN_SIZE, message = "{cadastro.senha.size}")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_]).*$",
            message = "{cadastro.senha.formato}")
    private String senha;
}
