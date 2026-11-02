package com.duarte.imobiliaria.formsValidation.validators;

import com.duarte.imobiliaria.controllers.DTO.ProprietarioDTO;
import com.duarte.imobiliaria.formsValidation.validationAnnotations.TipoPessoaConsistente;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TipoPessoaConsistenteValidator implements ConstraintValidator<TipoPessoaConsistente, ProprietarioDTO> {

    @Override
    public boolean isValid(ProprietarioDTO dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getTipo() == null) {
            return true; // o campo "tipo" tem a sua própria validação
        }

        context.disableDefaultConstraintViolation();
        boolean valido = true;

        if (dto.getTipo().equals("F")) {
            valido &= exigir(context, "cpf", dto.getCpf());
            valido &= exigir(context, "nome", dto.getNome());
        } else if (dto.getTipo().equals("J")) {
            valido &= exigir(context, "cnpj", dto.getCnpj());
            valido &= exigir(context, "razaoSocial", dto.getRazaoSocial());
        }

        return valido;
    }

    private boolean exigir(ConstraintValidatorContext context, String campo, String valor) {
        if (valor != null && !valor.isBlank()) {
            return true;
        }
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(campo)
                .addConstraintViolation();
        return false;
    }
}
