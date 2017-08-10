package com.treefinance.saas.monitor.biz.mq.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.collect.Maps;
import com.treefinance.saas.gateway.servicefacade.enums.MonitorTypeEnum;
import com.treefinance.saas.gateway.servicefacade.model.mq.TaskMonitorMessage;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.RedisKeyHelper;
import com.treefinance.saas.monitor.biz.helper.StatHelper;
import com.treefinance.saas.monitor.biz.mq.handler.AbstractMessageHandler;
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
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 任务监控消息处理
 * Created by yh-treefinance on 2017/7/13.
 */
@Component
public class TaskMonitorMessageHandler extends AbstractMessageHandler<TaskMonitorMessage> {

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
    public boolean isHandleAble(MessageExt messageExt) {
        return messageExt.getTopic().equals(diamondConfig.getMonitorAccessTopic())
                && messageExt.getTags().equalsIgnoreCase(MonitorTypeEnum.TASK.name());
    }

    @Override
    public void handleMessage(byte[] messageBody) {
        TaskMonitorMessage message = convertMessage(messageBody);

        long start = System.currentTimeMillis();
        try {
            Date completeTime = message.getCompleteTime();
            Date intervalTime = StatHelper.calculateIntervalTime(completeTime, diamondConfig.getMonitorIntervalMinutes());
            // 1.总数统计
            statTotal(message, intervalTime);
            // 合计所有商户的总数统计
            statAllTotal(message, intervalTime);
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
//            // 插入历史记录
//            MerchantAccessHistoryDTO historyDTO = DataConverterUtils.convert(message, MerchantAccessHistoryDTO.class);
//            accessHistoryService.insertAccessHistory(historyDTO);
        } finally {
            logger.info("TaskMonitorAlarm:handleMessage cost {} ms , message={}", System.currentTimeMillis() - start, JSON.toJSONString(message));
        }
    }

    /**
     * 合计所有商户的总数统计
     *
     * @param message
     * @param intervalTime
     */
    private void statAllTotal(TaskMonitorMessage message, Date intervalTime) {
        EBizType bizType = EBizType.getBizType(message.getBizType());
        updateAllTotalData(intervalTime, message, EStatType.TOTAL);
        updateAllTotalDayData(intervalTime, message, EStatType.TOTAL);
        // 电商
        if (bizType == EBizType.ECOMMERCE) {
            updateAllTotalData(intervalTime, message, EStatType.ECOMMERCE);
            updateAllTotalDayData(intervalTime, message, EStatType.ECOMMERCE);
        }
        // 邮箱或者账单
        else if (bizType == EBizType.EMAIL) {
            updateAllTotalData(intervalTime, message, EStatType.EMAIL);
            updateAllTotalDayData(intervalTime, message, EStatType.EMAIL);
//             #TODO 银行暂不统计
//            updateAllTotalData();(intervalTime, message, EStatType.BANK);
        }
        // 运营商
        else if (bizType == EBizType.OPERATOR) {
            updateAllTotalData(intervalTime, message, EStatType.OPERATER);
            updateAllTotalDayData(intervalTime, message, EStatType.OPERATER);
        }
    }

    private void updateAllTotalDayData(Date intervalTime, TaskMonitorMessage message, EStatType type) {
        updateAllData(intervalTime, message,
                statMap -> statMap.put("dataType", type.getType() + ""),
                () -> RedisKeyHelper.keyOfAllTotalDay(intervalTime, type));
    }

    private void updateAllTotalData(Date intervalTime, TaskMonitorMessage message, EStatType type) {
        updateAllData(intervalTime, message, statMap -> statMap.put("dataType", type.getType() + ""),
                () -> RedisKeyHelper.keyOfAllTotal(intervalTime, type));
    }

    /**
     * 统计访问总数
     *
     * @param message
     * @param intervalTime
     */
    private void statTotal(TaskMonitorMessage message, Date intervalTime) {
        EBizType bizType = EBizType.getBizType(message.getBizType());
        // 总数
        updateTotalData(intervalTime, message, EStatType.TOTAL);
        updateTotalDayData(intervalTime, message, EStatType.TOTAL);
        // 电商
        if (bizType == EBizType.ECOMMERCE) {
            updateTotalData(intervalTime, message, EStatType.ECOMMERCE);
            updateTotalDayData(intervalTime, message, EStatType.ECOMMERCE);
        }
        // 邮箱或者账单
        else if (bizType == EBizType.EMAIL) {
            updateTotalData(intervalTime, message, EStatType.EMAIL);
            updateTotalDayData(intervalTime, message, EStatType.EMAIL);
            // #TODO 银行暂不统计
//            updateTotalData(intervalTime, message, EStatType.BANK);
        }
        // 运营商
        else if (bizType == EBizType.OPERATOR) {
            updateTotalData(intervalTime, message, EStatType.OPERATER);
            updateTotalDayData(intervalTime, message, EStatType.OPERATER);
        }
    }

    /**
     * 按日统计数据
     *
     * @param intervalTime
     * @param message
     * @param type
     */
    private void updateTotalDayData(Date intervalTime, TaskMonitorMessage message, EStatType type) {
        updateData(intervalTime, message,
                statMap -> statMap.put("dataType", type.getType() + ""),
                () -> RedisKeyHelper.keyOfTotalDay(message.getAppId(), intervalTime, type));
    }

    /**
     * 更新总计数据
     *
     * @param intervalTime
     * @param message
     * @param type
     */
    private void updateTotalData(Date intervalTime, TaskMonitorMessage message, EStatType type) {
        updateData(intervalTime, message, statMap -> statMap.put("dataType", type.getType() + ""),
                () -> RedisKeyHelper.keyOfTotal(message.getAppId(), intervalTime, type));
    }

    /**
     * 更新邮箱数据
     *
     * @param intervalTime
     * @param message
     */
    private void updateEmailData(Date intervalTime, TaskMonitorMessage message) {
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
    private void updateOperatorData(Date intervalTime, TaskMonitorMessage message) {
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
    private void updateEcommerceData(Date intervalTime, TaskMonitorMessage message) {
        String website = message.getWebSite();
        EcommerceDTO ecommerceDTO = ecommerceService.getEcommerceByWebsite(website);
        Short ecommerceId = ecommerceDTO != null ? ecommerceDTO.getId() : null;
        updateData(intervalTime, message, statMap -> statMap.put("ecommerceId", ecommerceId + ""), () -> RedisKeyHelper.keyOfEcommerce(message.getAppId(), intervalTime, ecommerceId));

    }


    /**
     * 更新数据
     *
     * @param intervalTime 循环时间
     * @param message
     * @param dataInitor
     * @param keyGenerator
     */
    private void updateAllData(Date intervalTime, TaskMonitorMessage message, Consumer<Map<String, String>> dataInitor, Supplier<String> keyGenerator) {
        Byte status = message.getStatus();
        String uniqueId = message.getUniqueId();
        Map<String, String> statMap = Maps.newHashMap();
        if (dataInitor != null) {
            dataInitor.accept(statMap);
        }
        String key = keyGenerator.get();
        String userKey = RedisKeyHelper.keyOfUniqueId(key, uniqueId);
        redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {

            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                // 当日时间列表
                String dayKey = RedisKeyHelper.keyOfDay(intervalTime);
                redisOperations.opsForSet().add(dayKey, intervalTime.getTime() + "");
                logger.info("TaskMonitorAlarm:stat-day中的值为:daykey={},value={}", dayKey, JSON.toJSONString(redisOperations.opsForSet().members(dayKey)));
                statMap.put("dataTime", intervalTime.getTime() + "");
                // 判断是否有key
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                if (!Boolean.TRUE.equals(hashOperations.hasKey(key))) {
                    hashOperations.put("dataTime", intervalTime.getTime() + "");
                    // 设定超时时间默认为1天
                    hashOperations.expire(2, TimeUnit.DAYS);
                }
                // 统计总数
                Long totalCount = hashOperations.increment("totalCount", 1);
                statMap.put("totalCount", totalCount + "");

                // 统计用户数: 未存在用户+1
                if (Boolean.TRUE.equals(redisOperations.opsForValue().setIfAbsent(userKey, uniqueId))) {
                    Long userCount = hashOperations.increment("userCount", 1);
                    statMap.put("userCount", userCount + "");
                    // 设定超时时间
                    redisOperations.expire(userKey, diamondConfig.getMonitorIntervalMinutes() * 2, TimeUnit.MINUTES);
                }

                // 统计取消数
                if (ETaskStatus.CANCEL.getStatus().equals(status)) {
                    Long cancelCount = hashOperations.increment("cancelCount", 1);
                    statMap.put("cancelCount", cancelCount + "");
                }
                // 统计成功数
                else if (ETaskStatus.SUCCESS.getStatus().equals(status)) {
                    Long successCount = hashOperations.increment("successCount", 1);
                    statMap.put("successCount", successCount + "");
                }
                // 统计失败数
                else if (ETaskStatus.FAIL.getStatus().equals(status)) {
                    Long failCount = hashOperations.increment("failCount", 1);
                    statMap.put("failCount", failCount + "");
                }
                return null;
            }
        });
        logger.info("TaskMonitorAlarm:update redis all access data: key={},value={}", key, JSON.toJSONString(statMap));
    }

    /**
     * 更新数据
     *
     * @param intervalTime 循环时间
     * @param message
     * @param dataInitor
     * @param keyGenerator
     */
    private void updateData(Date intervalTime, TaskMonitorMessage message, Consumer<Map<String, String>> dataInitor, Supplier<String> keyGenerator) {
        String appId = message.getAppId();
        Byte status = message.getStatus();
        String uniqueId = message.getUniqueId();
        Map<String, String> statMap = Maps.newHashMap();
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
//                redisOperations.multi();
                // appId列表
                String appIdKey = RedisKeyHelper.keyOfAppIds();
                redisOperations.opsForSet().add(appIdKey, appId);

                // 当日时间列表
                String dayKey = RedisKeyHelper.keyOfDay(intervalTime);
                redisOperations.opsForSet().add(dayKey, intervalTime.getTime() + "");

                statMap.put("dataTime", intervalTime.getTime() + "");
                statMap.put("appId", appId);
                // 判断是否有key
                BoundHashOperations<String, String, String> hashOperations = redisOperations.boundHashOps(key);
                if (!Boolean.TRUE.equals(hashOperations.hasKey(key))) {
                    hashOperations.put("dataTime", intervalTime.getTime() + "");
                    hashOperations.put("appId", appId);
                    // 设定超时时间默认为1天
                    hashOperations.expire(2, TimeUnit.DAYS);
                }
                // 统计总数
                Long totalCount = hashOperations.increment("totalCount", 1);
                statMap.put("totalCount", totalCount + "");

                // 统计用户数: 未存在用户+1
                if (Boolean.TRUE.equals(redisOperations.opsForValue().setIfAbsent(userKey, uniqueId))) {
                    Long userCount = hashOperations.increment("userCount", 1);
                    statMap.put("userCount", userCount + "");
                    // 设定超时时间
                    redisOperations.expire(userKey, diamondConfig.getMonitorIntervalMinutes() * 2, TimeUnit.MINUTES);
                }

                // 统计取消数
                if (ETaskStatus.CANCEL.getStatus().equals(status)) {
                    Long cancelCount = hashOperations.increment("cancelCount", 1);
                    statMap.put("cancelCount", cancelCount + "");
                }
                // 统计成功数
                else if (ETaskStatus.SUCCESS.getStatus().equals(status)) {
                    Long successCount = hashOperations.increment("successCount", 1);
                    statMap.put("successCount", successCount + "");
                }
                // 统计失败数
                else if (ETaskStatus.FAIL.getStatus().equals(status)) {
                    Long failCount = hashOperations.increment("failCount", 1);
                    statMap.put("failCount", failCount + "");
                }
                return null;
            }
        });
        logger.info("update redis access data: key={},value={}", key, JSON.toJSONString(statMap));
    }
}
