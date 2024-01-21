package ru.clevertec.banking.security.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

import org.springframework.core.annotation.Order;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.clevertec.banking.security.filter.ExceptionFilter;
import ru.clevertec.banking.security.filter.JwtAuthorizationFilter;
import ru.clevertec.banking.security.model.AuthTokenProvider;
import ru.clevertec.banking.security.model.Role;
import ru.clevertec.banking.security.service.JwtTokenService;


@AutoConfiguration(before = {SecurityAutoConfiguration.class})
@org.springframework.context.annotation.Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnMissingBean(SecurityFilterChain.class)
public class AuthAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    UserDetailsService emptyDetailsService() {
        return username -> { throw new UsernameNotFoundException("No local users, only JWT tokens allowed"); };
    }


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
                                            ExceptionFilter exceptionFilter,
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
    ExceptionFilter exceptionFilter(HandlerExceptionResolver handlerExceptionResolver) {
        return new ExceptionFilter(handlerExceptionResolver);
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

    @Bean
    static RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(Role.SUPER_USER.toUpperStringRole() + " > " + Role.ADMIN.toUpperStringRole());
        return hierarchy;
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }


}
