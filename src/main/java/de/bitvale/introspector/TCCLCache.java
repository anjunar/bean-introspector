package de.bitvale.introspector;

import com.google.common.cache.*;
import com.google.common.collect.ImmutableMap;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Patrick Bittner on 03.05.2014.
 */
public class TCCLCache<K, V> implements Cache<K, V> {

    private final LoadingCache<ClassLoader, Cache<K, V>> tcclCache = CacheBuilder
            .newBuilder()
            .weakKeys()
            .build(new CacheLoader<ClassLoader, Cache<K, V>>() {
                @Override
                public Cache<K, V> load(final ClassLoader key) throws Exception {
                    return CacheBuilder
                            .newBuilder()
                            .expireAfterAccess(10, TimeUnit.MINUTES)
                            .build();
                }
            });

    private Cache<K, V> getTCCLCache() {
        try {
            final ClassLoader contextClassLoader;
            if (System.getSecurityManager() == null) {
                contextClassLoader = Thread.currentThread().getContextClassLoader();
            } else {
                contextClassLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> Thread.currentThread().getContextClassLoader());
            }
            return tcclCache.get(contextClassLoader);
        } catch (final ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }


    @Override
    public V getIfPresent(final Object key) {
        return getTCCLCache().getIfPresent(key);
    }

    @Override
    public V get(final K key, final Callable<? extends V> valueLoader) throws ExecutionException {
        return getTCCLCache().get(key, valueLoader);
    }

    @Override
    public ImmutableMap<K, V> getAllPresent(final Iterable<?> keys) {
        return getTCCLCache().getAllPresent(keys);
    }

    @Override
    public void put(final K key, final V value) {
        getTCCLCache().put(key, value);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        getTCCLCache().putAll(map);
    }

    @Override
    public void invalidate(final Object key) {
        getTCCLCache().invalidate(key);
    }

    @Override
    public void invalidateAll(final Iterable<?> keys) {
        getTCCLCache().invalidateAll(keys);
    }

    @Override
    public void invalidateAll() {
        getTCCLCache().invalidateAll();
    }

    @Override
    public long size() {
        return getTCCLCache().size();
    }

    @Override
    public CacheStats stats() {
        return getTCCLCache().stats();
    }

    @Override
    public ConcurrentMap<K, V> asMap() {
        return getTCCLCache().asMap();
    }

    @Override
    public void cleanUp() {
        getTCCLCache().cleanUp();
    }
}
