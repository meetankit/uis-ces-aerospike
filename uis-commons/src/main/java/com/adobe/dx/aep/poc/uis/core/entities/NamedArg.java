package com.adobe.dx.aep.poc.uis.core.entities;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface NamedArg {
    String value();

    String defaultValue() default "";
}