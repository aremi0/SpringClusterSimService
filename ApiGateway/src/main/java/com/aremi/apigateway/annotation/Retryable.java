package com.aremi.apigateway.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotazione usata assieme ad AOP per effettuare il retry di un metodo
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Retryable {
    int maxAttempts() default 3;
    Class<? extends Throwable>[] retryOnExceptions() default {Exception.class};
}
