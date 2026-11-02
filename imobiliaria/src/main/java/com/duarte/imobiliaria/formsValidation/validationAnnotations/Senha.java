package com.duarte.imobiliaria.formsValidation.validationAnnotations;

import com.duarte.imobiliaria.formsValidation.validators.CnpjValidator;
import com.duarte.imobiliaria.formsValidation.validators.SenhaValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SenhaValidator.class)
public @interface Senha {

    String message() default "{cadastro.senha.formato}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
