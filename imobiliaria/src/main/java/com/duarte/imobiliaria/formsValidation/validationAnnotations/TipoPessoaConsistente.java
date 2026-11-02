package com.duarte.imobiliaria.formsValidation.validationAnnotations;

import com.duarte.imobiliaria.formsValidation.validators.TipoPessoaConsistenteValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TipoPessoaConsistenteValidator.class)
public @interface TipoPessoaConsistente {

    String message() default "{cadastro.campo.obrigatorio}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}