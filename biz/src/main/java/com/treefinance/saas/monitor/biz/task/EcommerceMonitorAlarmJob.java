package com.treefinance.saas.monitor.biz.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.service.EcommerceMonitorAllAlarmService;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.domain.dto.EcommerceMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.common.utils.MonitorUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Created by haojiahong on 2018/1/17.
 */
public class EcommerceMonitorAlarmJob implements SimpleJob {

    private static final Logger logger = LoggerFactory.getLogger(EcommerceMonitorAlarmJob.class);

    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private EcommerceMonitorAllAlarmService ecommerceMonitorAllAlarmService;

    @Override
    public void execute(ShardingContext shardingContext) {
        if (!diamondConfig.isOldAlarmAllSwitchOn()) {
            return;
        }
        if (MonitorUtils.isPreProductContext()) {
            logger.info("定时任务,预发布环境暂不执行");
            return;
        }
        long start = System.currentTimeMillis();
        Date jobTime = new Date();
        logger.info("电商预警,预警定时任务执行时间jobTime={}", MonitorDateUtils.format(jobTime));
        try {
            String configStr = diamondConfig.getEcommerceMonitorAlarmConfig();
            List<EcommerceMonitorAlarmConfigDTO> configList = JSONObject.parseArray(configStr, EcommerceMonitorAlarmConfigDTO.class);
            for (EcommerceMonitorAlarmConfigDTO configDTO : configList) {
                logger.info("电商预警,预警定时任务执行时间jobTime={},config={}", MonitorDateUtils.format(jobTime), JSON.toJSONString(configDTO));
                if (!StringUtils.equalsIgnoreCase(configDTO.getAlarmSwitch(), AlarmConstants.SWITCH_ON)) {
                    continue;
                }
                if (configDTO.getAlarmType() == 1) {
                    //总电商按人统计的预警
                    ecommerceMonitorAllAlarmService.alarm(jobTime, configDTO, ETaskStatDataType.USER);
                }
                if (configDTO.getAlarmType() == 2) {
                    //总电商按人统计的预警
                    ecommerceMonitorAllAlarmService.alarm(jobTime, configDTO, ETaskStatDataType.TASK);
                }
            }
        } catch (Exception e) {
            logger.error("电商预警,预警定时任务执行jobTime={}异常", MonitorDateUtils.format(jobTime), e);
        } finally {
            logger.info("电商预警,预警定时任务执行jobTime={}完成,耗时{}ms", MonitorDateUtils.format(jobTime), System.currentTimeMillis() - start);
        }
    }
}
