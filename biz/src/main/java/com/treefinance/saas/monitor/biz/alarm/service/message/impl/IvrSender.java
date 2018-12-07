package com.treefinance.saas.monitor.biz.alarm.service.message.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.b2b.saas.util.DataUtils;
import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmMessage;
import com.treefinance.saas.monitor.biz.alarm.service.message.MessageSender;
import com.treefinance.saas.monitor.biz.alarm.service.message.MsgChannel;
import com.treefinance.saas.monitor.biz.config.IvrConfig;
import com.treefinance.saas.monitor.common.domain.Constants;
import com.treefinance.saas.monitor.common.enumeration.EAlarmChannel;
import com.treefinance.saas.monitor.dao.entity.SaasWorker;
import com.treefinance.saas.monitor.util.HttpClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.treefinance.saas.monitor.common.constants.AlarmConstants.SWITCH_ON;

/**
 * ivr 消息发送
 * Created by yh-treefinance on 2018/7/30.
 */
@MsgChannel(EAlarmChannel.IVR)
@Component
public class IvrSender implements MessageSender {
    /**
     * logger
     */
    private static final Logger logger = LoggerFactory.getLogger(IvrSender.class);

    @Autowired
    private IvrConfig ivrConfig;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private UidService uidService;

    @Override
    public void sendMessage(AlarmMessage alaramMessage, List<SaasWorker> recivers) {
        if (!SWITCH_ON.equalsIgnoreCase(ivrConfig.getIvrSwitch())) {
            logger.info("ivr message sender closed : ivrconfig={}, alaramMessage={},recivers={}",
                    JSON.toJSONString(ivrConfig), JSON.toJSONString(alaramMessage), JSON.toJSONString(recivers));
            return;
        }
        // 告警信息
        String saasEnvDesc = "saas-" + ivrConfig.getEnvironment();
        String alarmInfo = new StringBuffer()
                .append("【" + alaramMessage.getAlarmLevel().name() + "】")
                .append(saasEnvDesc)
                .append(alaramMessage.getMessage()).toString();
        Map<String, Object> bodyMap = generateBody(alarmInfo, recivers);
        Map<String, String> encrytBody = encryptBody(bodyMap);
        doSend(encrytBody);
        logger.info("ivr message sender : bodyMap={}, ivrconfig={}, alaramMessage={},recivers={}",
                JSON.toJSONString(bodyMap), JSON.toJSONString(ivrConfig), JSON.toJSONString(alaramMessage), JSON.toJSONString(recivers));
    }

    /**
     * 初始化消息体
     */
    protected Map<String, Object> generateBody(String alarmInfo, List<SaasWorker> saasWorkers) {
        List<Map<String, Object>> taskItems = Lists.newArrayList();
        Long refId = uidService.getId();

        saasWorkers.stream().filter(saasWorker -> StringUtils.isNotEmpty(saasWorker.getMobile()))
                .forEach(saasWorker -> {
                    Map<String, Object> msgMap = Maps.newHashMap();
                    msgMap.put("alarmMessage", alarmInfo);
                    msgMap.put("name", saasWorker.getName());

                    Map<String, Object> taskItem = Maps.newHashMap();
                    taskItem.put("refId", refId);
                    taskItem.put("telNum", saasWorker.getMobile());
                    taskItem.put("userId", 1);
                    taskItem.put("ivrParams", msgMap);
                    taskItems.add(taskItem);
                });

        Map<String, Object> ivrParams = Maps.newHashMap();
        ivrParams.put("system", "saas");
        ivrParams.put("sysUserId", 1);
        ivrParams.put("useGroup", generateUserGroup());
        ivrParams.put("modelId", ivrConfig.getModelId());
        ivrParams.put("executeTime", DateFormatUtils.format(new Date(), "yyyy-MM-dd hh:mm:ss"));
        ivrParams.put("executeNotify", true);
        ivrParams.put("taskItems", taskItems);

        // 缓存消息
        String redisKey = Constants.PREFIX_KEY + ":ivr-message:" + refId;
        redisTemplate.opsForValue().set(redisKey, JSON.toJSONString(ivrParams), 1, TimeUnit.DAYS);
        return ivrParams;
    }

    /**
     * 随机生成urserGroup
     *
     * @return
     */
    private byte generateUserGroup() {
        String key = Constants.PREFIX_KEY + ":ivr-user-group";
        String userGroupStr = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(userGroupStr)) {
            redisTemplate.opsForValue().increment(key, 0);
            return 0;
        }
        Integer userGroup = Integer.valueOf(userGroupStr);
        if (userGroup > 128) {
            redisTemplate.opsForValue().set(key, "0");
            return Byte.valueOf("0");
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
    public void doSend(Map<String, String> paramsMap) {
        Map<String, String> headerMap = Maps.newHashMap();
        headerMap.put("token", ivrConfig.getIvrToken());
        try (CloseableHttpResponse response = HttpClientUtils.fullyPost(ivrConfig.getIvrUrl(), paramsMap, headerMap, null)) {
            logger.info("send ivr message: url={},paramsMap={},headerMap={}", ivrConfig.getIvrUrl(),
                    JSON.toJSONString(paramsMap), JSON.toJSONString(headerMap));
        } catch (Exception e) {
            logger.error("send ivr message exception: url={},paramsMap={},headerMap={}", ivrConfig.getIvrUrl(),
                    JSON.toJSONString(paramsMap), JSON.toJSONString(headerMap), e);
        }
    }

    /**
     * 加密参数
     *
     * @return
     */
    private Map<String, String> encryptBody(Map<String, Object> bodyMap) {
        Map<String, String> encryBodyMap = Maps.newHashMap();
        String jsonBody;
        try {
            jsonBody = DataUtils.encryptBeanAsBase64StringByAes(bodyMap, ivrConfig.getIvrKey());
        } catch (Exception e) {
            jsonBody = JSON.toJSONString(bodyMap);
            logger.error("encrypt ivr message exception: jsonBody={}", jsonBody, e);
        }
        encryBodyMap.put("params", jsonBody);
        return encryBodyMap;
    }


}
