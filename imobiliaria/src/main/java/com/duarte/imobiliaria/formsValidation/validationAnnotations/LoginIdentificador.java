package com.duarte.imobiliaria.formsValidation.validationAnnotations;

import com.duarte.imobiliaria.formsValidation.validators.LoginIdentificadorValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LoginIdentificadorValidator.class)
public @interface LoginIdentificador {

    String message() default "{erro.login}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
