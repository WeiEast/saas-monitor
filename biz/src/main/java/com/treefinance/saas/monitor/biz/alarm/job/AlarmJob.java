package com.treefinance.saas.monitor.biz.alarm.job;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.treefinance.saas.assistant.model.Constants;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmConfig;
import com.treefinance.saas.monitor.biz.alarm.model.AlarmContext;
import com.treefinance.saas.monitor.biz.alarm.service.AlarmConfigService;
import com.treefinance.saas.monitor.biz.alarm.service.handler.AlarmHandlerChain;
import com.treefinance.saas.monitor.common.enumeration.ESaasEnv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * 预警job
 * Created by yh-treefinance on 2018/7/18.
 */
public class AlarmJob implements SimpleJob {
    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 预警配置ID
     */
    private Long alarmId;
    /**
     * 预警配置ID
     */
    private AlarmConfigService alarmConfigService;
    /**
     * 预警处理链
     */
    private AlarmHandlerChain alarmHandlerChain;

    public AlarmJob(Long alarmId,
                    AlarmConfigService alarmConfigService,
                    AlarmHandlerChain alarmHandlerChain) {
        this.alarmId = alarmId;
        this.alarmConfigService = alarmConfigService;
        this.alarmHandlerChain = alarmHandlerChain;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        Long start = System.currentTimeMillis();
        AlarmConfig config = null;
        AlarmContext context = null;
        try {
            config = alarmConfigService.getAlarmConfig(alarmId);
            context = alarmHandlerChain.handle(config);
            logger.info("alarm job running cost {}ms: config={},result={}",
                    (System.currentTimeMillis() - start), JSON.toJSONString(config), JSON.toJSONString(context));
        } catch (Exception e) {
            logger.error("alarm job running exception: config={},result={}",
                    (System.currentTimeMillis() - start), JSON.toJSONString(config), JSON.toJSONString(context), e);
        }
    }
}
