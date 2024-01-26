package ru.clevertec.banking.cache.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Role;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ru.clevertec.banking.cache.impl.LFUCacheManager;
import ru.clevertec.banking.cache.impl.LRUCacheManager;
import ru.clevertec.banking.cache.property.LFUCacheProperty;
import ru.clevertec.banking.cache.property.LRUCacheProperty;

import java.time.Duration;
import java.util.List;
import java.util.Optional;


@AutoConfiguration(before = {org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration.class, RedisAutoConfiguration.class})
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

    @Bean
    @ConditionalOnMissingBean({LFUCacheManager.class, LRUCacheManager.class, RedisCacheConfiguration.class})
    @ConditionalOnProperty(prefix = "spring.cache", name = "type", havingValue = "redis", matchIfMissing = true)
    public RedisCacheConfiguration defaultCacheConfig(CacheProperties properties, ObjectMapper objectMapper) {
        CacheProperties.Redis redisProperties = properties.getRedis();
        ObjectMapper redisObjectMapper = objectMapper.copy()
                .activateDefaultTyping(
                        objectMapper.getPolymorphicTypeValidator(),
                        ObjectMapper.DefaultTyping.EVERYTHING
                );
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Optional.ofNullable(redisProperties.getTimeToLive()).orElse(Duration.ZERO))
                .prefixCacheNameWith(Optional.ofNullable(redisProperties.getKeyPrefix()).orElse(""))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper)));
    }
}
