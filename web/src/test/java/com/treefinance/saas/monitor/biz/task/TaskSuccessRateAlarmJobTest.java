package com.treefinance.saas.monitor.biz.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.service.newmonitor.task.impl.TaskSuccessRateAlarmServiceImpl;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.TaskSuccessRateAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.util.SystemUtils;
import com.treefinance.toolkit.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author chengtong
 * @date 18/5/11 14:36
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SaasMonitorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TaskSuccessRateAlarmJobTest {

    private static final Logger logger = LoggerFactory.getLogger(TaskSuccessRateAlarmJob.class);

    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private TaskSuccessRateAlarmServiceImpl taskSuccessRateAlarmService;

    @Test
    public void execute() {
        if (SystemUtils.isPreProductContext()) {
            logger.info("定时任务,预发布环境暂不执行");
            return;
        }
        long start = System.currentTimeMillis();
        //定时任务执行时间
        String dateStr = "2018-07-02 18:27:00";

        Date jobTime = DateUtils.parse(dateStr);
        logger.info("任务成功率预警,定时任务执行jobTime={}", DateUtils.format(jobTime));
        try {
//            String configStr = diamondConfig.getTaskSuccessRateAlarmConfig();
            String configStr = "[{\"alarmSwitch\":\"on\",\"intervalMins\":5,\"levelConfig\":[{\"channels\":[\"ivr\",\"email\",\"wechat\"],\"level\":\"error\"},{\"channels\":[\"sms\",\"email\",\"wechat\"],\"level\":\"warning\"},{\"channels\":[\"wechat\"],\"level\":\"info\"}],\"saasEnv\":0,\"saasEnvDesc\":\"所有环境\",\"succesThreshold\":40,\"taskTimeoutSecs\":600,\"timeConfig\":[{\"endTime\":\"23:59:59\",\"inTime\":false,\"startTime\":\"19:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"thresholdError\":70,\"thresholdInfo\":90,\"thresholdWarning\":80},{\"endTime\":\"06:00:00\",\"intervals\":10,\"inTime\":false,\"startTime\":\"00:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"off\",\"email\":\"on\"},\"thresholdError\":50,\"thresholdInfo\":80,\"thresholdWarning\":70},{\"endTime\":\"19:00:00\",\"inTime\":true,\"startTime\":\"06:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"thresholdError\":70,\"thresholdInfo\":90,\"thresholdWarning\":80}],\"times\":3,\"type\":\"OPERATOR\"},{\"alarmSwitch\":\"on\",\"intervalMins\":5,\"levelConfig\":[{\"channels\":[\"ivr\",\"email\",\"wechat\"],\"level\":\"error\"},{\"channels\":[\"sms\",\"email\",\"wechat\"],\"level\":\"warning\"},{\"channels\":[\"wechat\"],\"level\":\"info\"}],\"saasEnv\":0,\"saasEnvDesc\":\"所有环境\",\"succesThreshold\":40,\"taskTimeoutSecs\":600,\"timeConfig\":[{\"endTime\":\"23:59:59\",\"inTime\":false,\"startTime\":\"19:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"thresholdError\":65,\"thresholdInfo\":90,\"thresholdWarning\":75},{\"endTime\":\"06:00:00\",\"intervals\":10,\"inTime\":false,\"startTime\":\"00:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"off\",\"email\":\"on\"},\"thresholdError\":50,\"thresholdInfo\":80,\"thresholdWarning\":70},{\"endTime\":\"19:00:00\",\"inTime\":true,\"startTime\":\"06:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"thresholdError\":65,\"thresholdInfo\":90,\"thresholdWarning\":75}],\"times\":3,\"type\":\"ECOMMERCE\"}]";
            List<TaskSuccessRateAlarmConfigDTO> configList = JSONObject.parseArray(configStr, TaskSuccessRateAlarmConfigDTO.class);
            Map<String, List<TaskSuccessRateAlarmConfigDTO>> configMap = configList.stream().collect(Collectors.groupingBy(TaskSuccessRateAlarmConfigDTO::getType));
            if (MapUtils.isEmpty(configMap)) {
                logger.info("任务成功率预警,定时任务执行jobTime={}任务成功率预警未设置", DateUtils.format(jobTime));
                return;
            }
            for (EBizType bizType : EBizType.values()) {
                List<TaskSuccessRateAlarmConfigDTO> configDTOList = configMap.get(bizType.getText());
                logger.info("bizType：{}，config：{}",bizType.getDesc(),configDTOList);
                if (CollectionUtils.isEmpty(configDTOList)) {
                    continue;
                }
                for (TaskSuccessRateAlarmConfigDTO config : configDTOList) {
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