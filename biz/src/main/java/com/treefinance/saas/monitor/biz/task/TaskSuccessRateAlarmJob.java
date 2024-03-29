package com.treefinance.saas.monitor.biz.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.service.newmonitor.task.TaskSuccessRateAlarmService;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.TaskSuccessRateAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.util.SystemUtils;
import com.treefinance.toolkit.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author haojiahong
 * @date 2017/11/24
 */
public class TaskSuccessRateAlarmJob implements SimpleJob {

    private static final Logger logger = LoggerFactory.getLogger(TaskSuccessRateAlarmJob.class);

    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private TaskSuccessRateAlarmService taskSuccessRateAlarmService;

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
        //定时任务执行时间
        Date jobTime = new Date();
        logger.info("任务成功率预警,定时任务执行jobTime={}", DateUtils.format(jobTime));
        try {
            String configStr = diamondConfig.getTaskSuccessRateAlarmConfig();
            List<TaskSuccessRateAlarmConfigDTO> configList = JSONObject.parseArray(configStr, TaskSuccessRateAlarmConfigDTO.class);
            //根据任务类型来分配的
            Map<String, List<TaskSuccessRateAlarmConfigDTO>> configMap = configList.stream().collect(Collectors.groupingBy(TaskSuccessRateAlarmConfigDTO::getType));
            if (MapUtils.isEmpty(configMap)) {
                logger.info("任务成功率预警,定时任务执行jobTime={}任务成功率预警未设置", DateUtils.format(jobTime));
                return;
            }
            for (EBizType bizType : EBizType.values()) {
                List<TaskSuccessRateAlarmConfigDTO> configDTOList = configMap.get(bizType.getText());
                logger.info("bizType：{}，config：{}", bizType.getDesc(), configDTOList);
                if (CollectionUtils.isEmpty(configDTOList)) {
                    continue;
                }
                for (TaskSuccessRateAlarmConfigDTO config : configDTOList) {
                    if (!AlarmConstants.SWITCH_ON.equals(config.getAlarmSwitch())) {
                        logger.info("任务成功率总开关关闭。。{}不预警", jobTime);
                        continue;
                    }

                    logger.info("任务成功率预警,定时任务执行jobTime={}任务成功率预警执行config={}", DateUtils.format(jobTime), JSON.toJSONString(config));
                    taskSuccessRateAlarmService.alarm(bizType, config, jobTime);
                }
            }
        } catch (Exception e) {
            logger.error("任务成功率预警,定时任务执行jobTime={}异常", DateUtils.format(jobTime), e);
        } finally {
            logger.info("任务成功率预警,定时任务执行jobTime={}完成,耗时{}ms", DateUtils.format(jobTime), System.currentTimeMillis() - start);
        }

    }
}
