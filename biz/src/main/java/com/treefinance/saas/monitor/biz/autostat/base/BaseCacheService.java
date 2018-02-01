package com.treefinance.saas.monitor.biz.autostat.base;

import java.util.List;

/**
 * Created by yh-treefinance on 2018/2/1.
 */
public interface BaseCacheService<K, V> {
    V get(K key);

    List<V> getAll();

    List<V> getList(List<K> keys);
}
