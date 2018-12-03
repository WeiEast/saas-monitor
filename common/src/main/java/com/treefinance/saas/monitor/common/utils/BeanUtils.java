package com.treefinance.saas.monitor.common.utils;

import org.springframework.cglib.beans.BeanCopier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Created by luoyihua on 2017/5/10.
 */
public final class BeanUtils {
    private BeanUtils() {

    }

    private static final Map<String, BeanCopier> beanCopierMap = new ConcurrentHashMap<>();

    /**
     * 基于CGLIB的bean properties 的拷贝，性能要远优于{@code org.springframework.beans.BeanUtils.copyProperties}
     *
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target) {
        if (source == null || target == null) {
            target = null;
            return;
        }

        String key = String.format("%s:%s", source.getClass().getName(), target.getClass().getName());
        if (!beanCopierMap.containsKey(key)) {
            BeanCopier beanCopier = BeanCopier.create(source.getClass(), target.getClass(), false);
            beanCopierMap.putIfAbsent(key, beanCopier);
        }
        BeanCopier beanCopier = beanCopierMap.get(key);
        beanCopier.copy(source, target, null);
    }

    /**
     * 对象转化
     * @param src
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T convert(Object src, T target) {
        copyProperties(src, target);
        return target;
    }

    public static <T> T checkNull(Supplier<T> t) {
        try {
            return t.get();
        } catch (NullPointerException e) {

        }
        return null;
    }

}
