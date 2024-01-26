package ru.clevertec.banking.controller.security;

import org.springframework.core.annotation.AliasFor;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithUUIDJwtUserSecurityContextFactory.class)
public @interface WithMockUUIDJwtUser {
    /**
     * The value should be UUID String default value UUID.randomUIID()
     *
     * @return
     */
    String value() default "";

    String[] roles() default {"USER"};

    String details() default "";

    @AliasFor(annotation = WithSecurityContext.class)
    TestExecutionEvent setupBefore() default TestExecutionEvent.TEST_METHOD;
}
