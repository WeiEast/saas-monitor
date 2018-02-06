package com.treefinance.saas.monitor.biz.service.impl;

import com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;
import com.treefinance.saas.monitor.biz.config.IvrConfig;
import com.treefinance.saas.monitor.biz.service.IvrCallBackService;
import com.treefinance.saas.monitor.common.exceptions.RequestFailedException;
import com.treefinance.saas.monitor.common.result.IvrCallBackResult;
import com.treefinance.saas.monitor.common.utils.AESUtils;
import com.treefinance.saas.monitor.common.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:guoguoyun
 * @date:Created in 2018/1/31下午3:54
 */
@Service
public class IvrCallBackServiceImpl implements IvrCallBackService {

    private static final Logger logger = LoggerFactory.getLogger(IvrCallBackServiceImpl.class);

    @Autowired
    IvrConfig ivrConfig;

    @Override
    public void dealIvrCallBackMessage(IvrCallBackResult ivrCallBackResult) {

        if (("success").equals(ivrCallBackResult.getStatus()) || ("cancel").equals(ivrCallBackResult.getStatus())) {
            logger.info("IVR状态信息为{}", ivrCallBackResult.getStatus());
            return;
        }
        logger.info("IVR状态信息为{}", ivrCallBackResult.getStatus());
        Map<String, String> stringStringMap = new HashMap<String, String>();
        stringStringMap.put("taskId", String.valueOf(ivrCallBackResult.getTaskId()));
        Map<String, String> map = new HashMap<String, String>();

        try {
            map.put("params", AESUtils.encrytDataWithBase64AsString(JSONObject.toJSONString(stringStringMap), ivrConfig.getAccessKey()));
        } catch (Exception e) {
            logger.error("encrytData exception map集合为{},密钥为{}",JSONObject.toJSONString(stringStringMap),ivrConfig.getAccessKey(), e);
        }


        //写入Header的toke信息
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("token", ivrConfig.getToken());


        //HTTP  post 请求
        try {
            HttpClientUtils.doPostwithHeader(ivrConfig.getIvrReplayurl(), JSONObject.toJSONString(map), map1);
        } catch (RequestFailedException e) {
            logger.error("RequestFailedException  callbackurl为{},aesContent为{},token信息为{}",ivrConfig.getIvrReplayurl(),JSONObject.toJSONString(map),map1.toString(),e);
        }


    }
}
