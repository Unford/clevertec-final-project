package ru.clevertec.banking.cache.operation;

import org.springframework.cache.interceptor.CacheOperation;

public class CacheExistOperation extends CacheOperation {

    public CacheExistOperation(Builder b) {
        super(b);
    }
    public static class Builder extends CacheOperation.Builder {


        @Override
        public CacheExistOperation build() {
            return new CacheExistOperation(this);
        }
    }
}
