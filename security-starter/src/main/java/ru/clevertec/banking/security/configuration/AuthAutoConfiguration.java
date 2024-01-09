package ru.clevertec.banking.security.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.clevertec.banking.security.filter.FilterExceptionFilter;
import ru.clevertec.banking.security.filter.JwtAuthorizationFilter;
import ru.clevertec.banking.security.model.AuthTokenProvider;
import ru.clevertec.banking.security.service.JwtTokenService;


@AutoConfiguration(before = {SecurityAutoConfiguration.class})
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnMissingBean(SecurityFilterChain.class)
public class AuthAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>
    requestMatcherRegistryCustomizer() {
        return req -> req.anyRequest().permitAll();
    }

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            JwtAuthorizationFilter jwtFilter,
                                            FilterExceptionFilter exceptionFilter,
                                            Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> registryCustomizer)
            throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(registryCustomizer)
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(exceptionFilter, LogoutFilter.class)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    JwtAuthorizationFilter jwtAuthorizationFilter(JwtTokenService tokenService, AuthTokenProvider authTokenProvider) {
        return new JwtAuthorizationFilter(tokenService, authTokenProvider);
    }

    @Bean
    FilterExceptionFilter filterExceptionFilter(HandlerExceptionResolver handlerExceptionResolver) {
        return new FilterExceptionFilter(handlerExceptionResolver);
    }


    @Bean
    @RequestScope
    AuthTokenProvider authTokenProvider(HttpServletRequest httpServletRequest) {
        return new AuthTokenProvider(httpServletRequest);
    }

    @Bean
    JwtTokenService jwtTokenService() {
        return new JwtTokenService();
    }


}
