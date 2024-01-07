package ru.clevertec.banking.cache.impl;

import lombok.Getter;
import org.springframework.cache.support.AbstractValueAdaptingCache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRUCacheImpl extends AbstractValueAdaptingCache {
    private final String name;
    @Getter
    private final int capacity;
    private final Map<Object, Object> store;
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();


    public LRUCacheImpl(String name, int capacity) {
        this(true, name, capacity);
    }

    public LRUCacheImpl(boolean allowNullValues, String name, int capacity) {
        super(allowNullValues);
        this.name = name;
        this.capacity = capacity;

        this.store = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
                return size() > capacity;
            }
        };

    }


    @Override
    protected Object lookup(Object key) {
        readWriteLock.readLock().lock();
        try {
            return store.get(key);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return store;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Callable<T> valueLoader) {
        readWriteLock.readLock().lock();
        try {
            return (T) fromStoreValue(this.store.computeIfAbsent(key, k -> {
                try {
                    return toStoreValue(valueLoader.call());
                } catch (Exception ex) {
                    throw new ValueRetrievalException(key, valueLoader, ex);
                }
            }));
        } finally {
            readWriteLock.readLock().unlock();
        }

    }

    @Override
    public void put(Object key, Object value) {
        readWriteLock.writeLock().lock();
        try {
            this.store.put(key, toStoreValue(value));
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void evict(Object key) {
        readWriteLock.writeLock().lock();
        try {
            this.store.remove(key);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public void clear() {
        readWriteLock.writeLock().lock();
        try {
            this.store.clear();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

}
