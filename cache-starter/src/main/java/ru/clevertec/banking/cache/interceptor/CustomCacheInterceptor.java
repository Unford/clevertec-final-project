package ru.clevertec.banking.cache.interceptor;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import ru.clevertec.banking.cache.operation.CacheExistOperation;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

public class CustomCacheInterceptor extends CacheInterceptor {

    @Override
    protected Object execute(CacheOperationInvoker invoker, Object target,
                             Method method, Object[] args) {

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        CacheOperationSource cacheOperationSource = getCacheOperationSource();

        if (cacheOperationSource != null) {
            Collection<CacheOperation> operations = cacheOperationSource.getCacheOperations(method, targetClass);
            if (!CollectionUtils.isEmpty(operations)) {
                List<CustomCacheOperationContext> contexts = operations.stream()
                        .filter(o -> o.getClass() == CacheExistOperation.class)
                        .map(o -> getOperationContext(o, method, args, target, targetClass))
                        .toList();
                Object object = findInContexts(contexts, invoker, method);
                if (object != null) {
                    return true;
                }
            }
        }
        return super.execute(invoker, targetClass, method, args);
    }

    private Object findInContexts(List<CustomCacheOperationContext> contexts, CacheOperationInvoker invoker, Method method) {
        for (CustomCacheOperationContext context : contexts){
            Object key = context.generateKey(null);
            Object cached = findInCaches(context, key, invoker, method);
            if (cached != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Cache entry for key '" + key + "' found in cache(s) " + context.getCacheNames());
                }
                return cached;
            } else {
                if (logger.isTraceEnabled()) {
                    logger.trace("No cache entry for key '" + key + "' in cache(s) " + context.getCacheNames());
                }
            }
        }

        return null;
    }

    @Nullable
    private Object findInCaches(CustomCacheOperationContext context, Object key,
                                CacheOperationInvoker invoker, Method method) {

        for (Cache cache : context.getCaches()) {
            Cache.ValueWrapper result = doGet(cache, key);
            if (result != null) {
                return result;
            }
        }
        return null;
    }



    @Override
    protected CustomCacheOperationContext getOperationContext(CacheOperation operation, Method method,
                                                              Object[] args, Object target, Class<?> targetClass) {
        CacheOperationMetadata metadata = getCacheOperationMetadata(operation, method, targetClass);
        return new CustomCacheOperationContext(metadata, args, target);
    }

    protected class CustomCacheOperationContext extends CacheOperationContext{

        @Override
        protected boolean isConditionPassing(Object result) {
            return super.isConditionPassing(result);
        }

        @Override
        protected boolean canPutToCache(Object value) {
            return super.canPutToCache(value);
        }

        @Override
        protected Object generateKey(Object result) {
            return super.generateKey(result);
        }

        @Override
        protected Collection<? extends Cache> getCaches() {
            return super.getCaches();
        }

        @Override
        protected Collection<String> getCacheNames() {
            return super.getCacheNames();
        }

        public CustomCacheOperationContext(CacheOperationMetadata metadata, Object[] args, Object target) {
            super(metadata, args, target);
        }
    }


}
