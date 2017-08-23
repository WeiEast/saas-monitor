package com.treefinance.saas.monitor.biz.mq.handler;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.assistant.listener.TagBaseMessageHandler;
import com.treefinance.saas.assistant.model.HttpMonitorMessage;
import com.treefinance.saas.assistant.model.MonitorTagEnum;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.RedisKeyHelper;
import com.treefinance.saas.monitor.biz.helper.StatHelper;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import com.treefinance.saas.monitor.dao.entity.ApiStatAccess;
import com.treefinance.saas.monitor.dao.entity.ApiStatMerchantDayAccess;
import com.treefinance.saas.monitor.dao.entity.ApiStatTotalAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2017/7/13.
 */
@Component
public class HttpMonitorMessageHandler implements TagBaseMessageHandler<List<HttpMonitorMessage>> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private RedisDao redisDao;

    @Override
    public MonitorTagEnum getMonitorType() {
        return MonitorTagEnum.HTTP;
    }

    @Override
    public void handleMessage(List<HttpMonitorMessage> list) {
        long start = System.currentTimeMillis();
        try {
            // 1.更新总计数据
            updateTotalData(list);
            // 2.更新商户日访问情况
            updateMerchantDayData(list);
            // 3.更新挨批访问数据
            updateApiData(list);
        } finally {
            logger.info("handleMessage cost {} ms , message={}", System.currentTimeMillis() - start, JSON.toJSONString(list));
        }


    }

    /**
     * 更新合计数据
     *
     * @param list
     */
    private void updateTotalData(List<HttpMonitorMessage> list) {
        int intervalMinutes = diamondConfig.getMonitorHttpIntervalMinutes();

        List<ApiStatTotalAccess> totalList = Lists.newArrayList();

        // API接口总访问
        list.stream()
                .collect(Collectors.groupingBy(msg -> StatHelper.calculateIntervalTime(msg.getCompleteTime(), intervalMinutes)))
                .forEach((date, httpMonitorMessages) -> {
                    ApiStatTotalAccess access = new ApiStatTotalAccess();
                    access.setDataTime(date);
                    access.setTotalCount(httpMonitorMessages.size());
                    access.setHttp2xxCount(count2xx(httpMonitorMessages));
                    access.setHttp4xxCount(count4xx(httpMonitorMessages));
                    access.setHttp5xxCount(count5xx(httpMonitorMessages));
                    access.setAvgResponseTime(sumTime(httpMonitorMessages));
                    totalList.add(access);
                });

        totalList.forEach(access -> redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {

            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                Date intervalTime = access.getDataTime();
                String key = RedisKeyHelper.keyOfHttpTotal(intervalTime);
                String dayKey = RedisKeyHelper.keyOfHttpDay(intervalTime);
                redisOperations.opsForSet().add(dayKey, intervalTime.getTime() + "");

                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);

                // 判断是否有key
                if (!Boolean.TRUE.equals(hashOperations.hasKey(key))) {
                    hashOperations.put("dataTime", intervalTime.getTime() + "");
                    // 设定超时时间默认为1天
                    hashOperations.expire(2, TimeUnit.DAYS);
                }
                hashOperations.increment("totalCount", access.getTotalCount());
                hashOperations.increment("http2xxCount", access.getHttp2xxCount());
                hashOperations.increment("http4xxCount", access.getHttp4xxCount());
                hashOperations.increment("http5xxCount", access.getHttp5xxCount());
                // avgResponseTime 为累计时间
                hashOperations.increment("avgResponseTime", access.getAvgResponseTime());
                return null;
            }
        }));
        logger.info("update http access data for total: {}", JSON.toJSONString(totalList));
    }


    /**
     * 更新商户日访问数据
     *
     * @param list
     */
    private void updateMerchantDayData(List<HttpMonitorMessage> list) {
        List<ApiStatMerchantDayAccess> merchantDayAccessList = Lists.newArrayList();
        list.stream()
                .collect(Collectors.groupingBy(msg -> StatHelper.calculateDayTime(msg.getCompleteTime()))) // 日访问数据
                .forEach(((date, httpMonitorMessages) -> httpMonitorMessages.stream()
                        .collect(Collectors.groupingBy(HttpMonitorMessage::getAppId))
                        .forEach((appId, merchantMessageList) -> {
                            ApiStatMerchantDayAccess access = new ApiStatMerchantDayAccess();
                            access.setAppId(appId);
                            access.setDataTime(date);
                            access.setTotalCount(merchantMessageList.size());
                            access.setHttp2xxCount(count2xx(merchantMessageList));
                            access.setHttp4xxCount(count4xx(merchantMessageList));
                            access.setHttp5xxCount(count5xx(merchantMessageList));
                            access.setAvgResponseTime(sumTime(merchantMessageList));
                            merchantDayAccessList.add(access);
                        })
                ));

        merchantDayAccessList.forEach(access -> redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {

            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                Date intervalTime = access.getDataTime();
                String appId = access.getAppId();

                String key = RedisKeyHelper.keyOfHttpMerchant(intervalTime, appId);
                String dayKey = RedisKeyHelper.keyOfHttpDay(intervalTime);
                String mercantListKey = RedisKeyHelper.keyOfHttpMerchantList(intervalTime);

                redisOperations.opsForSet().add(dayKey, intervalTime.getTime() + "");
                redisOperations.opsForSet().add(mercantListKey, access.getAppId());
                redisOperations.expire(mercantListKey, 2, TimeUnit.DAYS);


                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                // 判断是否有key
                if (!Boolean.TRUE.equals(hashOperations.hasKey(key))) {
                    hashOperations.put("dataTime", intervalTime.getTime() + "");
                    // 设定超时时间默认为1天
                    hashOperations.expire(2, TimeUnit.DAYS);
                }
                hashOperations.putIfAbsent("appId", appId);
                hashOperations.increment("totalCount", access.getTotalCount());
                hashOperations.increment("http2xxCount", access.getHttp2xxCount());
                hashOperations.increment("http4xxCount", access.getHttp4xxCount());
                hashOperations.increment("http5xxCount", access.getHttp5xxCount());
                // avgResponseTime 为累计时间
                hashOperations.increment("avgResponseTime", access.getAvgResponseTime());
                return null;
            }
        }));
        logger.info("update http access data for merchant: {}", JSON.toJSONString(merchantDayAccessList));
    }

    /**
     * 更新合计数据
     *
     * @param list
     */
    private void updateApiData(List<HttpMonitorMessage> list) {
        int intervalMinutes = diamondConfig.getMonitorHttpIntervalMinutes();


        List<ApiStatAccess> apiStatAccessList = Lists.newArrayList();
        // API接口总访问
        list.stream()
                .collect(Collectors.groupingBy(msg -> StatHelper.calculateIntervalTime(msg.getCompleteTime(), intervalMinutes)))
                .forEach((date, httpMonitorMessages) -> {
                    httpMonitorMessages.stream()
                            .collect(Collectors.groupingBy(HttpMonitorMessage::getRequestUrl))
                            .forEach(((apiUrl, apiMessageList) -> {
                                ApiStatAccess access = new ApiStatAccess();
                                access.setDataTime(date);
                                access.setApiUrl(apiUrl);
                                access.setTotalCount(apiMessageList.size());
                                access.setHttp2xxCount(count2xx(apiMessageList));
                                access.setHttp4xxCount(count4xx(apiMessageList));
                                access.setHttp5xxCount(count5xx(apiMessageList));
                                access.setAvgResponseTime(sumTime(apiMessageList));
                                apiStatAccessList.add(access);
                            }));
                });

        apiStatAccessList.forEach(access -> redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {

            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                Date intervalTime = access.getDataTime();
                String apiUrl = access.getApiUrl();

                String key = RedisKeyHelper.keyOfHttpApi(intervalTime, apiUrl);
                String dayKey = RedisKeyHelper.keyOfHttpDay(intervalTime);
                String apiListKey = RedisKeyHelper.keyOfHttpApiList(intervalTime);

                redisOperations.opsForSet().add(dayKey, intervalTime.getTime() + "");
                redisOperations.opsForSet().add(apiListKey, access.getApiUrl());
                redisOperations.expire(apiListKey, 2, TimeUnit.DAYS);


                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);

                // 判断是否有key
                if (!Boolean.TRUE.equals(hashOperations.hasKey(key))) {
                    hashOperations.put("dataTime", intervalTime.getTime() + "");
                    // 设定超时时间默认为1天
                    hashOperations.expire(2, TimeUnit.DAYS);
                }
                hashOperations.putIfAbsent("apiUrl", apiUrl);
                hashOperations.increment("totalCount", access.getTotalCount());
                hashOperations.increment("http2xxCount", access.getHttp2xxCount());
                hashOperations.increment("http4xxCount", access.getHttp4xxCount());
                hashOperations.increment("http5xxCount", access.getHttp5xxCount());
                // avgResponseTime 为累计时间
                hashOperations.increment("avgResponseTime", access.getAvgResponseTime());
                return null;
            }
        }));
        logger.info("update http access data for api: {}", JSON.toJSONString(apiStatAccessList));
    }

    /**
     * 计算httpCode 2xx的数量
     *
     * @param list
     * @return
     */
    private int count2xx(List<HttpMonitorMessage> list) {
        return Long.valueOf(list.stream().filter(msg -> msg.getHttpCode() >= 200 && msg.getHttpCode() <= 299).count()).intValue();
    }

    /**
     * 计算httpCode 4xx的数量
     *
     * @param list
     * @return
     */
    private int count4xx(List<HttpMonitorMessage> list) {
        return Long.valueOf(list.stream().filter(msg -> msg.getHttpCode() >= 400 && msg.getHttpCode() <= 499).count()).intValue();
    }

    /**
     * 计算httpCode 5xx的数量
     *
     * @param list
     * @return
     */
    private int count5xx(List<HttpMonitorMessage> list) {
        return Long.valueOf(list.stream().filter(msg -> msg.getHttpCode() >= 500 && msg.getHttpCode() <= 599).count()).intValue();
    }

    /**
     * 计算总计时间
     *
     * @param list
     * @return
     */
    private int sumTime(List<HttpMonitorMessage> list) {
        return Long.valueOf(list.stream().mapToLong(HttpMonitorMessage::getCostTime).sum()).intValue();
    }

}
