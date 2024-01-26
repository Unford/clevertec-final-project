package ru.clevertec.banking.cache.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.cache.clevertec.lru")
public class LRUCacheProperty {
    private int capacity = 256;
}
