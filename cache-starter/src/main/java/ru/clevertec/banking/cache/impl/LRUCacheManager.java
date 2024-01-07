package ru.clevertec.banking.cache.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;
import ru.clevertec.banking.cache.property.LRUCacheProperty;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LRUCacheManager extends AbstractCacheManager {
    private final CacheProperties cacheProperties;
    private final LRUCacheProperty lruCacheProperty;

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return cacheProperties.getCacheNames()
                .stream()
                .map(this::getMissingCache)
                .collect(Collectors.toSet());
    }

    @Override
    protected Cache getMissingCache(String name) {
        return new LRUCacheImpl(name, lruCacheProperty.getCapacity());
    }
}
