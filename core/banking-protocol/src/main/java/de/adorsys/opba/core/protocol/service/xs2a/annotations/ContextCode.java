package de.adorsys.opba.core.protocol.service.xs2a.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
public @interface ContextCode {

    /**
     * Direct path in context class.
     */
    String value() default "";

    /**
     * Prefix how to reach field in context class that will be appended with validation error path.
     */
    String prefix() default "";
}