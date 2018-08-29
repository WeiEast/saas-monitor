package com.treefinance.saas.monitor.biz.alarm.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.dao.entity.AsAlarmTriggerRecord;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yh-treefinance on 2018/7/23.
 */
public class AlarmContext {

    /**
     * 非分组数据
     */
    private ConcurrentHashMap<String, Object> normalContext = new ConcurrentHashMap<>();

    /**
     * 分组
     */
    private ConcurrentHashMap<String, Object> groupContext = new ConcurrentHashMap<>();

    /**
     * group data
     */
    private List<Map<String, Object>> dataList = Lists.newArrayList();

    /**
     * 预警消息
     */
    private List<AlarmMessage> alaramMessageList = Lists.newArrayList();
    /**
     * 预警记录
     */
    private List<AsAlarmTriggerRecord> triggerRecords = Lists.newArrayList();

    /**
     * 本次警时间
     */
    private Date alarmTime;
    /**
     * 预警间隔时间（毫秒）
     */
    private Long intervalTime;

    /**
     * 开始时间搓
     */
    private Long startTimeStamp = System.currentTimeMillis();

    /**
     * 增加分组项
     *
     * @param key
     * @param value
     */
    public void addGroup(String key, Object value) {
        if (value.getClass().isArray()) {
            groupContext.put(key, value);
        } else if (value instanceof Collection) {
            groupContext.put(key, value);
        } else {
            normalContext.put(key, value);
        }
        descartes();
    }

    /**
     * 添加预警发送消息
     *
     * @param alarmMessage
     */
    public void addMessage(AlarmMessage alarmMessage) {
        alaramMessageList.add(alarmMessage);
    }

    /**
     * 添加预警记录
     *
     * @param records
     */
    public void addRecords(List<AsAlarmTriggerRecord> records) {
        triggerRecords.addAll(records);
    }

    /**
     * 原始数据存储
     *
     * @param dataMap
     * @param key
     * @param value
     * @return
     */
    public Map<String, Object> origin(Map<String, Object> dataMap, String key, Object value) {
        Map<String, Object> original = (Map<String, Object>) dataMap.get("origin");
        if (original == null) {
            original = Maps.newHashMap();
            dataMap.put("origin", original);
        }
        original.put(key, value);
        return original;
    }

    /**
     * 分组项
     *
     * @return
     */
    public List<Map<String, Object>> groups() {
        return dataList;
    }


    /**
     * 笛卡尔积
     *
     * @return
     */
    private void descartes() {
        dataList.clear();
        // 计算总数
        int totalCount = 1;
        for (Object groupValue : groupContext.values()) {
            totalCount *= groupValue.getClass().isArray() ? Array.getLength(groupValue) : ((Collection) groupValue).size();
        }
        // 生成分组项
        for (int i = 0; i < totalCount; i++) {
            dataList.add(Maps.newHashMap(normalContext));
        }

        // 填充分组项
        for (String key : groupContext.keySet()) {
            Object groupValue = groupContext.get(key);
            int size = groupValue.getClass().isArray() ? Array.getLength(groupValue) : ((Collection) groupValue).size();

            for (int i = 0; i < size; i++) {
                Object value = groupValue.getClass().isArray() ? Array.get(groupValue, i) : ((Collection) groupValue).toArray()[i];
                for (int j = 0; j < totalCount; j++) {
                    if (j % size == i) {
                        Map<String, Object> dataMap = dataList.get(j);
                        dataMap.put(key, value);
                    }
                }
            }
        }
    }

    public Date getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public Long getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(Long intervalTime) {
        this.intervalTime = intervalTime;
    }

    public List<AlarmMessage> getAlaramMessageList() {
        return alaramMessageList;
    }

    public List<Map<String, Object>> getDataList() {
        return dataList;
    }

    public List<AsAlarmTriggerRecord> getTriggerRecords() {
        return triggerRecords;
    }

    public Long getStartTimeStamp() {
        return startTimeStamp;
    }
}
