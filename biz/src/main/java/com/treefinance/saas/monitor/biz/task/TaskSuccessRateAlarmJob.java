package com.treefinance.saas.monitor.biz.task;

import com.alibaba.fastjson.JSONObject;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.service.newmonitor.task.TaskSuccessRateAlarmService;
import com.treefinance.saas.monitor.common.domain.dto.TaskSuccessRateAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by haojiahong on 2017/11/24.
 */
public class TaskSuccessRateAlarmJob implements SimpleJob {

    private static final Logger logger = LoggerFactory.getLogger(TaskSuccessRateAlarmJob.class);

    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private TaskSuccessRateAlarmService taskSuccessRateAlarmService;

    @Override
    public void execute(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        Date jobTime = new Date();//定时任务执行时间
        logger.info("任务成功率预警,定时任务执行jobTime={}", MonitorDateUtils.format(jobTime));
        try {
            String configStr = diamondConfig.getTaskSuccessRateAlarmConfig();
            List<TaskSuccessRateAlarmConfigDTO> configList = JSONObject.parseArray(configStr, TaskSuccessRateAlarmConfigDTO.class);
            Map<String, TaskSuccessRateAlarmConfigDTO> configMap = configList.stream().collect(Collectors.toMap(TaskSuccessRateAlarmConfigDTO::getType, config -> config));
            if (MapUtils.isEmpty(configMap)) {
                logger.info("任务成功率预警,定时任务执行jobTime={}任务成功率预警未设置", MonitorDateUtils.format(jobTime));
                return;
            }
            for (EBizType bizType : EBizType.values()) {
                TaskSuccessRateAlarmConfigDTO config = configMap.get(bizType.getText());
                if (config == null) {
                    continue;
                }
                taskSuccessRateAlarmService.alarm(bizType, config, jobTime);
            }

        } catch (Exception e) {
            logger.error("任务成功率预警,定时任务执行jobTime={}异常", MonitorDateUtils.format(jobTime), e);
        } finally {
            logger.info("任务成功率预警,定时任务执行jobTime={}完成,耗时{}ms", MonitorDateUtils.format(jobTime), System.currentTimeMillis() - start);
        }

    }
}
