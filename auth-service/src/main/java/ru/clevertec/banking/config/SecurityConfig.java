package ru.clevertec.banking.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

//    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
//    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests( requests -> requests
                        .requestMatchers("/api/v1/auth/**").permitAll()
//  Пример                .requestMatchers("/api/v1/profile/update").hasAnyAuthority("ADMIN", "USER")
//  Пример                .requestMatchers(HttpMethod.GET, "/api/v1/profile").hasAnyAuthority("SUPER_USER")
//  Пример                 .requestMatchers("/api/v1/profile/**").hasAuthority("USER")
                        .anyRequest().authenticated()
                )
                .sessionManagement( management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider);
//                .exceptionHandling(handler -> handler
//                        .accessDeniedHandler(customAccessDeniedHandler))
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
