package com.treefinance.saas.monitor.biz.alarm.service.handler.chain;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmConfig;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmContext;
import com.treefinance.saas.monitor.biz.alarm.service.handler.AlarmHandler;
import com.treefinance.saas.monitor.biz.alarm.service.handler.AlarmHandlerChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 预定义5步处理链：
 * （1）常量
 * （2）数据查询
 * （3）变量
 * （4）触发条件
 * （5）预警消息
 * Created by yh-treefinance on 2018/7/24.
 */
@Component
public class FiveAlarmHandlerChain implements AlarmHandlerChain {

    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private List<AlarmHandler> handlerList;

    @Override
    public AlarmContext handle(AlarmConfig config) {
        AlarmContext context = new AlarmContext();
        // 根据order排序
        List<AlarmHandler> handlerList = this.handlerList.stream().sorted(AlarmHandler::compareTo).collect(Collectors.toList());
        // 调用处理链处理
        handlerList.forEach(alarmHandler -> {
            long start = System.currentTimeMillis();
            try {
                alarmHandler.handle(config, context);
            } finally {
                logger.info("alarm handle chain running:  handle={} , alarm={} cost {}ms,", alarmHandler.getClass(), JSON.toJSONString(config.getAlarm()), System.currentTimeMillis() - start);
            }
        });
        return context;
    }
}
