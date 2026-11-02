package com.duarte.imobiliaria.formsValidation.validationAnnotations;

import com.duarte.imobiliaria.formsValidation.validators.CpfValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CpfValidator.class)
public @interface Cpf {

    String message() default "{cadastro.cpf.invalido}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}