package com.duarte.imobiliaria.formsValidation.validators;

import com.duarte.imobiliaria.formsValidation.validationAnnotations.LoginIdentificador;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;

public class LoginIdentificadorValidator implements ConstraintValidator<LoginIdentificador, String> {

    @Override
    public boolean isValid(String identificador, ConstraintValidatorContext constraintValidatorContext) {

        EmailValidator emailValidator = new EmailValidator();
        CpfValidator cpfValidator = new CpfValidator();
        CnpjValidator cnpjValidator = new CnpjValidator();

        return emailValidator.isValid(identificador, constraintValidatorContext)
            || cpfValidator.isFormatoValido(identificador)
            || cnpjValidator.isFormatoValido(identificador);
    }
}
