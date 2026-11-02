package com.duarte.imobiliaria.formsValidation.validators;

import com.duarte.imobiliaria.formsValidation.util.DocumentoUtil;
import com.duarte.imobiliaria.formsValidation.util.ValidacaoConstants;
import com.duarte.imobiliaria.formsValidation.validationAnnotations.Cpf;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class CpfValidator implements ConstraintValidator<Cpf, String> {

    @Override
    public boolean isValid(String valor, ConstraintValidatorContext context) {
        if (valor == null || valor.isBlank()) {
            return true;
        }
        return DocumentoUtil.cpfValido(valor);
    }

    public boolean isFormatoValido(String valor) {
        if (valor == null || valor.isBlank()) return false;
        return Pattern.matches(ValidacaoConstants.CPF_PATTERN, valor);
    }
}