package com.treefinance.saas.monitor.biz.task;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper;
import com.treefinance.saas.monitor.biz.service.OperatorStatAccessUpdateService;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import com.treefinance.saas.monitor.common.domain.dto.OperatorAllStatAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorAllStatDayAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorStatAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorStatDayAccessDTO;
import com.treefinance.saas.monitor.common.enumeration.ETaskOperatorStatType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by haojiahong on 2017/10/31.
 */
public class TaskOperatorDataFlushJob implements SimpleJob {

    private static final Logger logger = LoggerFactory.getLogger(TaskOperatorDataFlushJob.class);
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private OperatorStatAccessUpdateService operatorStatAccessUpdateService;
    @Autowired
    private DiamondConfig diamondConfig;


    @Override
    public void execute(ShardingContext shardingContext) {
        String saasEnv = Constants.SAAS_ENV;
        logger.info("定时任务执行,当前环境SAAS-ENV={}", saasEnv);
        if (StringUtils.isNotBlank(saasEnv) && StringUtils.equalsIgnoreCase(saasEnv, "pre-product")) {
            logger.info("定时任务,预发布环境暂不执行");
            return;
        }
        long start = System.currentTimeMillis();
        Date jobTime = new Date();//定时任务执行时间
        logger.info("运营商监控,定时任务执行jobTime={}", MonitorDateUtils.format(jobTime));
        try {
            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {

                    Set<String> groupCodeSet = redisOperations.opsForSet().members(TaskOperatorMonitorKeyHelper.keyOfGroupCodes());
                    Set<String> appIdSet = redisOperations.opsForSet().members(TaskOperatorMonitorKeyHelper.keyOfAppIds());
                    if (CollectionUtils.isEmpty(appIdSet)) {
                        logger.info("运营商监控,定时任务执行jobTime={},要统计的商户appIdSet为空", MonitorDateUtils.format(jobTime));
                        return null;
                    }
                    for (String appId : appIdSet) {

                        //保存所有运营商特定统计时间统计数据
                        saveAllIntervalData(redisOperations, jobTime, appId, ETaskOperatorStatType.TASK);
                        //保存所有运营商日统计数据
                        saveAllDayData(redisOperations, jobTime, appId, ETaskOperatorStatType.TASK);
                        //保存运营商特定统计时间统计数据
                        saveIntervalData(redisOperations, jobTime, groupCodeSet, appId, ETaskOperatorStatType.TASK);
                        //保存运营商日统计数据
                        saveDayData(redisOperations, jobTime, groupCodeSet, appId, ETaskOperatorStatType.TASK);

                        //保存所有运营商特定统计时间统计数据
                        saveAllIntervalData(redisOperations, jobTime, appId, ETaskOperatorStatType.USER);
                        //保存所有运营商日统计数据
                        saveAllDayData(redisOperations, jobTime, appId, ETaskOperatorStatType.USER);
                        //保存运营商特定统计时间统计数据
                        saveIntervalData(redisOperations, jobTime, groupCodeSet, appId, ETaskOperatorStatType.USER);
                        //保存运营商日统计数据
                        saveDayData(redisOperations, jobTime, groupCodeSet, appId, ETaskOperatorStatType.USER);
                    }

                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("运营商监控,定时任务执行jobTime={}异常", MonitorDateUtils.format(jobTime), e);
        } finally {
            logger.info("运营商监控,定时任务执行jobTime={}完成,耗时{}ms", MonitorDateUtils.format(jobTime), System.currentTimeMillis() - start);
        }
    }

    private void saveAllIntervalData(RedisOperations redisOperations, Date jobTime, String appId, ETaskOperatorStatType statType) {
        try {
            String dayKey = TaskOperatorMonitorKeyHelper.keyOfDayOnAllStat(jobTime, appId, statType);
            Set<String> redisStatDataTimeStrSets = redisOperations.opsForSet().members(dayKey);
            if (CollectionUtils.isEmpty(redisStatDataTimeStrSets)) {
                return;
            }
            logger.info("运营商监控,定时任务执行jobTime={},保存所有运营商特定统计时间统计数据,此次任务需要统计的时间段有dataTimeStrSets={}",
                    MonitorDateUtils.format(jobTime), JSON.toJSONString(redisStatDataTimeStrSets));
            Set<Date> redisStatDataTimeSets = Sets.newHashSet();
            for (String dateStr : redisStatDataTimeStrSets) {
                List<String> strList = Splitter.on(";").splitToList(dateStr);
                Date date = MonitorDateUtils.parse(strList.get(0));
                redisStatDataTimeSets.add(date);
            }
            if (CollectionUtils.isEmpty(redisStatDataTimeSets)) {
                return;
            }
            List<OperatorAllStatAccessDTO> list = Lists.newArrayList();
            for (Date redisStatDataTime : redisStatDataTimeSets) {
                String hashKey = TaskOperatorMonitorKeyHelper.keyOfAllIntervalStat(redisStatDataTime, appId, statType);
                if (!redisOperations.hasKey(hashKey)) {
                    return;
                }
                Map<String, Object> dataMap = redisOperations.opsForHash().entries(hashKey);
                if (MapUtils.isEmpty(dataMap)) {
                    return;
                }
                logger.info("运营商监控,定时任务执行jobTime={},刷新所有运营商按时间段统计数据到db中,key={},data={}",
                        MonitorDateUtils.format(jobTime), hashKey, JSON.toJSONString(dataMap));
                String json = JSON.toJSONString(dataMap);
                OperatorAllStatAccessDTO dto = JSON.parseObject(json, OperatorAllStatAccessDTO.class);
                dto.setId(UidGenerator.getId());
                if (dto.getEntryCount() == null) {
                    dto.setEntryCount(0);
                }
                if (dto.getConfirmMobileCount() == null) {
                    dto.setConfirmMobileCount(0);
                }
                if (dto.getStartLoginCount() == null) {
                    dto.setStartLoginCount(0);
                }
                if (dto.getLoginSuccessCount() == null) {
                    dto.setLoginSuccessCount(0);
                }
                if (dto.getCrawlSuccessCount() == null) {
                    dto.setCrawlSuccessCount(0);
                }
                if (dto.getProcessSuccessCount() == null) {
                    dto.setProcessSuccessCount(0);
                }
                if (dto.getCallbackSuccessCount() == null) {
                    dto.setCallbackSuccessCount(0);
                }
                dto.setConfirmMobileConversionRate(calcRate(dto.getEntryCount(), dto.getConfirmMobileCount()));
                dto.setLoginConversionRate(calcRate(dto.getConfirmMobileCount(), dto.getStartLoginCount()));
                dto.setLoginSuccessRate(calcRate(dto.getStartLoginCount(), dto.getLoginSuccessCount()));
                dto.setCrawlSuccessRate(calcRate(dto.getLoginSuccessCount(), dto.getCrawlSuccessCount()));
                dto.setProcessSuccessRate(calcRate(dto.getCrawlSuccessCount(), dto.getProcessSuccessCount()));
                dto.setCallbackSuccessRate(calcRate(dto.getProcessSuccessCount(), dto.getCallbackSuccessCount()));

                String taskUserCountKey = TaskOperatorMonitorKeyHelper.keyOfTaskUserCountAllIntervalStat(redisStatDataTime, appId, statType);
                if (redisOperations.hasKey(taskUserCountKey)) {
                    Map<String, Object> taskUserCountMap = redisOperations.opsForHash().entries(taskUserCountKey);
                    if (taskUserCountMap.get("taskCount") != null) {
                        dto.setTaskCount(Integer.valueOf(taskUserCountMap.get("taskCount").toString()));
                    }
                    if (taskUserCountMap.get("userCount") != null) {
                        dto.setUserCount(Integer.valueOf(taskUserCountMap.get("userCount").toString()));
                    }
                }
                list.add(dto);
            }

            if (CollectionUtils.isNotEmpty(list)) {
                logger.info("运营商监控,定时任务执行jobTime={},刷新OperatorAllStatAccess数据到db中list={}",
                        MonitorDateUtils.format(jobTime), JSON.toJSONString(list));
                operatorStatAccessUpdateService.batchInsertAllOperatorStatAccess(list);
            }
            if (CollectionUtils.isNotEmpty(redisStatDataTimeStrSets)) {
                logger.info("运营商监控,定时任务执行jobTime={},刷新OperatorStatAccess数据到db后,删除dayKey={}中已统计数据时间dataTimeSet={},dataTimeStrSets={}",
                        MonitorDateUtils.format(jobTime), dayKey, JSON.toJSONString(redisStatDataTimeSets), JSON.toJSONString(redisStatDataTimeStrSets));
                String[] array = new String[redisStatDataTimeStrSets.size()];
                redisOperations.opsForSet().remove(dayKey, redisStatDataTimeStrSets.toArray(array));
            }
        } catch (Exception e) {
            logger.error("运营商监控,定时任务执行jobTime={},刷新OperatorAllStatAccess数据到db异常", MonitorDateUtils.format(jobTime), e);
            e.printStackTrace();
        }
    }

    private void saveAllDayData(RedisOperations redisOperations, Date jobTime, String appId, ETaskOperatorStatType statType) {
        try {
            Date redisStatDataTime = TaskOperatorMonitorKeyHelper.getRedisStatDateTime(jobTime, diamondConfig.getOperatorMonitorIntervalMinutes());
            String hashKey = TaskOperatorMonitorKeyHelper.keyOfAllDayStat(redisStatDataTime, appId, statType);
            if (!redisOperations.hasKey(hashKey)) {
                return;
            }
            Map<String, Object> dataMap = redisOperations.opsForHash().entries(hashKey);
            if (MapUtils.isEmpty(dataMap)) {
                return;
            }
            logger.info("运营商监控,定时任务执行jobTime={},刷新所有运营商日统计数据到db中,key={},data={}",
                    MonitorDateUtils.format(jobTime), hashKey, JSON.toJSONString(dataMap));
            String json = JSON.toJSONString(dataMap);
            OperatorAllStatDayAccessDTO dto = JSON.parseObject(json, OperatorAllStatDayAccessDTO.class);
            dto.setId(UidGenerator.getId());
            if (dto.getEntryCount() == null) {
                dto.setEntryCount(0);
            }
            if (dto.getConfirmMobileCount() == null) {
                dto.setConfirmMobileCount(0);
            }
            if (dto.getStartLoginCount() == null) {
                dto.setStartLoginCount(0);
            }
            if (dto.getLoginSuccessCount() == null) {
                dto.setLoginSuccessCount(0);
            }
            if (dto.getCrawlSuccessCount() == null) {
                dto.setCrawlSuccessCount(0);
            }
            if (dto.getProcessSuccessCount() == null) {
                dto.setProcessSuccessCount(0);
            }
            if (dto.getCallbackSuccessCount() == null) {
                dto.setCallbackSuccessCount(0);
            }
            dto.setConfirmMobileConversionRate(calcRate(dto.getEntryCount(), dto.getConfirmMobileCount()));
            dto.setLoginConversionRate(calcRate(dto.getConfirmMobileCount(), dto.getStartLoginCount()));
            dto.setLoginSuccessRate(calcRate(dto.getStartLoginCount(), dto.getLoginSuccessCount()));
            dto.setCrawlSuccessRate(calcRate(dto.getLoginSuccessCount(), dto.getCrawlSuccessCount()));
            dto.setProcessSuccessRate(calcRate(dto.getCrawlSuccessCount(), dto.getProcessSuccessCount()));
            dto.setCallbackSuccessRate(calcRate(dto.getProcessSuccessCount(), dto.getCallbackSuccessCount()));

            String taskUserCountKey = TaskOperatorMonitorKeyHelper.keyOfTaskUserCountAllDayStat(redisStatDataTime, appId, statType);
            if (redisOperations.hasKey(taskUserCountKey)) {
                Map<String, Object> taskUserCountMap = redisOperations.opsForHash().entries(taskUserCountKey);
                if (taskUserCountMap.get("taskCount") != null) {
                    dto.setTaskCount(Integer.valueOf(taskUserCountMap.get("taskCount").toString()));
                }
                if (taskUserCountMap.get("userCount") != null) {
                    dto.setUserCount(Integer.valueOf(taskUserCountMap.get("userCount").toString()));
                }
            }

            logger.info("运营商监控,定时任务执行jobTime={},刷新AllOperatorStatDayAccess数据到db中dto={}", MonitorDateUtils.format(jobTime), JSON.toJSONString(dto));
            operatorStatAccessUpdateService.batchInsertAllOperatorStatDayAccess(Lists.newArrayList(dto));
        } catch (Exception e) {
            logger.error("运营商监控,定时任务执行jobTime={},刷新AllOperatorStatDayAccess数据到db异常", MonitorDateUtils.format(jobTime), e);
            e.printStackTrace();
        }
    }

    private void saveDayData(RedisOperations redisOperations, Date jobTime, Set<String> groupCodeSet, String appId, ETaskOperatorStatType statType) {
        try {
            if (CollectionUtils.isEmpty(groupCodeSet)) {
                return;
            }
            List<OperatorStatDayAccessDTO> list = Lists.newArrayList();
            Date redisStatDataTime = TaskOperatorMonitorKeyHelper.getRedisStatDateTime(jobTime, diamondConfig.getOperatorMonitorIntervalMinutes());//redis中时间为redisStatDataTime的key需要刷新到db中
            for (String groupCode : groupCodeSet) {
                String hashKey = TaskOperatorMonitorKeyHelper.keyOfGroupCodeDayStat(redisStatDataTime, groupCode, appId, statType);
                if (!redisOperations.hasKey(hashKey)) {
                    continue;
                }
                Map<String, Object> dataMap = redisOperations.opsForHash().entries(hashKey);
                if (MapUtils.isEmpty(dataMap)) {
                    continue;
                }
                logger.info("运营商监控,定时任务执行jobTime={},刷新特定运营商日统计数据到db中,key={},data={},groupCode={}",
                        MonitorDateUtils.format(jobTime), hashKey, JSON.toJSONString(dataMap), groupCode);
                String json = JSON.toJSONString(dataMap);
                OperatorStatDayAccessDTO dto = JSON.parseObject(json, OperatorStatDayAccessDTO.class);
                dto.setId(UidGenerator.getId());
                if (dto.getConfirmMobileCount() == null) {
                    dto.setConfirmMobileCount(0);
                }
                if (dto.getStartLoginCount() == null) {
                    dto.setStartLoginCount(0);
                }
                if (dto.getLoginSuccessCount() == null) {
                    dto.setLoginSuccessCount(0);
                }
                if (dto.getCrawlSuccessCount() == null) {
                    dto.setCrawlSuccessCount(0);
                }
                if (dto.getProcessSuccessCount() == null) {
                    dto.setProcessSuccessCount(0);
                }
                if (dto.getCallbackSuccessCount() == null) {
                    dto.setCallbackSuccessCount(0);
                }
                dto.setLoginConversionRate(calcRate(dto.getConfirmMobileCount(), dto.getStartLoginCount()));
                dto.setLoginSuccessRate(calcRate(dto.getStartLoginCount(), dto.getLoginSuccessCount()));
                dto.setCrawlSuccessRate(calcRate(dto.getLoginSuccessCount(), dto.getCrawlSuccessCount()));
                dto.setProcessSuccessRate(calcRate(dto.getCrawlSuccessCount(), dto.getProcessSuccessCount()));
                dto.setCallbackSuccessRate(calcRate(dto.getProcessSuccessCount(), dto.getCallbackSuccessCount()));
                list.add(dto);
            }

            if (CollectionUtils.isNotEmpty(list)) {
                logger.info("运营商监控,定时任务执行jobTime={},刷新OperatorStatDayAccess数据到db中list={}",
                        MonitorDateUtils.format(jobTime), JSON.toJSONString(list));
                operatorStatAccessUpdateService.batchInsertOperatorStatDayAccess(list);
            }
        } catch (Exception e) {
            logger.error("运营商监控,定时任务执行jobTime={},刷新OperatorStatDayAccess数据到db中异常", MonitorDateUtils.format(jobTime), e);
            e.printStackTrace();
        }


    }

    private void saveIntervalData(RedisOperations redisOperations, Date jobTime, Set<String> groupCodeSet, String appId, ETaskOperatorStatType statType) {
        try {
            if (CollectionUtils.isEmpty(groupCodeSet)) {
                return;
            }
            String dayKey = TaskOperatorMonitorKeyHelper.keyOfDayOnGroupStat(jobTime, appId, statType);
            Set<String> redisStatDataTimeStrSets = redisOperations.opsForSet().members(dayKey);
            if (CollectionUtils.isEmpty(redisStatDataTimeStrSets)) {
                return;
            }
            logger.info("运营商监控,定时任务执行jobTime={},保存运营商特定统计时间统计数据,此次任务需要统计的时间段有dataTimeStrSets={}",
                    MonitorDateUtils.format(jobTime), JSON.toJSONString(redisStatDataTimeStrSets));
            Set<Date> redisStatDataTimeSets = Sets.newHashSet();
            for (String dateStr : redisStatDataTimeStrSets) {
                List<String> strList = Splitter.on(";").splitToList(dateStr);
                Date date = MonitorDateUtils.parse(strList.get(0));
                redisStatDataTimeSets.add(date);
            }
            if (CollectionUtils.isEmpty(redisStatDataTimeSets)) {
                return;
            }
            List<OperatorStatAccessDTO> list = Lists.newArrayList();
            for (Date redisStatDataTime : redisStatDataTimeSets) {
                for (String groupCode : groupCodeSet) {
                    String hashKey = TaskOperatorMonitorKeyHelper.keyOfGroupCodeIntervalStat(redisStatDataTime, groupCode, appId, statType);
                    if (!redisOperations.hasKey(hashKey)) {
                        continue;
                    }
                    Map<String, Object> dataMap = redisOperations.opsForHash().entries(hashKey);
                    if (MapUtils.isEmpty(dataMap)) {
                        continue;
                    }
                    logger.info("运营商监控,定时任务执行jobTime={},刷新特定运营商按时间段统计数据到db中,key={},data={},groupCode={},redisStatTime={}",
                            MonitorDateUtils.format(jobTime), hashKey, JSON.toJSONString(dataMap), groupCode, MonitorDateUtils.format(redisStatDataTime));
                    String json = JSON.toJSONString(dataMap);
                    OperatorStatAccessDTO dto = JSON.parseObject(json, OperatorStatAccessDTO.class);
                    dto.setId(UidGenerator.getId());
                    if (dto.getConfirmMobileCount() == null) {
                        dto.setConfirmMobileCount(0);
                    }
                    if (dto.getStartLoginCount() == null) {
                        dto.setStartLoginCount(0);
                    }
                    if (dto.getLoginSuccessCount() == null) {
                        dto.setLoginSuccessCount(0);
                    }
                    if (dto.getCrawlSuccessCount() == null) {
                        dto.setCrawlSuccessCount(0);
                    }
                    if (dto.getProcessSuccessCount() == null) {
                        dto.setProcessSuccessCount(0);
                    }
                    if (dto.getCallbackSuccessCount() == null) {
                        dto.setCallbackSuccessCount(0);
                    }
                    dto.setLoginConversionRate(calcRate(dto.getConfirmMobileCount(), dto.getStartLoginCount()));
                    dto.setLoginSuccessRate(calcRate(dto.getStartLoginCount(), dto.getLoginSuccessCount()));
                    dto.setCrawlSuccessRate(calcRate(dto.getLoginSuccessCount(), dto.getCrawlSuccessCount()));
                    dto.setProcessSuccessRate(calcRate(dto.getCrawlSuccessCount(), dto.getProcessSuccessCount()));
                    dto.setCallbackSuccessRate(calcRate(dto.getProcessSuccessCount(), dto.getCallbackSuccessCount()));
                    list.add(dto);
                }
            }
            if (CollectionUtils.isNotEmpty(list)) {
                logger.info("运营商监控,定时任务执行jobTime={},刷新OperatorStatAccess数据到db中list={}",
                        MonitorDateUtils.format(jobTime), JSON.toJSONString(list));
                operatorStatAccessUpdateService.batchInsertOperatorStatAccess(list);
            }
            if (CollectionUtils.isNotEmpty(redisStatDataTimeStrSets)) {
                logger.info("运营商监控,定时任务执行jobTime={},刷新OperatorStatAccess数据到db后,删除dayKey={}中已统计数据时间dataTimeSet={},dataTimeStrSets={}",
                        MonitorDateUtils.format(jobTime), dayKey, JSON.toJSONString(redisStatDataTimeSets), JSON.toJSONString(redisStatDataTimeStrSets));
                String[] array = new String[redisStatDataTimeStrSets.size()];
                redisOperations.opsForSet().remove(dayKey, redisStatDataTimeStrSets.toArray(array));
            }
        } catch (Exception e) {
            logger.error("运营商监控,定时任务执行jobTime={},刷新OperatorStatAccess数据到db中异常", MonitorDateUtils.format(jobTime), e);
            e.printStackTrace();
        }

    }


    /**
     * 计算比率
     *
     * @param a
     * @param b
     * @return
     */
    private BigDecimal calcRate(Integer a, Integer b) {
        if (Integer.valueOf(0).compareTo(a) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = BigDecimal.valueOf(b, 2)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(a, 2), 2, BigDecimal.ROUND_HALF_UP);
        return rate;
    }


}
