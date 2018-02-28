package com.treefinance.saas.monitor.biz.autostat.base;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by yh-treefinance on 2017/9/13.
 */
public abstract class AbstractCacheService<K, V> extends LoggerBaseService implements InitializingBean, BaseCacheService<K,V> {

    /**
     * 本地缓存
     */
    protected final LoadingCache<K, V> cache = newCache();

    /**
     * cache builder
     *
     * @return
     */
    protected CacheBuilder newBuilder() {
        return CacheBuilder.newBuilder()
                .refreshAfterWrite(5, TimeUnit.MINUTES)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(20000);
    }

    /**
     * cache
     *
     * @return
     */
    private LoadingCache<K, V> newCache() {
        return newBuilder().build(CacheLoader.from(dataLoader()));
    }

    /**
     * data loader
     *
     * @return
     */
    public abstract Function<K, V> dataLoader();


    /**
     * 根据key获取value
     *
     * @param key
     * @return
     */
    @Override
    public V get(K key) {
        if (key == null) {
            return null;
        }
        V value = null;
        try {
            value = cache.get(key);
        } catch (CacheLoader.InvalidCacheLoadException | ExecutionException e) {
            logger.info("get data from cache error:key={}", key);
        }
        return value;
    }

    /**
     * 批量更具key获取value
     *
     * @param keys
     * @return
     */
    @Override
    public List<V> getList(List<K> keys) {
        List<V> list = Lists.newArrayList();
        if (CollectionUtils.isEmpty(keys)) {
            return list;
        }
        try {
            Map<K, V> map = cache.getAll(keys);
            if (MapUtils.isNotEmpty(map)) {
                list.addAll(map.values());
            }
        } catch (ExecutionException e) {
            logger.info("get data from cache error:keys={}", JSON.toJSONString(keys));
        }
        return list;
    }

    /**
     * 获取所有
     *
     * @return
     */
    @Override
    public List<V> getAll() {
        List<V> list = Lists.newArrayList();
        Collection<V> collection = cache.asMap().values();
        if (collection != null) {
            list.addAll(collection);
        }
        return list;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
