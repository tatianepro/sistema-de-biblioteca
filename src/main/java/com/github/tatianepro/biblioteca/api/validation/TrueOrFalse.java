package com.github.tatianepro.biblioteca.api.validation;


import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.constraints.ConstraintComposition;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ConstraintComposition(CompositionType.OR)
@AssertTrue
@AssertFalse
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface TrueOrFalse {

    String message() default "The property must have status true or false";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
