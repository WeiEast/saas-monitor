package com.treefinance.saas.monitor.biz.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.service.OperatorMonitorAllAlarmService;
import com.treefinance.saas.monitor.biz.service.OperatorMonitorGroupAlarmService;
import com.treefinance.saas.monitor.common.enumeration.ETaskOperatorStatType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/6.
 */
public class OperatorMonitorByTaskAlarmJob implements SimpleJob {

    private static final Logger logger = LoggerFactory.getLogger(OperatorMonitorByTaskAlarmJob.class);

    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private OperatorMonitorGroupAlarmService operatorMonitorGroupAlarmService;
    @Autowired
    private OperatorMonitorAllAlarmService operatorMonitorAllAlarmService;


    @Override
    public void execute(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        //定时任务执行时间
        Date jobTime = new Date();
        //如果定时任务是11:00执行,则要统计的数据应该是10:00
        Date dataTime = DateUtils.addMinutes(jobTime, -diamondConfig.getOperatorMonitorIntervalMinutes());
        logger.info("运营商监控,按任务预警定时任务执行时间jobTime={},要统计的数据时刻dataTime={}", MonitorDateUtils.format(jobTime), MonitorDateUtils.format(dataTime));
        try {
            //区分运营商的预警
            operatorMonitorGroupAlarmService.alarm(jobTime, dataTime, ETaskOperatorStatType.TASK);
            //所有运营商的预警
            operatorMonitorAllAlarmService.alarm(jobTime, dataTime, ETaskOperatorStatType.TASK);
        } catch (Exception e) {
            logger.error("运营商监控,按任务预警定时任务执行jobTime={}异常", MonitorDateUtils.format(jobTime), e);
        } finally {
            logger.info("运营商监控,按任务预警定时任务执行jobTime={}完成,耗时{}ms", MonitorDateUtils.format(jobTime), System.currentTimeMillis() - start);
        }
    }


}
