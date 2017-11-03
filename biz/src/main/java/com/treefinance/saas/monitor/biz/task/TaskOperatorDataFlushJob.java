package com.treefinance.saas.monitor.biz.task;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper;
import com.treefinance.saas.monitor.biz.service.OperatorStatAccessUpdateService;
import com.treefinance.saas.monitor.common.cache.RedisDao;
import com.treefinance.saas.monitor.common.domain.dto.AllOperatorStatDayAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorStatAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorStatDayAccessDTO;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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


    @Override
    public void execute(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        Date jobTime = new Date();//定时任务执行时间
        logger.info("运营商监控,定时任务执行jobTime={}", MonitorDateUtils.format(jobTime));
        try {
            redisDao.getRedisTemplate().execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations redisOperations) throws DataAccessException {

                    Set<String> groupCodeSet = redisOperations.opsForSet().members(TaskOperatorMonitorKeyHelper.keyOfGroupCodes());
                    //保存所有运营商日统计数据
                    saveAllDayData(redisOperations, jobTime);
                    //保存运营商日统计数据
                    saveDayData(redisOperations, jobTime, groupCodeSet);
                    //保存运营商特定统计时间统计数据
                    saveIntervalData(redisOperations, jobTime, groupCodeSet);
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error("运营商监控,定时任务执行jobTime={}异常", MonitorDateUtils.format(jobTime), e);
        } finally {
            logger.info("运营商监控,定时任务执行jobTime={}完成,耗时{}", MonitorDateUtils.format(jobTime), System.currentTimeMillis() - start);
        }
    }

    private void saveAllDayData(RedisOperations redisOperations, Date jobTime) {
        try {
            Date redisStatDataTime = TaskOperatorMonitorKeyHelper.getRedisStatDateTime(jobTime);
            String hashKey = TaskOperatorMonitorKeyHelper.keyOfAllDayStat(redisStatDataTime);
            if (!redisOperations.hasKey(hashKey)) {
                return;
            }
            Map<String, Object> dataMap = redisOperations.opsForHash().entries(hashKey);
            if (MapUtils.isEmpty(dataMap)) {
                return;
            }
            String json = JSON.toJSONString(dataMap);
            AllOperatorStatDayAccessDTO dto = JSON.parseObject(json, AllOperatorStatDayAccessDTO.class);
            if (dto.getEntryCount() == null) {
                dto.setEntryCount(0);
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
            dto.setLoginConversionRate(calcRate(dto.getEntryCount(), dto.getLoginSuccessCount()));
            dto.setCrawlSuccessRate(calcRate(dto.getLoginSuccessCount(), dto.getCrawlSuccessCount()));
            dto.setProcessSuccessRate(calcRate(dto.getCrawlSuccessCount(), dto.getProcessSuccessCount()));
            dto.setCallbackSuccessRate(calcRate(dto.getProcessSuccessCount(), dto.getCallbackSuccessCount()));
            logger.info("运营商监控,定时任务执行jobTime={},刷新AllOperatorStatDayAccess数据到db中dto={}", MonitorDateUtils.format(jobTime), JSON.toJSONString(dto));
            operatorStatAccessUpdateService.batchInsertAllOperatorStatDayAccess(Lists.newArrayList(dto));
        } catch (Exception e) {
            logger.error("运营商监控,定时任务执行jobTime={},刷新AllOperatorStatDayAccess数据到db异常", MonitorDateUtils.format(jobTime), e);
            e.printStackTrace();
        }
    }

    private void saveDayData(RedisOperations redisOperations, Date jobTime, Set<String> groupCodeSet) {
        try {
            if (CollectionUtils.isEmpty(groupCodeSet)) {
                return;
            }
            List<OperatorStatDayAccessDTO> list = Lists.newArrayList();
            Date redisStatDataTime = TaskOperatorMonitorKeyHelper.getRedisStatDateTime(jobTime);//redis中时间为redisStatDataTime的key需要刷新到db中
            for (String groupCode : groupCodeSet) {
                String hashKey = TaskOperatorMonitorKeyHelper.keyOfGroupCodeDayStat(redisStatDataTime, groupCode);
                if (!redisOperations.hasKey(hashKey)) {
                    continue;
                }
                Map<String, Object> dataMap = redisOperations.opsForHash().entries(hashKey);
                if (MapUtils.isEmpty(dataMap)) {
                    continue;
                }
                String json = JSON.toJSONString(dataMap);
                OperatorStatDayAccessDTO dto = JSON.parseObject(json, OperatorStatDayAccessDTO.class);
                if (dto.getConfirmMobileCount() == null) {
                    dto.setConfirmMobileCount(0);
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
                dto.setLoginConversionRate(calcRate(dto.getConfirmMobileCount(), dto.getLoginSuccessCount()));
                dto.setCrawlSuccessRate(calcRate(dto.getLoginSuccessCount(), dto.getCrawlSuccessCount()));
                dto.setProcessSuccessRate(calcRate(dto.getCrawlSuccessCount(), dto.getProcessSuccessCount()));
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

    private void saveIntervalData(RedisOperations redisOperations, Date jobTime, Set<String> groupCodeSet) {
        try {
            if (CollectionUtils.isEmpty(groupCodeSet)) {
                return;
            }
            String dayKey = TaskOperatorMonitorKeyHelper.keyOfDay(jobTime);
            Set<String> redisStatDataTimeStrSets = redisOperations.opsForSet().members(dayKey);
            if (CollectionUtils.isEmpty(redisStatDataTimeStrSets)) {
                return;
            }
            Set<Date> redisStatDataTimeSets = Sets.newHashSet();
            for (String dateStr : redisStatDataTimeStrSets) {
                Date date = MonitorDateUtils.parse(dateStr);
                redisStatDataTimeSets.add(date);
            }
            if (CollectionUtils.isEmpty(redisStatDataTimeSets)) {
                return;
            }
            List<OperatorStatAccessDTO> list = Lists.newArrayList();
            for (Date redisStatDataTime : redisStatDataTimeSets) {
                for (String groupCode : groupCodeSet) {
                    String hashKey = TaskOperatorMonitorKeyHelper.keyOfGroupCodeIntervalStat(redisStatDataTime, groupCode);
                    if (!redisOperations.hasKey(hashKey)) {
                        continue;
                    }
                    Map<String, Object> dataMap = redisOperations.opsForHash().entries(hashKey);
                    if (MapUtils.isEmpty(dataMap)) {
                        continue;
                    }
                    String json = JSON.toJSONString(dataMap);
                    OperatorStatAccessDTO dto = JSON.parseObject(json, OperatorStatAccessDTO.class);
                    if (dto.getConfirmMobileCount() == null) {
                        dto.setConfirmMobileCount(0);
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
                    dto.setLoginConversionRate(calcRate(dto.getConfirmMobileCount(), dto.getLoginSuccessCount()));
                    dto.setCrawlSuccessRate(calcRate(dto.getLoginSuccessCount(), dto.getCrawlSuccessCount()));
                    dto.setProcessSuccessRate(calcRate(dto.getCrawlSuccessCount(), dto.getProcessSuccessCount()));
                    list.add(dto);
                }
            }
            if (CollectionUtils.isNotEmpty(list)) {
                logger.info("运营商监控,定时任务执行jobTime={},刷新OperatorStatAccess数据到db中list={}",
                        MonitorDateUtils.format(jobTime), JSON.toJSONString(list));
                operatorStatAccessUpdateService.batchInsertOperatorStatAccess(list);
            }
            if (CollectionUtils.isNotEmpty(redisStatDataTimeStrSets)) {
                logger.info("运营商监控,定时任务执行jobTime={},刷新OperatorStatAccess数据到db后,删除dayKey={}中已统计数据时间dataTimeSet={}",
                        MonitorDateUtils.format(jobTime), dayKey, JSON.toJSONString(redisStatDataTimeSets));
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
