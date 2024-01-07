package ru.clevertec.banking.cache.configuration;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotationMetadata;
import ru.clevertec.banking.cache.impl.LFUCacheManager;
import ru.clevertec.banking.cache.impl.LRUCacheManager;
import ru.clevertec.banking.cache.property.LFUCacheProperty;
import ru.clevertec.banking.cache.property.LRUCacheProperty;

import java.util.List;


@AutoConfiguration(before = {org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration.class})
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@EnableConfigurationProperties({LRUCacheProperty.class, LFUCacheProperty.class, CacheProperties.class})
@ConditionalOnMissingBean(CacheManager.class)
public class CacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CacheManagerCustomizers cacheManagerCustomizers(ObjectProvider<CacheManagerCustomizer<?>> customizers) {
        return new CacheManagerCustomizers(customizers.orderedStream().toList());
    }

    @Bean
    @Profile("lru")
    public LRUCacheManager lruCacheManager(CacheProperties cacheProperties,
                                           LRUCacheProperty lruCacheProperty,
                                           CacheManagerCustomizers cacheManagerCustomizers) {
        return cacheManagerCustomizers.customize(new LRUCacheManager(cacheProperties, lruCacheProperty));
    }


    @Bean
    @Profile("lfu")
    public LFUCacheManager lfuCacheManager(CacheProperties cacheProperties,
                                           LFUCacheProperty lfuCacheProperty,
                                           CacheManagerCustomizers cacheManagerCustomizers) {
        LFUCacheManager lfuCacheManager = new LFUCacheManager(lfuCacheProperty.getCapacity());
        List<String> cacheNames = cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            lfuCacheManager.setCacheNames(cacheNames);
        }
        return cacheManagerCustomizers.customize(lfuCacheManager);
    }




}
