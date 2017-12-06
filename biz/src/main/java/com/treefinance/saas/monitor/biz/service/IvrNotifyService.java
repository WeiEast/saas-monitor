package com.treefinance.saas.monitor.biz.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.config.IvrConfig;
import com.treefinance.saas.monitor.common.domain.dto.IvrContactsDTO;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.utils.AESUtils;
import com.treefinance.saas.monitor.common.utils.HttpClientUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    /**
     * 通知ivr
     */
    public void notifyIvr(EAlarmLevel alarmLevel, String alarmRule) {
        if (!"on".equalsIgnoreCase(ivrConfig.getIvrSwitch())) {
            logger.info("ivr 服务开关关闭...{}", JSON.toJSONString(ivrConfig));
            return;
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
        // 3.组合参数
        Map<String, Object> jsonMessage = initMessageBody(alarmInfo, contactsDTOS);
        // 4.加密参数
        Map<String, String> paramsMessage = encrytMessageBody(jsonMessage);
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
                try {
                    // 根据cron表达式来判断此人是否负责当前时间点
                    if (new CronExpression(cron).isSatisfiedBy(currentTime)) {
                        ivrContactsDTOList.add(contactsDTO);
                    }
                } catch (ParseException e) {
                    logger.error(" duty time cron error : contactsDTO={}", JSON.toJSONString(contactsDTO), e);
                }
            }
        }
        return ivrContactsDTOList;
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
        contactsDTOS.forEach(ivrContactsDTO -> {
            Map<String, Object> msgMap = Maps.newHashMap();
            msgMap.put("name", ivrContactsDTO.getName());
            msgMap.put("alarmMessage", alarmInfo);

            Map<String, Object> taskItem = Maps.newHashMap();
            taskItem.put("refId", "1");
            taskItem.put("telNum", ivrContactsDTO.getTelNum());
            taskItem.put("userId", 1);
            taskItem.put("ivrParams", msgMap);
            taskItems.add(taskItem);
        });

        Map<String, Object> ivrParams = Maps.newHashMap();
        ivrParams.put("system", "saas");
        ivrParams.put("sysUserId", 1);
        ivrParams.put("useGroup", 1);
        ivrParams.put("modelId", ivrConfig.getModelId());
        ivrParams.put("executeTime", DateFormatUtils.format(new Date(), "yyyy-MM-dd hh:mm:ss"));
        ivrParams.put("executeNotify", false);
        ivrParams.put("taskItems", taskItems);
        return ivrParams;
    }

    /**
     * 发送消息
     *
     * @param paramsMap
     */
    @Async
    public void sendMessage(Map<String, String> paramsMap) {
        int count = ivrConfig.getIvrCount() == null ? 3 : ivrConfig.getIvrCount();
        for (int i = 0; i < count; i++) {
            Map<String, String> headerMap = Maps.newHashMap();
            headerMap.put("token", ivrConfig.getIvrToken());
            CloseableHttpResponse response = null;
            try {
                response = HttpClientUtils.fullyPost(ivrConfig.getIvrUrl(), paramsMap, headerMap, null);
                Thread.sleep(60000);
            } catch (Exception e) {
                logger.error("send ivr message exception: url={},paramsMap={},headerMap={}", ivrConfig.getIvrUrl(),
                        JSON.toJSONString(paramsMap), JSON.toJSONString(headerMap), e);
            } finally {
                HttpClientUtils.closeResponse(response);
            }
        }
    }
}
