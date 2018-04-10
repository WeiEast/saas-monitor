package com.treefinance.saas.monitor.biz.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.service.OperatorMonitorGroupAlarmService;
import com.treefinance.saas.monitor.common.domain.dto.OperatorMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.common.utils.MonitorUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Created by haojiahong on 2017/11/6.
 */
public class OperatorMonitorAlarmJob implements SimpleJob {

    private static final Logger logger = LoggerFactory.getLogger(OperatorMonitorAlarmJob.class);

    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private OperatorMonitorGroupAlarmService operatorMonitorGroupAlarmService;


    @Override
    public void execute(ShardingContext shardingContext) {
        if (MonitorUtils.isPreProductContext()) {
            logger.info("定时任务,预发布环境暂不执行");
            return;
        }
        long start = System.currentTimeMillis();
        //定时任务执行时间,每5分钟执行一次
        Date jobTime = new Date();
        logger.info("运营商监控,预警定时任务执行时间jobTime={}", MonitorDateUtils.format(jobTime));
        try {
            String configStr = diamondConfig.getOperatorMonitorAlarmConfig();
            List<OperatorMonitorAlarmConfigDTO> configList = JSONObject.parseArray(configStr, OperatorMonitorAlarmConfigDTO.class);
            for (OperatorMonitorAlarmConfigDTO configDTO : configList) {
                logger.info("运营商监控,预警定时任务执行时间jobTime={},config={}", MonitorDateUtils.format(jobTime), JSON.toJSONString(configDTO));
                if (!StringUtils.equalsIgnoreCase(configDTO.getAlarmSwitch(), "on")) {
                    continue;
                }
                operatorMonitorGroupAlarmService.alarm(jobTime, configDTO);
            }
        } catch (Exception e) {
            logger.error("运营商监控,预警定时任务执行jobTime={}异常", MonitorDateUtils.format(jobTime), e);
        } finally {
            logger.info("运营商监控,预警定时任务执行jobTime={}完成,耗时{}ms", MonitorDateUtils.format(jobTime), System.currentTimeMillis() - start);
        }
    }


}
