package com.clevertec.starter.configuration;

import com.clevertec.starter.aspect.ControllerLoggingAspect;
import com.clevertec.starter.aspect.LoggableAnnotationAspect;
import com.clevertec.starter.property.AutoLoggingControllerProperty;
import com.clevertec.starter.property.AutoLoggingProperty;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

@AutoConfiguration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@EnableConfigurationProperties({AutoLoggingProperty.class, AutoLoggingControllerProperty.class})
@ConditionalOnProperty(value = "clevertec.logging.enable", havingValue = "true", matchIfMissing = true)
public class LoggingAutoConfiguration {

    @Bean
    @ConditionalOnProperty(value = "clevertec.logging.controller.enable", havingValue = "true", matchIfMissing = true)
    public ControllerLoggingAspect controllerLoggingAspect(){
        return new ControllerLoggingAspect();
    }

    @Bean
    public LoggableAnnotationAspect loggableAnnotationAspect(){
        return new LoggableAnnotationAspect();
    }
}
