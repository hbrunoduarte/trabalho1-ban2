package com.duarte.imobiliaria.formsValidation.validationAnnotations;

import com.duarte.imobiliaria.formsValidation.validators.CnpjValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CnpjValidator.class)
public @interface Cnpj {

    String message() default "{cadastro.cnpj.invalido}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}