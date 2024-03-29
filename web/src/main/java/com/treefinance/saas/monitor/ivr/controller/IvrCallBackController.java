package com.treefinance.saas.monitor.ivr.controller;

import com.treefinance.saas.monitor.biz.service.IvrNotifyService;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
import com.treefinance.saas.monitor.common.result.IvrCallBackResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author:guoguoyun
 * @date:Created in 2018/1/31下午3:37
 */
@RestController
@RequestMapping("/ivr")
public class IvrCallBackController {
    private static final Logger logger = LoggerFactory.getLogger(IvrCallBackController.class);

    @Autowired
    private IvrNotifyService ivrNotifyService;

    @RequestMapping("/callback")
    public String dealIvrCallBackMessage(@RequestBody IvrCallBackResult ivrCallBackResult) {
        logger.info("Ivr回调信息传入为{}", ivrCallBackResult.toString());
        ivrNotifyService.resendMessage(ivrCallBackResult);
        return "success";
    }


    @RequestMapping("/test")
    public String test(EAlarmLevel alarmLevel, EAlarmType type, String alarmRule) {
        ivrNotifyService.notifyIvr(alarmLevel, type, alarmRule);
        return "success";
    }
}
