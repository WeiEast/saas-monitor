package com.treefinance.saas.monitor.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.service.RealTimeAvgStatAccessService;
import com.treefinance.saas.monitor.common.constants.MonitorConstants;
import com.treefinance.saas.monitor.common.domain.dto.RealTimeStatAccessDTO;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.RealTimeStatAccess;
import com.treefinance.saas.monitor.dao.entity.RealTimeStatAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.RealTimeStatAccessMapper;
import com.treefinance.saas.monitor.facade.domain.request.BaseStatAccessRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Good Luck Bro , No Bug !
 *
 * @author haojiahong
 * @date 2018/6/27
 */
@Service
public class RealTimeAvgStatAccessServiceImpl implements RealTimeAvgStatAccessService {

    private static final Logger logger = LoggerFactory.getLogger(RealTimeAvgStatAccessService.class);

    private static final String REAL_TIME_STAT_REDIS_KEY_PREFIX = "real-time";

    @Autowired
    private RealTimeStatAccessMapper realTimeStatAccessMapper;
    @Autowired
    @Qualifier("jsonRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<RealTimeStatAccessDTO> queryRealTimeStatAccess(BaseStatAccessRequest request) {
        List<RealTimeStatAccessDTO> result = Lists.newArrayList();
        RealTimeStatAccessCriteria criteria = new RealTimeStatAccessCriteria();
        RealTimeStatAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        if (StringUtils.isNotBlank(request.getAppId())) {
            innerCriteria.andAppIdEqualTo(request.getAppId());
        } else {
            innerCriteria.andAppIdEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_APP_ID);
        }
        if (request.getSaasEnv() != null) {
            innerCriteria.andSaasEnvEqualTo(request.getSaasEnv());
        } else {
            innerCriteria.andSaasEnvEqualTo((byte) 0);
        }
        if (request.getIntervalMins() != null && request.getIntervalMins() > 0) {
            request.setStartTime(MonitorDateUtils.getIntervalDateTime(request.getStartTime(), request.getIntervalMins()));
        }
        innerCriteria.andBizTypeEqualTo(request.getBizType())
                .andDataTimeGreaterThanOrEqualTo(request.getStartTime())
                .andDataTimeLessThanOrEqualTo(request.getEndTime());
        List<RealTimeStatAccess> list = realTimeStatAccessMapper.selectByExample(criteria);

        List<RealTimeStatAccessDTO> dataList = this.convertStatData(list);
        Integer intervalMins = request.getIntervalMins() == null ? 1 : request.getIntervalMins();
        dataList = this.convertIntervalMinsData(dataList, intervalMins);
        Map<String, RealTimeStatAccessDTO> dataMap = dataList.stream()
                .collect(Collectors.toMap(d -> MonitorDateUtils.format(d.getDataTime()), d -> d));
        List<String> timeList = this.getIntervalTimeStrList(request.getStartTime(), request.getEndTime(),
                intervalMins, request.getHiddenRecentPoint());
        for (String timeStr : timeList) {
            if (dataMap.get(timeStr) == null) {
                //初始化一个空值,填充时间空白点
                RealTimeStatAccessDTO data = new RealTimeStatAccessDTO();
                data.setDataTime(MonitorDateUtils.parse(timeStr));
                data.setStatDataMap(new HashMap<>());
                result.add(data);
            } else {
                result.add(dataMap.get(timeStr));
            }
        }
        return result;
    }

    @Override
    public void saveDataOnFixedTime() {
        Date nowDate = MonitorDateUtils.getDayStartTime(new Date());
        Date sevenDate = DateUtils.addDays(nowDate, -7);

        RealTimeStatAccessCriteria criteria = new RealTimeStatAccessCriteria();
        RealTimeStatAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        innerCriteria.andDataTimeGreaterThanOrEqualTo(sevenDate)
                .andDataTimeLessThan(nowDate);
        List<RealTimeStatAccess> list = realTimeStatAccessMapper.selectByExample(criteria);

        //key:uniqueKey
        Map<String, List<RealTimeStatAccess>> map = list.stream()
                .collect(Collectors.groupingBy(r -> this.getUniqueKey(r.getAppId(), r.getGroupCode(), r.getDataType(), r.getBizType(), r.getSaasEnv())));

        //key:uniqueKey,value:当天各个时刻7天平均值
        Map<String, List<RealTimeStatAccessDTO>> avgMap = Maps.newHashMap();
        for (Map.Entry<String, List<RealTimeStatAccess>> entry : map.entrySet()) {
            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }
            List<RealTimeStatAccessDTO> dtoList = this.convertStatData(entry.getValue());
            List<RealTimeStatAccessDTO> avgList = this.computeAvgStatData(dtoList);
            avgMap.put(entry.getKey(), avgList);
        }
        String redisKey = this.getRedisKey(new Date());
        HashOperations<String, String, List<RealTimeStatAccessDTO>> hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(redisKey, avgMap);
        redisTemplate.expire(redisKey, 3, TimeUnit.DAYS);
    }


    @Override
    public List<RealTimeStatAccessDTO> queryDataByConditions(String appId, Byte saasEnv, Byte bizType,
                                                             Date startTime, Date endTime, Integer intervalMins,
                                                             Byte hiddenRecentPoint) {
        if (startTime == null || endTime == null || bizType == null) {
            logger.error("任务实时监控平均值查询,参数缺失,bizType,startTime,endTime必填,appId={},saaEnv={},startTime={},endTime={}",
                    appId, saasEnv, bizType, startTime, endTime);
            throw new IllegalArgumentException("任务实时监控平均值查询,参数缺失,bizType,startTime,endTime必填");
        }
        if (StringUtils.isBlank(appId)) {
            appId = MonitorConstants.VIRTUAL_TOTAL_STAT_APP_ID;
        }
        if (saasEnv == null) {
            saasEnv = 0;
        }
        if (intervalMins != null && intervalMins > 0) {
            startTime = MonitorDateUtils.getIntervalDateTime(startTime, intervalMins);
        }
        List<Date> dateList = this.getDayStartTimeList(startTime, endTime);
        List<RealTimeStatAccessDTO> dataList = Lists.newArrayList();
        for (Date date : dateList) {
            String redisKey = this.getRedisKey(date);
            String uniqueKey = this.getUniqueKey(appId, null, null, bizType, saasEnv);
            HashOperations<String, String, List<RealTimeStatAccessDTO>> hashOperations = redisTemplate.opsForHash();
            List<RealTimeStatAccessDTO> list = hashOperations.get(redisKey, uniqueKey);
            if (CollectionUtils.isNotEmpty(list)) {
                dataList.addAll(list);
            }
        }
        dataList = this.convertIntervalMinsData(dataList, intervalMins);
        Map<String, RealTimeStatAccessDTO> dataMap = dataList.stream()
                .collect(Collectors.toMap(d -> MonitorDateUtils.format(d.getDataTime()), d -> d));
        List<RealTimeStatAccessDTO> result = Lists.newArrayList();
        List<String> timeList = this.getIntervalTimeStrList(startTime, endTime, intervalMins, hiddenRecentPoint);
        for (String timeStr : timeList) {
            if (dataMap.get(timeStr) == null) {
                //初始化一个空值,填充时间空白点
                RealTimeStatAccessDTO data = new RealTimeStatAccessDTO();
                data.setDataTime(MonitorDateUtils.parse(timeStr));
                data.setStatDataMap(new HashMap<>());
                result.add(data);
            } else {
                result.add(dataMap.get(timeStr));
            }
        }
        return result;
    }


    /**
     * 获取redisKey,存储某天7天平均值的redisKey
     *
     * @param date
     * @return
     */
    private String getRedisKey(Date date) {
        String redisKey = Joiner.on(":").join(REAL_TIME_STAT_REDIS_KEY_PREFIX, MonitorDateUtils.format2Ymd(date));
        return redisKey;
    }

    /**
     * 获取统计分组key
     *
     * @param appId
     * @param groupCode
     * @param dataType
     * @param bizType
     * @param saasEnv
     * @return
     */
    private String getUniqueKey(String appId, String groupCode, Byte dataType, Byte bizType, Byte saasEnv) {
        //现在只统计所有分组
        if (StringUtils.isBlank(groupCode)) {
            groupCode = MonitorConstants.VIRTUAL_TOTAL_STAT_GROUPCODE;
        }
        //现在只统计任务
        if (dataType == null) {
            dataType = 0;
        }
        return Joiner.on(":").join("appId", appId, "groupCode", groupCode, "dataType", dataType, "bizType", bizType, "saasEnv", saasEnv);

    }

    /**
     * 获取今天各个时刻7天统计均值
     *
     * @param list
     * @return
     */
    private List<RealTimeStatAccessDTO> computeAvgStatData(List<RealTimeStatAccessDTO> list) {
        List<RealTimeStatAccessDTO> result = Lists.newArrayList();
        //key:时分秒时刻
        Map<String, List<RealTimeStatAccessDTO>> map = list.stream().collect(Collectors.groupingBy(r -> MonitorDateUtils.format2Hms(r.getDataTime())));
        for (Map.Entry<String, List<RealTimeStatAccessDTO>> entry : map.entrySet()) {

            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }
            RealTimeStatAccessDTO data = new RealTimeStatAccessDTO();
            BeanUtils.copyProperties(entry.getValue().get(0), data);
            String dataStr = new StringBuilder().append(MonitorDateUtils.format2Ymd(new Date())).append(" ").append(entry.getKey()).toString();
            data.setDataTime(MonitorDateUtils.parse(dataStr));

            Map<String, Integer> statDataMap = Maps.newHashMap();
            for (RealTimeStatAccessDTO item : entry.getValue()) {
                Map<String, Integer> itemMap = item.getStatDataMap();
                for (Map.Entry<String, Integer> itemEntry : itemMap.entrySet()) {
                    if (statDataMap.get(itemEntry.getKey()) == null) {
                        statDataMap.put(itemEntry.getKey(), itemEntry.getValue());
                    } else {
                        Integer value = statDataMap.get(itemEntry.getKey());
                        value = value + itemEntry.getValue();
                        statDataMap.put(itemEntry.getKey(), value);
                    }
                }
            }
            //这里暂时取整了,小数更好
            for (Map.Entry<String, Integer> item : statDataMap.entrySet()) {
                statDataMap.put(item.getKey(), item.getValue() / entry.getValue().size());
            }
            data.setStatDataMap(statDataMap);
            result.add(data);
        }
        return result;
    }

    private List<RealTimeStatAccessDTO> convertStatData(List<RealTimeStatAccess> list) {
        List<RealTimeStatAccessDTO> result = Lists.newArrayList();
        for (RealTimeStatAccess realTimeStatAccess : list) {
            RealTimeStatAccessDTO realTimeStatAccessDTO = DataConverterUtils.convert(realTimeStatAccess, RealTimeStatAccessDTO.class);
            if (StringUtils.isNotBlank(realTimeStatAccess.getStatData())) {
                Map<String, Integer> statDataMap = JSON.parseObject(realTimeStatAccess.getStatData(), new TypeReference<Map<String, Integer>>() {
                });
                realTimeStatAccessDTO.setStatDataMap(statDataMap);
            }
            result.add(realTimeStatAccessDTO);
        }
        return result;
    }

    private List<RealTimeStatAccessDTO> convertIntervalMinsData(List<RealTimeStatAccessDTO> list, Integer intervalMins) {
        //数据库中最小时间单位为1min钟,若比1min小则不转换,直接返回
        if (intervalMins == null || intervalMins < 1) {
            return list;
        }
        List<RealTimeStatAccessDTO> result = Lists.newArrayList();
        Map<Date, List<RealTimeStatAccessDTO>> map = list.stream()
                .collect(Collectors.groupingBy(data -> MonitorDateUtils.getIntervalDateTime(data.getDataTime(), intervalMins)));
        for (Map.Entry<Date, List<RealTimeStatAccessDTO>> entry : map.entrySet()) {
            RealTimeStatAccessDTO data = new RealTimeStatAccessDTO();
            BeanUtils.copyProperties(entry.getValue().get(0), data);
            data.setDataTime(entry.getKey());

            Map<String, Integer> statDataMap = Maps.newHashMap();
            for (RealTimeStatAccessDTO item : entry.getValue()) {
                Map<String, Integer> itemMap = item.getStatDataMap();
                for (Map.Entry<String, Integer> itemEntry : itemMap.entrySet()) {
                    if (statDataMap.get(itemEntry.getKey()) == null) {
                        statDataMap.put(itemEntry.getKey(), itemEntry.getValue());
                    } else {
                        Integer value = statDataMap.get(itemEntry.getKey());
                        value = value + itemEntry.getValue();
                        statDataMap.put(itemEntry.getKey(), value);
                    }
                }
            }
            data.setStatDataMap(statDataMap);
            result.add(data);
        }
        return result;
    }


    /**
     * 获取时间区间内的每日开始时间(左闭右闭)
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private List<Date> getDayStartTimeList(Date startTime, Date endTime) {
        List<Date> result = Lists.newArrayList();
        Date startDate = MonitorDateUtils.getDayStartTime(startTime);
        Date endDate = MonitorDateUtils.getDayStartTime(endTime);
        while (startDate.compareTo(endDate) <= 0) {
            result.add(startDate);
            startDate = DateUtils.addDays(startDate, 1);
        }
        return result;
    }


    private List<String> getIntervalTimeStrList(Date startTime, Date endTime, Integer intervalMins, Byte hiddenRecentPoint) {
        List<String> result = Lists.newArrayList();
        List<Date> list = Lists.newArrayList();
        Date endTimeInterval;
        if (hiddenRecentPoint != null && hiddenRecentPoint == 1) {
            endTimeInterval = MonitorDateUtils.getIntervalDateTime(DateUtils.addMinutes(endTime, -intervalMins), intervalMins);
        } else {
            endTimeInterval = MonitorDateUtils.getIntervalDateTime(endTime, intervalMins);
        }
        Date startTimeInterval = MonitorDateUtils.getIntervalDateTime(startTime, intervalMins);
        while (endTimeInterval.compareTo(startTimeInterval) >= 0) {
            list.add(endTimeInterval);
            endTimeInterval = DateUtils.addMinutes(endTimeInterval, -intervalMins);
        }
        list = list.stream().sorted(Date::compareTo).collect(Collectors.toList());
        result = list.stream().map(MonitorDateUtils::format).collect(Collectors.toList());
        return result;
    }

}
