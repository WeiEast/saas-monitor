package com.treefinance.saas.monitor.biz.mq.listener;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.RedisKeyHelper;
import com.treefinance.saas.monitor.biz.helper.StatHelper;
import com.treefinance.saas.monitor.biz.mq.model.GatewayAccessMessage;
import com.treefinance.saas.monitor.biz.service.EcommerceService;
import com.treefinance.saas.monitor.biz.service.OperatorService;
import com.treefinance.saas.monitor.biz.service.WebsiteService;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import com.treefinance.saas.monitor.common.domain.dto.EcommerceDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorDTO;
import com.treefinance.saas.monitor.common.domain.dto.WebsiteDTO;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.common.enumeration.EStatType;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by yh-treefinance on 2017/6/6.
 */
@Service
public class StatMessageListener extends AbstractMessageListener<GatewayAccessMessage> {

    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private WebsiteService websiteService;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private EcommerceService ecommerceService;

    @Override
    public void handleMessage(GatewayAccessMessage message) {
        Date completeTime = message.getCompleteTime();
        Date intervalTime = StatHelper.calculateIntervalTime(completeTime, diamondConfig.getMonitorIntervalMinutes());
        // 1.总数统计
        statTotal(message, intervalTime);
        // 2.电商、银行、邮箱、运营商维度统计
        EBizType bizType = EBizType.getBizType(message.getBizType());
        switch (bizType) {
            case EMAIL:
                updateEmailData(intervalTime, message);
                break;
            case OPERATOR:
                updateOperatorData(intervalTime, message);
                break;
            case ECOMMERCE:
                updateEcommerceData(intervalTime, message);
                break;
        }

    }

    /**
     * 统计访问总数
     *
     * @param message
     * @param intervalTime
     */
    private void statTotal(GatewayAccessMessage message, Date intervalTime) {
        EBizType bizType = EBizType.getBizType(message.getBizType());
        // 总数
        updateTotalData(intervalTime, message, EStatType.TOTAL);
        // 电商
        if (bizType == EBizType.ECOMMERCE) {
            updateTotalData(intervalTime, message, EStatType.ECOMMERCE);
        }
        // 邮箱或者账单
        else if (bizType == EBizType.EMAIL) {
            updateTotalData(intervalTime, message, EStatType.EMAIL);
            // #TODO 银行暂不统计
//            updateTotalData(intervalTime, message, EStatType.BANK);
        }
        // 运营商
        else if (bizType == EBizType.OPERATOR) {
            updateTotalData(intervalTime, message, EStatType.OPERATER);
        }
    }

    /**
     * 更新总计数据
     *
     * @param intervalTime
     * @param message
     * @param type
     */
    private void updateTotalData(Date intervalTime, GatewayAccessMessage message, EStatType type) {
        updateData(intervalTime, message, statMap -> statMap.put("dataType", type.getType()), () -> RedisKeyHelper.keyOfTotal(message.getAppId(), intervalTime, type));
    }

    /**
     * 更新邮箱数据
     *
     * @param intervalTime
     * @param message
     */
    private void updateEmailData(Date intervalTime, GatewayAccessMessage message) {
        String website = message.getWebSite();
        WebsiteDTO websiteDTO = websiteService.getWebsiteByName(website);
        String mailCode = websiteDTO != null ? websiteDTO.getWebsiteName() : null;
        updateData(intervalTime, message, statMap -> statMap.put("mailCode", mailCode), () -> RedisKeyHelper.keyOfMail(message.getAppId(), intervalTime, mailCode));
    }

    /**
     * 更新运营商数据
     *
     * @param intervalTime
     * @param message
     */
    private void updateOperatorData(Date intervalTime, GatewayAccessMessage message) {
        String website = message.getWebSite();
        OperatorDTO operatorDTO = operatorService.getOperatorByWebsite(website);
        String operatorId = operatorDTO != null ? operatorDTO.getId().toString() : null;
        updateData(intervalTime, message, statMap -> statMap.put("operatorId", operatorId), () -> RedisKeyHelper.keyOfOperator(message.getAppId(), intervalTime, operatorId));
    }

    /**
     * 更新电商数据
     *
     * @param intervalTime
     * @param message
     */
    private void updateEcommerceData(Date intervalTime, GatewayAccessMessage message) {
        String website = message.getWebSite();
        EcommerceDTO ecommerceDTO = ecommerceService.getEcommerceByWebsite(website);
        Short ecommerceId = ecommerceDTO != null ? ecommerceDTO.getId() : null;
        updateData(intervalTime, message, statMap -> statMap.put("ecommerceId", ecommerceId), () -> RedisKeyHelper.keyOfEcommerce(message.getAppId(), intervalTime, ecommerceId));

    }

    /**
     * 更新数据
     *
     * @param intervalTime 循环时间
     * @param message
     * @param dataInitor
     * @param keyGenerator
     */
    private void updateData(Date intervalTime, GatewayAccessMessage message, Consumer<Map<String, Object>> dataInitor, Supplier<String> keyGenerator) {
        String appId = message.getAppId();
        Byte status = message.getStatus();
        String uniqueId = message.getUniqueId();
        Map<String, Object> statMap = Maps.newHashMap();
        if (dataInitor != null) {
            dataInitor.accept(statMap);
        }
        String key = keyGenerator.get();
        String userKey = RedisKeyHelper.keyOfUniqueId(key, uniqueId);
//        redisDao.getRedisTemplate().setEnableTransactionSupport(true);
        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {

            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                // 开启事务
                redisOperations.multi();
                // appId列表
                String appIdKey = RedisKeyHelper.keyOfAppIds();
                redisOperations.opsForSet().add(appIdKey, appId);

                // 当日时间列表
                String dayKey = RedisKeyHelper.keyOfDay(intervalTime);
                redisOperations.opsForSet().add(dayKey, intervalTime.getTime() + "");

                statMap.put("dataTime", intervalTime.getTime());
                statMap.put("appId", appId);
                // 判断是否有key
                if (!redisOperations.hasKey(key)) {
                    redisOperations.opsForHash().putAll(key, statMap);
                    // 设定超时时间默认为1天
                    redisOperations.expire(key, 2, TimeUnit.DAYS);
                }
                // 统计总数
                Long totalCount = redisOperations.opsForHash().increment(key, "totalCount", 1);
                statMap.put("totalCount", totalCount);

                // 统计用户数: 未存在用户+1

                if (redisOperations.opsForValue().setIfAbsent(userKey, uniqueId)) {
                    Long userCount = redisOperations.opsForHash().increment(key, "userCount", 1);
                    statMap.put("userCount", userCount);
                    // 设定超时时间
                    redisOperations.expire(userKey, diamondConfig.getMonitorIntervalMinutes() * 2, TimeUnit.MINUTES);
                }

                // 统计取消数
                if (ETaskStatus.CANCEL.getStatus().equals(status)) {
                    Long cancelCount = redisOperations.opsForHash().increment(key, "cancelCount", 1);
                    statMap.put("cancelCount", cancelCount);
                }
                // 统计成功数
                else if (ETaskStatus.SUCCESS.getStatus().equals(status)) {
                    Long successCount = redisOperations.opsForHash().increment(key, "successCount", 1);
                    statMap.put("successCount", successCount);
                }
                // 统计失败数
                else if (ETaskStatus.FAIL.getStatus().equals(status)) {
                    Long failCount = redisOperations.opsForHash().increment(key, "failCount", 1);
                    statMap.put("failCount", failCount);
                }
                return null;
            }
        });
        logger.info("update redis access data: key={},value={}", key, JSON.toJSONString(statMap));
    }

}
