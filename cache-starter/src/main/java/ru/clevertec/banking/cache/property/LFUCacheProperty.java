package ru.clevertec.banking.cache.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.cache.clevertec.lfu")
public class LFUCacheProperty {
    private int capacity = 256;
}
