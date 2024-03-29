package com.treefinance.saas.monitor.biz.task;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.config.EmailAlarmConfig;
import com.treefinance.saas.monitor.biz.service.MonitorAlarmService;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.EmailMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;
import com.treefinance.saas.monitor.util.SystemUtils;
import com.treefinance.toolkit.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

/**
 * @Author: chengtong
 * @Date: 18/3/9 16:19
 */
public class EmailMonitorAlarmJob implements SimpleJob {

    private static final Logger logger = LoggerFactory.getLogger(EmailMonitorAlarmJob.class);

    @Autowired
    private EmailAlarmConfig config;
    @Autowired
    private DiamondConfig diamondConfig;

    @Resource
    @Qualifier("emailAlarmMonitorService")
    private MonitorAlarmService emailMonitorAlarmService;

    @Override
    public void execute(ShardingContext shardingContext) {
        if (!diamondConfig.isOldAlarmAllSwitchOn()) {
            return;
        }
        if (SystemUtils.isPreProductContext()) {
            logger.info("定时任务,预发布环境暂不执行");
            return;
        }
        long start = System.currentTimeMillis();
        Date now = new Date();
        List<EmailMonitorAlarmConfigDTO> configDTOList = JSON.parseArray(config.getEmailAlarmConfig(),
                EmailMonitorAlarmConfigDTO
                        .class);
        //这里只是区分了 任务数/人数

        try {
            for (EmailMonitorAlarmConfigDTO configDTO : configDTOList) {

                if (!AlarmConstants.SWITCH_ON.equals(configDTO.getAlarmSwitch())) {
                    logger.info(configDTO.getAlarmTypeDesc() + " 邮箱预警开关关闭。。。");
                    continue;
                }

                ETaskStatDataType type = ETaskStatDataType.getByValue(configDTO.getAlarmType());
                emailMonitorAlarmService.alarm(now, configDTO, type, EAlarmType.email_alarm);
            }
        } catch (Exception e) {
            logger.error("邮箱监控,预警定时任务执行jobTime={}异常", DateUtils.format(now), e);
        } finally {
            logger.info("邮箱监控,预警定时任务执行jobTime={}完成,耗时{}ms", DateUtils.format(now), System.currentTimeMillis() -
                    start);
        }

    }
}
