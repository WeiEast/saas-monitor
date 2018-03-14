package com.treefinance.saas.monitor.biz.service;

import com.alibaba.fastjson.JSON;
import com.datatrees.notify.async.body.sms.SmsEnum;
import com.datatrees.notify.sms.newservice.SmsNewService;
import com.datatrees.notify.sms.newservice.entity.message.SmsMessage;
import com.google.common.base.Splitter;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 短信通知服务
 * Created by haojiahong on 2017/12/21.
 */
@Component
public class SmsNotifyService {

    private static final Logger logger = LoggerFactory.getLogger(SmsNotifyService.class);

    @Autowired
    private SmsNewService smsNewService;
    @Autowired
    private DiamondConfig diamondConfig;

    public void send(String content) {
        List<String> mobileList = Splitter.on(",").splitToList("18258265028");
        SmsMessage smsMessage = new SmsMessage();
        smsMessage.setSmsEnum(SmsEnum.CUI_SHOU);
        smsMessage.setContent(content);
        smsMessage.setMobileList(mobileList);
        smsMessage.setBusinessTag("saas预警");
        logger.info("saas预警发送短信:message={}", JSON.toJSONString(smsMessage));
        smsNewService.sendMessage(smsMessage);
    }

    public void send(String content, List<String> mobileList) {
        SmsMessage smsMessage = new SmsMessage();
        smsMessage.setSmsEnum(SmsEnum.CUI_SHOU);
        smsMessage.setContent(content);
        smsMessage.setMobileList(mobileList);
        smsMessage.setBusinessTag("saas预警");
        logger.info("saas预警发送短信:message={}", JSON.toJSONString(smsMessage));
        smsNewService.sendMessage(smsMessage);
    }


}
