package com.treefinance.saas.monitor.biz.task;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.RedisKeyHelper;
import com.treefinance.saas.monitor.biz.service.EcommerceService;
import com.treefinance.saas.monitor.biz.service.MerchantStatAccessService;
import com.treefinance.saas.monitor.biz.service.OperatorService;
import com.treefinance.saas.monitor.biz.service.WebsiteService;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import com.treefinance.saas.monitor.common.domain.dto.*;
import com.treefinance.saas.monitor.common.enumeration.EStatType;
import com.treefinance.saas.monitor.dao.entity.Ecommerce;
import com.treefinance.saas.monitor.dao.entity.MerchantStatAccess;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据统计Job
 * Created by yh-treefinance on 2017/5/25.
 */
public class StatDataFlushJob implements SimpleJob {
    private static final Logger logger = LoggerFactory.getLogger(StatDataFlushJob.class);
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private EcommerceService ecommerceService;
    @Autowired
    private WebsiteService websiteService;
    @Autowired
    private OperatorService operatorService;
    @Autowired
    private MerchantStatAccessService merchantStatAccessService;

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            int intervalMinutes = diamondConfig.getMonitorIntervalMinutes();
            logger.info("intervalMinutes={}", intervalMinutes);
            Date now = new Date();
            // 电商列表
            List<EcommerceDTO> ecommerceDTOList = ecommerceService.getAll();

            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {
                    redisOperations.multi();
                    // 统计时间
                    String dayKey = RedisKeyHelper.keyOfDay(now);
                    Set<String> intervalSets = redisOperations.opsForSet().members(dayKey);
                    if (CollectionUtils.isEmpty(intervalSets)) {
                        return null;
                    }
                    Set<Date> intervalTimeSets = Sets.newHashSet();
                    intervalSets.forEach(t -> {
                        Long time = Long.valueOf(t);
                        intervalTimeSets.add(new Date(time));
                    });
                    // appId列表
                    String appIdKey = RedisKeyHelper.keyOfAppIds();
                    Set<String> appIdSet = redisOperations.opsForSet().members(appIdKey);
                    if (CollectionUtils.isEmpty(appIdSet)) {
                        return null;
                    }
                    // 1.保存合计数据
                    saveTotalData(intervalTimeSets, appIdSet, redisOperations);
                    // 2.保存电商数据
                    saveEcommerceData(intervalTimeSets, appIdSet, redisOperations);
                    // 3.保存邮箱数据
                    saveMailData(intervalTimeSets, appIdSet, redisOperations);
                    // 4.保存运营商数据
                    saveOperatorData(intervalTimeSets, appIdSet, redisOperations);
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("statdataflushjob exception : ", e);
        }
    }

    /**
     * 保存合计数据
     *
     * @param intervalTimes   统计时间
     * @param appIds          商户ID
     * @param redisOperations
     */
    private void saveTotalData(Set<Date> intervalTimes, Set<String> appIds, RedisOperations redisOperations) {
        List<MerchantStatAccessDTO> totalList = Lists.newArrayList();
        intervalTimes.forEach(intervalTime -> {
            // 统计总数
            appIds.forEach(appId -> {
                for (EStatType type : EStatType.values()) {
                    String totalkey = RedisKeyHelper.keyOfTotal(appId, intervalTime, type);
                    Map<String, Object> totalMap = redisOperations.opsForHash().entries(totalkey);
                    if (MapUtils.isEmpty(totalMap)) {
                        continue;
                    }
                    String json = JSON.toJSONString(totalMap);
                    MerchantStatAccessDTO dto = JSON.parseObject(json, MerchantStatAccessDTO.class);
                    totalList.add(dto);
                }
            });
        });
        if (CollectionUtils.isNotEmpty(totalList)) {
            logger.info("saveTotalData : data={}", JSON.toJSONString(totalList));
            merchantStatAccessService.batchInsertStatAccess(totalList);
        }
    }

    /**
     * 保存电商数据
     *
     * @param intervalTimes
     * @param appIds
     * @param redisOperations
     */
    private void saveEcommerceData(Set<Date> intervalTimes, Set<String> appIds, RedisOperations redisOperations) {
        List<EcommerceDTO> ecommerceList = ecommerceService.getAll();
        if (CollectionUtils.isEmpty(ecommerceList)) {
            return;
        }
        List<MerchantStatEcommerceDTO> dataList = Lists.newArrayList();

        intervalTimes.forEach(intervalTime -> {
            appIds.forEach(appId -> {
                for (EcommerceDTO ecommerceDTO : ecommerceList) {
                    Short ecommerceId = ecommerceDTO.getId();
                    String hashKey = RedisKeyHelper.keyOfEcommerce(appId, intervalTime, ecommerceId);
                    if (redisOperations.hasKey(hashKey)) {
                        Map<String, Object> dataMap = redisOperations.opsForHash().entries(hashKey);
                        if (MapUtils.isEmpty(dataMap)) {
                            continue;
                        }
                        String json = JSON.toJSONString(dataMap);
                        MerchantStatEcommerceDTO dto = JSON.parseObject(json, MerchantStatEcommerceDTO.class);
                        dataList.add(dto);
                    }
                }
            });
        });
        if (CollectionUtils.isNotEmpty(dataList)) {
            logger.info("saveEcommerceData : data={}", JSON.toJSONString(dataList));
            merchantStatAccessService.batchInsertEcommerce(dataList);
        }
    }

    /**
     * 邮箱数据保存
     * @param intervalTimes
     * @param appIds
     * @param redisOperations
     */
    private void saveMailData(Set<Date> intervalTimes, Set<String> appIds, RedisOperations redisOperations) {
        List<WebsiteDTO> mails = websiteService.getSupportMails();
        if (CollectionUtils.isEmpty(mails)) {
            return;
        }
        List<MerchantStatMailDTO> dataList = Lists.newArrayList();

        intervalTimes.forEach(intervalTime -> {
            appIds.forEach(appId -> {
                for (WebsiteDTO mail : mails) {
                    String mailCode = mail.getWebsiteName();
                    String hashKey = RedisKeyHelper.keyOfMail(appId, intervalTime, mailCode);
                    if (redisOperations.hasKey(hashKey)) {
                        Map<String, Object> dataMap = redisOperations.opsForHash().entries(hashKey);
                        if (MapUtils.isEmpty(dataMap)) {
                            continue;
                        }
                        String json = JSON.toJSONString(dataMap);
                        MerchantStatMailDTO dto = JSON.parseObject(json, MerchantStatMailDTO.class);
                        dataList.add(dto);
                    }
                }
            });
        });
        if (CollectionUtils.isNotEmpty(dataList)) {
            logger.info("saveEcommerceData : data={}", JSON.toJSONString(dataList));
            merchantStatAccessService.batchInsertMail(dataList);
        }
    }


    /**
     * 运营商数据保存
     * @param intervalTimes
     * @param appIds
     * @param redisOperations
     */
    private void saveOperatorData(Set<Date> intervalTimes, Set<String> appIds, RedisOperations redisOperations) {
        List<OperatorDTO> operators = operatorService.getAll();
        if (CollectionUtils.isEmpty(operators)) {
            return;
        }
        List<MerchantStatOperatorDTO> dataList = Lists.newArrayList();

        intervalTimes.forEach(intervalTime -> {
            appIds.forEach(appId -> {
                for (OperatorDTO operatorDTO : operators) {
                    String operatorId = operatorDTO.getId().toString();
                    String hashKey = RedisKeyHelper.keyOfOperator(appId, intervalTime, operatorId);
                    if (redisOperations.hasKey(hashKey)) {
                        Map<String, Object> dataMap = redisOperations.opsForHash().entries(hashKey);
                        if (MapUtils.isEmpty(dataMap)) {
                            continue;
                        }
                        String json = JSON.toJSONString(dataMap);
                        MerchantStatOperatorDTO dto = JSON.parseObject(json, MerchantStatOperatorDTO.class);
                        dataList.add(dto);
                    }
                }
            });
        });
        if (CollectionUtils.isNotEmpty(dataList)) {
            logger.info("saveEcommerceData : data={}", JSON.toJSONString(dataList));
            merchantStatAccessService.batchInsertOperator(dataList);
        }
    }
}
