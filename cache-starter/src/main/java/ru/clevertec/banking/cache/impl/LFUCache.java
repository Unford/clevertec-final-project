package ru.clevertec.banking.cache.impl;

import org.springframework.cache.Cache;
import org.springframework.cache.support.NullValue;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.core.serializer.support.SerializationDelegate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LFUCache implements Cache {
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock.writeLock();
    private final ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock.readLock();

    private final String name;

    @Nullable
    private final SerializationDelegate serialization;

    private final boolean allowNullValues;

    private final Map<Object, Node> valueMap;
    private final Map<Object, Integer> countMap;
    private final TreeMap<Integer, DoubleLinkedList> freqMap;
    private int size;


    public LFUCache(String name) {
        this(name, 256, true);
    }

    public LFUCache(String name, boolean allowNullValues) {
        this(name, 256, allowNullValues);
    }

    public LFUCache(String name, int initialCapacity, boolean allowNullValues) {
        this(name, initialCapacity, allowNullValues, null);
    }

    protected LFUCache(String name, int initialCapacity,
                       boolean allowNullValues, @Nullable SerializationDelegate serialization) {

        this.allowNullValues = allowNullValues;
        Assert.notNull(name, "Name must not be null");
        Assert.notNull(initialCapacity, "initialCapacity must not be null");
        this.name = name;
        this.freqMap = new TreeMap<>();
        this.countMap = new HashMap<>();
        this.valueMap = new HashMap<>();
        this.size = initialCapacity;
        this.serialization = serialization;
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this.freqMap;
    }

    @Override
    public ValueWrapper get(Object key) {
        return toValueWrapper(lookup(key));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return (T) fromStoreValue(this.valueMap.computeIfAbsent(key, k -> {
            try {
                return new Node(key, toStoreValue(valueLoader.call()));
            } catch (Throwable ex) {
                throw new ValueRetrievalException(key, valueLoader, ex);
            }
        }).getValue());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, @Nullable Class<T> type) {
        Object value = fromStoreValue(lookup(key));
        if (value != null && type != null && !type.isInstance(value)) {
            throw new IllegalStateException(
                    "Cached value is not of required type [" + type.getName() + "]: " + value);
        }
        return (T) value;
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        addToLFU(key, toStoreValue(value));
    }

    @Override
    public void evict(Object key) {
        removeFromLFU(key);
    }

    @Override
    public void clear() {
        clearLFU();
    }

    @Nullable
    protected Object lookup(Object key) {
        return getFromLFU(key);
    }

    @Nullable
    protected ValueWrapper toValueWrapper(@Nullable Object storeValue) {
        return (storeValue != null ? new SimpleValueWrapper(fromStoreValue(storeValue)) : null);
    }

    @Nullable
    protected Object fromStoreValue(@Nullable Object storeValue) {
        if (this.allowNullValues && storeValue == NullValue.INSTANCE) {
            return null;
        }
        return storeValue;
    }

    protected Object toStoreValue(@Nullable Object userValue) {
        Object storeValue = checkNullable(userValue);
        if (this.serialization != null) {
            try {
                return this.serialization.serializeToByteArray(storeValue);
            } catch (Throwable ex) {
                throw new IllegalArgumentException("Failed to serialize cache value '" + userValue +
                        "'. Does it implement Serializable?", ex);
            }
        } else {
            return storeValue;
        }
    }

    protected Object checkNullable(@Nullable Object userValue) {
        if (userValue == null) {
            if (this.allowNullValues) {
                return NullValue.INSTANCE;
            }
            throw new IllegalArgumentException(
                    "Cache '" + getName() + "' is configured to not allow null values but null was provided");
        }
        return userValue;
    }

    protected Object getFromLFU(Object key) {
        if (!valueMap.containsKey(key) || size == 0) {
            return null;
        }
        Node nodeToDelete;
        Node node;
        readLock.lock();
        try {
            nodeToDelete = valueMap.get(key);
            node = new Node(key, nodeToDelete.value);
        } finally {
            readLock.unlock();
        }
        deleteAndUpdate(key, node, nodeToDelete);

        return valueMap.get(key).getValue();
    }

    protected void addToLFU(Object key, Object value) {
        writeLock.lock();
        try {
            if (!valueMap.containsKey(key) && size > 0) {
                Node node = new Node(key, value);
                if (valueMap.size() == size) {
                    int lowCount = freqMap.firstKey();
                    Node nodeToDelete = freqMap.get(lowCount).head;
                    freqMap.get(lowCount).remove(nodeToDelete);
                    removeListIfEmpty(lowCount);

                    Object keyToDelete = nodeToDelete.getKey();

                    valueMap.remove(keyToDelete);
                    countMap.remove(keyToDelete);
                }
                freqMap.computeIfAbsent(1, x -> new DoubleLinkedList()).add(node);
                valueMap.put(key, node);
                countMap.put(key, 1);
            } else if (size > 0) {
                Node node = new Node(key, value);
                Node nodeToDelete = valueMap.get(key);
                deleteAndUpdate(key, node, nodeToDelete);
            }
        } finally {
            writeLock.unlock();
        }
    }

    protected Object removeFromLFU(Object key) {
        writeLock.lock();
        try {
            if (valueMap.containsKey(key)) {
                Node nodeToDelete = valueMap.get(key);
                int freq = countMap.get(key);

                freqMap.get(freq).remove(nodeToDelete);
                removeListIfEmpty(freq);
                valueMap.remove(key);
                countMap.remove(key);

                return nodeToDelete.getValue();
            } else return null;
        } finally {
            writeLock.unlock();
        }
    }

    protected void clearLFU() {
        writeLock.lock();
        try {
            valueMap.clear();
            freqMap.clear();
            countMap.clear();
        } finally {
            writeLock.unlock();
        }
    }

    private void deleteAndUpdate(Object key, Node node, Node nodeToDelete) {
        int freq = countMap.get(key);

        writeLock.lock();
        try {
            freqMap.get(freq).remove(nodeToDelete);
            removeListIfEmpty(freq);
            valueMap.remove(key);
            countMap.remove(key);

            valueMap.put(key, node);
            countMap.put(key, freq + 1);

            freqMap.computeIfAbsent(freq + 1, x -> new DoubleLinkedList()).add(node);
        } finally {
            writeLock.unlock();
        }
    }

    private void removeListIfEmpty(int freq) {
        writeLock.lock();
        try {
            if (freqMap.get(freq).len() == 0) {
                freqMap.remove(freq);
            }
        } finally {
            writeLock.unlock();
        }
    }

    private class Node {
        private Object key;
        private Object value;
        Node next;
        Node prev;

        public Node(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value;
        }
    }

    private class DoubleLinkedList {
        private int n;
        private Node head;
        private Node tail;

        public void add(Node node) {
            if (head == null) {
                head = node;
            } else {
                tail.next = node;
                node.prev = tail;
            }
            tail = node;
            n++;
        }

        public void remove(Node node) {
            if (node.next == null) {
                tail = node.prev;
            } else {
                node.next.prev = node.prev;
            }
            if (node.prev == null) {
                head = node.next;
            } else {
                node.prev.next = node.next;
            }
            n--;
        }

        public Node head() {
            return head;
        }

        public Node tail() {
            return tail;
        }

        public int len() {
            return n;
        }
    }
}
