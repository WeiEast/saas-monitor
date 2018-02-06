package com.treefinance.saas.monitor.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.monitor.biz.config.IvrConfig;
import com.treefinance.saas.monitor.common.domain.Constants;
import com.treefinance.saas.monitor.common.domain.dto.IvrContactsDTO;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
import com.treefinance.saas.monitor.common.result.IvrCallBackResult;
import com.treefinance.saas.monitor.common.utils.AESUtils;
import com.treefinance.saas.monitor.common.utils.HttpClientUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by yh-treefinance on 2017/12/6.
 */
@Component
public class IvrNotifyService {
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(IvrNotifyService.class);

    @Autowired
    private IvrConfig ivrConfig;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 通知ivr
     */
    public void notifyIvr(EAlarmLevel alarmLevel, EAlarmType type, String alarmRule) {
        if (!"on".equalsIgnoreCase(ivrConfig.getIvrSwitch())) {
            logger.info("ivr 服务开关关闭...{}", JSON.toJSONString(ivrConfig));
            return;
        }
        // 验证此类预警是否需要通知
        String alarmTypeCron = ivrConfig.getAlarmTypeCron();
        Map<String, Object> cronMap = JSON.parseObject(alarmTypeCron);
        if (MapUtils.isNotEmpty(cronMap) && cronMap.get(type.getCode()) != null) {
            String typeCron = cronMap.get(type.getCode()).toString();
            // 根据cron表达式来判断此人是否负责当前时间点
            if (!isSatisfiedBy(new Date(), typeCron)) {
                logger.info("告警类型不在指定时段：alarmLevel={},type={},alarmRule={}," +
                        "alarmTypeCron={}", alarmLevel, type, alarmRule, alarmTypeCron);
                return;
            }
        }

        // 1.取得联系人
        List<IvrContactsDTO> contactsDTOS = getDutyContacts();
        if (CollectionUtils.isEmpty(contactsDTOS)) {
            logger.info("当前预警时间未指认负责人:{},{}", alarmLevel, alarmRule);
            return;
        }
        // 2.告警信息
        String alarmInfo = new StringBuffer()
                .append("【" + alarmLevel.name() + "】")
                .append("saas-" + ivrConfig.getEnvironment())
                .append(alarmRule).toString();
        Map<String, Object> jsonMap = initMessageBody(alarmInfo, contactsDTOS);
        // 4.加密参数
        Map<String, String> paramsMessage = encrytMessageBody(jsonMap);
        // 5.发送请求
        sendMessage(paramsMessage);
    }


    /**
     * 获取匹配的通知人
     *
     * @return
     */
    public List<IvrContactsDTO> getDutyContacts() {
        // 获取联系人心
        String contacts = ivrConfig.getContacts();
        List<IvrContactsDTO> contactsDTOS = JSON.parseArray(contacts, IvrContactsDTO.class);
        if (CollectionUtils.isEmpty(contactsDTOS)) {
            return Lists.newArrayList();
        }
        List<IvrContactsDTO> ivrContactsDTOList = Lists.newArrayList();
        Date currentTime = new Date();
        for (IvrContactsDTO contactsDTO : contactsDTOS) {
            String[] cronArr = contactsDTO.getDutyTimeCron();
            if (ArrayUtils.isEmpty(cronArr)) {
                continue;
            }
            // cron表达式
            for (String cron : cronArr) {
                if (ivrContactsDTOList.contains(contactsDTO)) {
                    break;
                }
                // 根据cron表达式来判断此人是否负责当前时间点
                if (isSatisfiedBy(currentTime, cron)) {
                    ivrContactsDTOList.add(contactsDTO);
                } else {
                    logger.info(" duty time cron error : contactsDTO={}", JSON.toJSONString(contactsDTO));
                }
            }
        }
        return ivrContactsDTOList;
    }

    /**
     * 是否在cron时段内
     *
     * @param currentTime
     * @param cron
     * @return
     */
    private boolean isSatisfiedBy(Date currentTime, String cron) {
        try {
            return new CronExpression(cron).isSatisfiedBy(currentTime);
        } catch (ParseException e) {
            logger.error(" duty time cron error : contactsDTO={}", JSON.toJSONString(cron), e);
        }
        return false;
    }


    /**
     * 加密参数
     *
     * @return
     */
    protected Map<String, String> encrytMessageBody(Map<String, Object> jsonMap) {
        Map<String, String> paramMap = Maps.newHashMap();
        String jsonString = JSON.toJSONString(jsonMap);
        try {
            jsonString = AESUtils.encrytDataWithBase64AsString(jsonString, ivrConfig.getIvrKey());
        } catch (Exception e) {
            logger.error("encry ivr message exception: json={}", jsonString, e);
        }
        paramMap.put("params", jsonString);
        return paramMap;
    }

    /**
     * 初始化消息体
     */
    protected Map<String, Object> initMessageBody(String alarmInfo, List<IvrContactsDTO> contactsDTOS) {
        List<Map<String, Object>> taskItems = Lists.newArrayList();
        Long refId = UidGenerator.getId();

        contactsDTOS.forEach(ivrContactsDTO -> {
            Map<String, Object> msgMap = Maps.newHashMap();
            msgMap.put("name", ivrContactsDTO.getName());
            msgMap.put("alarmMessage", alarmInfo);

            Map<String, Object> taskItem = Maps.newHashMap();
            taskItem.put("refId", refId);
            taskItem.put("telNum", ivrContactsDTO.getTelNum());
            taskItem.put("userId", 1);
            taskItem.put("ivrParams", msgMap);
            taskItems.add(taskItem);
        });

        Map<String, Object> ivrParams = Maps.newHashMap();
        ivrParams.put("system", "saas");
        ivrParams.put("sysUserId", 1);
        ivrParams.put("useGroup", getUserGroup());
        ivrParams.put("modelId", ivrConfig.getModelId());
        ivrParams.put("executeTime", DateFormatUtils.format(new Date(), "yyyy-MM-dd hh:mm:ss"));
        ivrParams.put("executeNotify", true);
        ivrParams.put("taskItems", taskItems);

        // 缓存消息
        String redisKey = Constants.PREFIX_KEY + ":ivr-message:" + refId;
        redisTemplate.opsForValue().set(redisKey, JSON.toJSONString(ivrParams), 1, TimeUnit.DAYS);

        logger.info("send ivr message: url={},ivrParams={},contactsDTOS={}", alarmInfo,
                JSON.toJSONString(ivrParams),
                JSON.toJSONString(contactsDTOS));
        return ivrParams;
    }

    /**
     * 随机生成urserGroup
     *
     * @return
     */
    private byte getUserGroup() {
        String key = Constants.PREFIX_KEY + ":ivr-user-group";
        String userGroupStr = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(userGroupStr)) {
            redisTemplate.opsForValue().increment(key, 0);
            return 0;
        }
        Integer userGroup = Integer.valueOf(userGroupStr);
        if (userGroup > 128) {
            redisTemplate.opsForValue().set(key, "0");
            return (byte) 0;
        } else {
            redisTemplate.opsForValue().increment(key, 1);
            return userGroup.byteValue();
        }
    }

    /**
     * 发送消息
     *
     * @param paramsMap
     */
    @Async
    public void sendMessage(Map<String, String> paramsMap) {
        Map<String, String> headerMap = Maps.newHashMap();
        headerMap.put("token", ivrConfig.getIvrToken());
        CloseableHttpResponse response = null;
        try {
            response = HttpClientUtils.fullyPost(ivrConfig.getIvrUrl(), paramsMap, headerMap, null);
        } catch (Exception e) {
            logger.error("send ivr message exception: url={},paramsMap={},headerMap={}", ivrConfig.getIvrUrl(),
                    JSON.toJSONString(paramsMap), JSON.toJSONString(headerMap), e);
        } finally {
            HttpClientUtils.closeResponse(response);
            logger.info("send ivr message: url={},paramsMap={},headerMap={}", ivrConfig.getIvrUrl(),
                    JSON.toJSONString(paramsMap), JSON.toJSONString(headerMap));
        }

    }

    /**
     * 重播MQ消息
     */
    public void resendMessage(IvrCallBackResult callBackResult) {
        Long currentCount = 0L;

        if (!("fail").equals(callBackResult.getStatus())) {
            logger.info("resend ivr message error : IVR状态信息为{}", JSON.toJSONString(callBackResult));
            return;
        }

        Long refId = callBackResult.getRefId();
        String redisKey = Constants.PREFIX_KEY + ":ivr-message:" + refId;
        String message = redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isEmpty(message)) {
            logger.error("resend ivr message error : message[{}] is empty", redisKey);
            return;
        }
        Map<String, Object> jsonMap = JSON.parseObject(message);
        Map<String, String> paramsMap = encrytMessageBody(jsonMap);

        // 计数判断
        String countKey = Constants.PREFIX_KEY + ":ivr-message:resend-count:" + refId;
        currentCount = redisTemplate.opsForValue().increment(countKey, 1) - 1;
        if (currentCount < ivrConfig.getIvrCount()) {
            sendMessage(paramsMap);
            logger.info("resend ivr message : ivrcount={}, resendcount={} , callback={}, message={}, encrytJson={}",
                    ivrConfig.getIvrCount(), currentCount, JSON.toJSONString(callBackResult), JSON.toJSONString(jsonMap),
                    JSON.toJSONString(paramsMap));
            return;
        }
        redisTemplate.expire(countKey, 1, TimeUnit.HOURS);
        logger.info("resend ivr message : failed, ivrcount={} < resendcount={} , callback={}, message={}, encrytJson={}",
                ivrConfig.getIvrCount(), currentCount, JSON.toJSONString(callBackResult), JSON.toJSONString(jsonMap),
                JSON.toJSONString(paramsMap));
    }
}
