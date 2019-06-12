package com.example.miaosha.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributeLimitAnno {
     String limitKey() default "limit";
     int limit() default 1;
     int seconds() default 1;
}
