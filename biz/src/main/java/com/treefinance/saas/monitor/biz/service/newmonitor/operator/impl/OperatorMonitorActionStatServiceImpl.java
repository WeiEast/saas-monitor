package com.treefinance.saas.monitor.biz.service.newmonitor.operator.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.saas.assistant.model.TaskOperatorMonitorMessage;
import com.treefinance.saas.grapserver.facade.model.enums.ETaskOperatorMonitorStatus;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper;
import com.treefinance.saas.monitor.biz.service.newmonitor.operator.OperatorMonitorActionStatService;
import com.treefinance.saas.monitor.biz.service.newmonitor.operator.TaskOperatorMonitorMessageProcessor;
import com.treefinance.saas.monitor.common.domain.dto.OperatorMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.ETaskOperatorStatType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by haojiahong on 2017/11/14.
 */
@Service
public class OperatorMonitorActionStatServiceImpl implements OperatorMonitorActionStatService {
    private static final Logger logger = LoggerFactory.getLogger(OperatorMonitorActionStatService.class);

    @Autowired
    private TaskOperatorMonitorMessageProcessor taskOperatorMonitorMessageProcessor;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private DiamondConfig diamondConfig;


    @Override
    public void updateIntervalDataByTask(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status) {
        if (message == null || StringUtils.isBlank(message.getGroupCode()) || StringUtils.isBlank(message.getGroupName())) {
            logger.error("运营商监控,消息处理,groupCode,groupName为空,message={}", JSON.toJSONString(message));
            return;
        }
        taskOperatorMonitorMessageProcessor.updateIntervalData(intervalTime, message, status, ETaskOperatorStatType.TASK);
    }

    @Override
    public void updateDayDataByTask(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status) {
        if (message == null || StringUtils.isBlank(message.getGroupCode()) || StringUtils.isBlank(message.getGroupName())) {
            logger.error("运营商监控,消息处理,groupCode,groupName为空,message={}", JSON.toJSONString(message));
            return;
        }
        taskOperatorMonitorMessageProcessor.updateDayData(intervalTime, message, status, ETaskOperatorStatType.TASK);

    }

    @Override
    public void updateAllIntervalDataByTask(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status) {
        taskOperatorMonitorMessageProcessor.updateAllIntervalData(intervalTime, message, status, ETaskOperatorStatType.TASK);

    }

    @Override
    public void updateAllDayDataByTask(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status) {
        taskOperatorMonitorMessageProcessor.updateAllDayData(intervalTime, message, status, ETaskOperatorStatType.TASK);
    }

    @Override
    public void updateIntervalDataByUser(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status) {
        if (message == null || StringUtils.isBlank(message.getGroupCode()) || StringUtils.isBlank(message.getGroupName())) {
            logger.error("运营商监控,消息处理,groupCode,groupName为空,message={}", JSON.toJSONString(message));
            return;
        }

        OperatorMonitorAlarmConfigDTO config = null;
        Date userIntervalTime = intervalTime;
        String configStr = diamondConfig.getOperatorMonitorAlarmConfig();
        List<OperatorMonitorAlarmConfigDTO> configList = JSONObject.parseArray(configStr, OperatorMonitorAlarmConfigDTO.class);
        for (OperatorMonitorAlarmConfigDTO configDTO : configList) {
            if (StringUtils.equals(TaskOperatorMonitorKeyHelper.VIRTUAL_TOTAL_STAT_APP_ID, configDTO.getAppId())
                    && configDTO.getAlarmType() == 2) {
                config = configDTO;
            }
        }
        if (config != null) {
            userIntervalTime = TaskOperatorMonitorKeyHelper.getRedisStatDateTime(message.getDataTime(), config.getIntervalMins());
        }

        StringBuilder sb = new StringBuilder();
        String uniqueValue = sb.append(message.getAppId()).append("-").append(message.getUniqueId()).toString();//区分用户的唯一键值
        //判断此用户此操作是否已经统计过
        String usersKey = TaskOperatorMonitorKeyHelper.keyOfUsersGroupOnIntervalStat(userIntervalTime, message.getGroupCode(), status, message.getAppId());
        BoundSetOperations<String, Object> setOperations = redisTemplate.boundSetOps(usersKey);
        if (setOperations.isMember(uniqueValue)) {
            logger.info("运营商监控,消息处理,uniqueValue={}在时刻点userIntervalTime={}时,操作状态status={}已经统计过了,不再统计.message={}",
                    uniqueValue, MonitorDateUtils.format(userIntervalTime), status, JSON.toJSONString(message));
            return;
        }
        setOperations.add(uniqueValue);
        if (setOperations.getExpire() == -1) {
            setOperations.expire(2, TimeUnit.HOURS);
        }
        //统计时段内只统计用户一个手机号的数据
        String accountNo = message.getAccountNo();
        if (StringUtils.isNotBlank(accountNo)) {
            String userMobileKey = TaskOperatorMonitorKeyHelper.keyOfIntervalUsersMobileLog(userIntervalTime, message.getAppId());
            BoundHashOperations<String, String, String> hashOperations = redisTemplate.boundHashOps(userMobileKey);
            String oldAccountNo = hashOperations.get(uniqueValue);
            if (StringUtils.isNotBlank(oldAccountNo)) {
                if (!StringUtils.equals(oldAccountNo, accountNo)) {
                    logger.info("运营商监控,消息处理,uniqueValue={}在时刻点userIntervalTime={}时,已统计过oldAccountNo={},不再统计新的账号accountNo={}数据.",
                            uniqueValue, MonitorDateUtils.format(userIntervalTime), oldAccountNo, accountNo);
                    return;
                }
            } else {
                hashOperations.put(uniqueValue, accountNo);
            }
            if (hashOperations.getExpire() == -1) {
                hashOperations.expire(2, TimeUnit.HOURS);
            }

        }
        taskOperatorMonitorMessageProcessor.updateIntervalData(intervalTime, message, status, ETaskOperatorStatType.USER);

    }

    @Override
    public void updateDayDataByUser(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status) {
        if (message == null || StringUtils.isBlank(message.getGroupCode()) || StringUtils.isBlank(message.getGroupName())) {
            logger.error("运营商监控,消息处理,groupCode,groupName为空,message={}", JSON.toJSONString(message));
            return;
        }
        StringBuilder sb = new StringBuilder();
        String uniqueValue = sb.append(message.getAppId()).append("-").append(message.getUniqueId()).toString();

        //判断此用户此操作是否已经统计过
        String usersKey = TaskOperatorMonitorKeyHelper.keyOfUsersGroupOnDayStat(intervalTime, message.getGroupCode(), status, message.getAppId());
        BoundSetOperations<String, Object> setOperations = redisTemplate.boundSetOps(usersKey);
        if (setOperations.isMember(uniqueValue)) {
            logger.info("运营商监控,消息处理,uniqueValue={}在时刻点intervalTime={}时,操作状态status={}已经统计过了,不再统计.message={}",
                    uniqueValue, MonitorDateUtils.format(intervalTime), status, JSON.toJSONString(message));
            return;
        }
        setOperations.add(uniqueValue);
        if (setOperations.getExpire() == -1) {
            setOperations.expire(2, TimeUnit.HOURS);
        }

        //统计时段内只统计用户一个手机号的数据
        String accountNo = message.getAccountNo();
        if (StringUtils.isNotBlank(accountNo)) {
            String userMobileKey = TaskOperatorMonitorKeyHelper.keyOfDayUsersMobileLog(intervalTime, message.getAppId());
            BoundHashOperations<String, String, String> hashOperations = redisTemplate.boundHashOps(userMobileKey);
            String oldAccountNo = hashOperations.get(uniqueValue);
            if (StringUtils.isNotBlank(oldAccountNo)) {
                if (!StringUtils.equals(oldAccountNo, accountNo)) {
                    logger.info("运营商监控,消息处理,uniqueValue={}在intervalTime={}时,已统计过oldAccountNo={},不再统计新的账号accountNo={}数据.",
                            uniqueValue, MonitorDateUtils.format2Ymd(intervalTime), oldAccountNo, accountNo);
                    return;
                }
            } else {
                hashOperations.put(uniqueValue, accountNo);
            }
            if (hashOperations.getExpire() == -1) {
                hashOperations.expire(2, TimeUnit.DAYS);
            }

        }

        taskOperatorMonitorMessageProcessor.updateDayData(intervalTime, message, status, ETaskOperatorStatType.USER);

    }

    @Override
    public void updateAllIntervalDataByUser(Date intervalTime, TaskOperatorMonitorMessage message, ETaskOperatorMonitorStatus status) {
        StringBuilder sb = new StringBuilder();
        String uniqueValue = sb.append(message.getAppId()).append("-").append(message.getUniqueId()).toString();
        OperatorMonitorAlarmConfigDTO config = null;
        Date userIntervalTime = intervalTime;
        String configStr = diamondConfig.getOperatorMonitorAlarmConfig();
        List<OperatorMonitorAlarmConfigDTO> configList = JSONObject.parseArray(configStr, OperatorMonitorAlarmConfigDTO.class);
        for (OperatorMonitorAlarmConfigDTO configDTO : configList) {
            if (StringUtils.equals(TaskOperatorMonitorKeyHelper.VIRTUAL_TOTAL_STAT_APP_ID, configDTO.getAppId())
                    && configDTO.getAlarmType() == 1) {
                config = configDTO;
            }
        }
        if (config != null) {
            userIntervalTime = TaskOperatorMonitorKeyHelper.getRedisStatDateTime(message.getDataTime(), config.getIntervalMins());
        }

        //判断此用户此操作是否已经统计过
        String usersKey = TaskOperatorMonitorKeyHelper.keyOfUsersAllOnIntervalStat(userIntervalTime, status, message.getAppId());
        BoundSetOperations<String, Object> setOperations = redisTemplate.boundSetOps(usersKey);
        if (setOperations.isMember(uniqueValue)) {
            logger.info("运营商监控,消息处理,uniqueValue={}在时刻点userIntervalTime={}时,操作状态status={}已经统计过了,不再统计.message={}",
                    uniqueValue, MonitorDateUtils.format(userIntervalTime), status, JSON.toJSONString(message));
            return;
        }
        setOperations.add(uniqueValue);
        if (setOperations.getExpire() == -1) {
            setOperations.expire(2, TimeUnit.HOURS);
        }

        //统计时段内只统计用户一个手机号的数据
        String accountNo = message.getAccountNo();
        if (StringUtils.isNotBlank(accountNo)) {
            String userMobileKey = TaskOperatorMonitorKeyHelper.keyOfIntervalUsersMobileLog(userIntervalTime, message.getAppId());
            BoundHashOperations<String, String, String> hashOperations = redisTemplate.boundHashOps(userMobileKey);
            String oldAccountNo = hashOperations.get(uniqueValue);
            if (StringUtils.isNotBlank(oldAccountNo)) {
                if (!StringUtils.equals(oldAccountNo, accountNo)) {
                    logger.info("运营商监控,消息处理,uniqueValue={}在userIntervalTime={}时,已统计过oldAccountNo={},不再统计新的账号accountNo={}数据.",
                            uniqueValue, MonitorDateUtils.format(userIntervalTime), oldAccountNo, accountNo);
                    return;
                }
            } else {
                hashOperations.put(uniqueValue, accountNo);
            }
            if (hashOperations.getExpire() == -1) {
                hashOperations.expire(2, TimeUnit.HOURS);
            }

        }

        taskOperatorMonitorMessageProcessor.updateAllIntervalData(intervalTime, message, status, ETaskOperatorStatType.USER);

    }

    @Override
    public void updateAllDayDataByUser(Date intervalTime, TaskOperatorMonitorMessage
            message, ETaskOperatorMonitorStatus status) {
        StringBuilder sb = new StringBuilder();
        String uniqueValue = sb.append(message.getAppId()).append("-").append(message.getUniqueId()).toString();

        //判断此用户此操作是否已经统计过
        String usersKey = TaskOperatorMonitorKeyHelper.keyOfUsersAllOnDayStat(intervalTime, status, message.getAppId());
        BoundSetOperations<String, Object> setOperations = redisTemplate.boundSetOps(usersKey);
        if (setOperations.isMember(uniqueValue)) {
            logger.info("运营商监控,消息处理,uniqueId={}在时刻点intervalTime={}时,操作状态status={}已经统计过了,不再统计.message={}",
                    message.getUniqueId(), MonitorDateUtils.format(intervalTime), status, JSON.toJSONString(message));
            return;
        }
        setOperations.add(uniqueValue);
        if (setOperations.getExpire() == -1) {
            setOperations.expire(2, TimeUnit.HOURS);
        }

        //统计时段内只统计用户一个手机号的数据
        String accountNo = message.getAccountNo();
        if (StringUtils.isNotBlank(accountNo)) {
            String userMobileKey = TaskOperatorMonitorKeyHelper.keyOfDayUsersMobileLog(intervalTime, message.getAppId());
            BoundHashOperations<String, String, String> hashOperations = redisTemplate.boundHashOps(userMobileKey);
            String oldAccountNo = hashOperations.get(uniqueValue);
            if (StringUtils.isNotBlank(oldAccountNo)) {
                if (!StringUtils.equals(oldAccountNo, accountNo)) {
                    logger.info("运营商监控,消息处理,uniqueValue={}在intervalTime={}时,已统计过oldAccountNo={},不再统计新的账号accountNo={}数据.",
                            uniqueValue, MonitorDateUtils.format2Ymd(intervalTime), oldAccountNo, accountNo);
                    return;
                }
            } else {
                hashOperations.put(uniqueValue, accountNo);
            }
            if (hashOperations.getExpire() == -1) {
                setOperations.expire(2, TimeUnit.HOURS);
            }

        }

        taskOperatorMonitorMessageProcessor.updateAllDayData(intervalTime, message, status, ETaskOperatorStatType.USER);

    }

    @Override
    public void updateAllIntervalTaskUserCountByTask(Date intervalTime, TaskOperatorMonitorMessage msg) {
        taskOperatorMonitorMessageProcessor.updateAllIntervalTaskUserCount(intervalTime, msg, ETaskOperatorStatType.TASK);
    }

    @Override
    public void updateAllIntervalTaskUserCountByUser(Date intervalTime, TaskOperatorMonitorMessage msg) {
        taskOperatorMonitorMessageProcessor.updateAllIntervalTaskUserCount(intervalTime, msg, ETaskOperatorStatType.USER);

    }

    @Override
    public void updateAllDayTaskUserCountByTask(Date intervalTime, TaskOperatorMonitorMessage msg) {
        taskOperatorMonitorMessageProcessor.updateAllDayTaskUserCount(intervalTime, msg, ETaskOperatorStatType.TASK);

    }

    @Override
    public void updateallDayTaskUserCountByUser(Date intervalTime, TaskOperatorMonitorMessage msg) {
        taskOperatorMonitorMessageProcessor.updateAllDayTaskUserCount(intervalTime, msg, ETaskOperatorStatType.USER);

    }
}
