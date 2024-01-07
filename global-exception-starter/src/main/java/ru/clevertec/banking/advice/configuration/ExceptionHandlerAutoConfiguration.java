package ru.clevertec.banking.advice.configuration;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Role;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.clevertec.banking.advice.handler.GlobalExceptionHandler;

@AutoConfiguration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@ConditionalOnMissingBean(ResponseEntityExceptionHandler.class)
public class ExceptionHandlerAutoConfiguration {

    @RestControllerAdvice
    private class SpecificGlobalExceptionHandler extends GlobalExceptionHandler {
    }
}
