package com.duarte.imobiliaria.formsValidation.validators;

import com.duarte.imobiliaria.formsValidation.util.ValidacaoConstants;
import com.duarte.imobiliaria.formsValidation.validationAnnotations.Senha;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class SenhaValidator implements ConstraintValidator<Senha, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return Pattern.matches(ValidacaoConstants.SENHA_PATTERN, s);
    }
}
