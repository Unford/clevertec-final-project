package ru.clevertec.banking.customer.client.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@Configuration
public class FeignConfiguration {
    @Bean
    public AuthorizationRequestInterceptor authorizationRequestInterceptor(){
        return new AuthorizationRequestInterceptor();
    }

    @Bean(name = "feignThreadPoolTaskExecutor")
    public ThreadPoolTaskExecutor feignTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setThreadNamePrefix("feign-thread-");
        return executor;
    }

    @Bean(name = "delegatingSecurityContextAsyncTaskExecutor")
    public DelegatingSecurityContextAsyncTaskExecutor taskExecutor(
            @Qualifier("feignThreadPoolTaskExecutor") ThreadPoolTaskExecutor delegate) {
        return new DelegatingSecurityContextAsyncTaskExecutor(delegate);
    }
}
