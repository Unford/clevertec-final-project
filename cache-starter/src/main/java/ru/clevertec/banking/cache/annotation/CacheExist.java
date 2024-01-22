package ru.clevertec.banking.cache.annotation;

import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Reflective
public @interface CacheExist {
    @AliasFor("cacheName")
    String value() default "";


    @AliasFor("value")
    String cacheName() default "";


    String key() default "";

    String keyGenerator() default "";

    String cacheManager() default "";


    String cacheResolver() default "";


}
