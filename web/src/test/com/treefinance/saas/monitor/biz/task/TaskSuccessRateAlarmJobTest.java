package com.treefinance.saas.monitor.biz.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.datatrees.common.util.DateUtils;
import com.treefinance.saas.monitor.app.SaasMonitorApplication;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.service.newmonitor.task.impl.TaskSuccessRateAlarmServiceImpl;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.TaskSuccessRateAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.common.utils.MonitorUtils;
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
        if (MonitorUtils.isPreProductContext()) {
            logger.info("定时任务,预发布环境暂不执行");
            return;
        }
        long start = System.currentTimeMillis();
        //定时任务执行时间
        String dateStr = "2018-05-13 23:40:00";

        Date jobTime = DateUtils.parseDate(dateStr,"yyyy-MM-dd hh:mm:ss");
        logger.info("任务成功率预警,定时任务执行jobTime={}", MonitorDateUtils.format(jobTime));
        try {
//            String configStr = diamondConfig.getTaskSuccessRateAlarmConfig();
            String configStr = "[{\"alarmSwitch\":\"on\",\"intervalMins\":3,\"levelConfig\":[{\"channels\":[\"ivr\",\"email\",\"wechat\"],\"level\":\"error\"},{\"channels\":[\"sms\",\"email\",\"wechat\"],\"level\":\"warning\"},{\"channels\":[\"email\",\"wechat\"],\"level\":\"info\"}],\"saasEnv\":1,\"saasEnvDesc\":\"生产环境\",\"succesThreshold\":40,\"taskTimeoutSecs\":600,\"timeConfig\":[{\"endTime\":\"23:59:59\",\"inTime\":false,\"startTime\":\"19:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"thresholdError\":70,\"thresholdInfo\":90,\"thresholdWarning\":80},{\"endTime\":\"06:00:00\",\"inTime\":false,\"startTime\":\"00:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"thresholdError\":70,\"thresholdInfo\":90,\"thresholdWarning\":80},{\"endTime\":\"19:00:00\",\"inTime\":true,\"startTime\":\"06:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"off\",\"email\":\"on\"},\"thresholdError\":70,\"thresholdInfo\":90,\"thresholdWarning\":80}],\"times\":3,\"type\":\"OPERATOR\"},{\"alarmSwitch\":\"on\",\"intervalMins\":3,\"levelConfig\":[{\"channels\":[\"ivr\",\"email\",\"wechat\"],\"level\":\"error\"},{\"channels\":[\"sms\",\"email\",\"wechat\"],\"level\":\"warning\"},{\"channels\":[\"email\",\"wechat\"],\"level\":\"info\"}],\"saasEnv\":2,\"saasEnvDesc\":\"预发布环境\",\"succesThreshold\":40,\"taskTimeoutSecs\":600,\"timeConfig\":[{\"endTime\":\"23:59:59\",\"inTime\":false,\"startTime\":\"19:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"thresholdError\":70,\"thresholdInfo\":90,\"thresholdWarning\":80},{\"endTime\":\"06:00:00\",\"inTime\":false,\"startTime\":\"00:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"thresholdError\":70,\"thresholdInfo\":90,\"thresholdWarning\":80},{\"endTime\":\"19:00:00\",\"inTime\":true,\"startTime\":\"06:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"off\",\"email\":\"on\"},\"thresholdError\":70,\"thresholdInfo\":90,\"thresholdWarning\":80}],\"times\":3,\"type\":\"OPERATOR\"},{\"alarmSwitch\":\"on\",\"intervalMins\":3,\"levelConfig\":[{\"channels\":[\"ivr\",\"email\",\"wechat\"],\"level\":\"error\"},{\"channels\":[\"sms\",\"email\",\"wechat\"],\"level\":\"warning\"},{\"channels\":[\"email\",\"wechat\"],\"level\":\"info\"}],\"saasEnv\":0,\"saasEnvDesc\":\"所有环境\",\"succesThreshold\":40,\"taskTimeoutSecs\":600,\"timeConfig\":[{\"endTime\":\"23:59:59\",\"inTime\":false,\"startTime\":\"19:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"thresholdError\":70,\"thresholdInfo\":90,\"thresholdWarning\":80},{\"endTime\":\"06:00:00\",\"inTime\":false,\"startTime\":\"00:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"on\",\"email\":\"on\"},\"thresholdError\":70,\"thresholdInfo\":90,\"thresholdWarning\":80},{\"endTime\":\"19:00:00\",\"inTime\":true,\"startTime\":\"06:00:00\",\"switches\":{\"sms\":\"on\",\"wechat\":\"on\",\"ivr\":\"off\",\"email\":\"on\"},\"thresholdError\":70,\"thresholdInfo\":90,\"thresholdWarning\":80}],\"times\":3,\"type\":\"OPERATOR\"}]\n";
            List<TaskSuccessRateAlarmConfigDTO> configList = JSONObject.parseArray(configStr, TaskSuccessRateAlarmConfigDTO.class);
            Map<String, List<TaskSuccessRateAlarmConfigDTO>> configMap = configList.stream().collect(Collectors.groupingBy(TaskSuccessRateAlarmConfigDTO::getType));
            if (MapUtils.isEmpty(configMap)) {
                logger.info("任务成功率预警,定时任务执行jobTime={}任务成功率预警未设置", MonitorDateUtils.format(jobTime));
                return;
            }
            for (EBizType bizType : EBizType.values()) {
                List<TaskSuccessRateAlarmConfigDTO> configDTOList = configMap.get(bizType.getText());
                logger.info("bizType：{}，config：{}",bizType.getDesc(),configDTOList);
                if (CollectionUtils.isEmpty(configDTOList)) {
                    continue;
                }
                for (TaskSuccessRateAlarmConfigDTO config : configDTOList) {
                    logger.info("任务成功率预警,定时任务执行jobTime={}任务成功率预警执行config={}", MonitorDateUtils.format(jobTime), JSON.toJSONString(config));
                    taskSuccessRateAlarmService.alarm(bizType, config, jobTime);
                }
            }
        } catch (Exception e) {
            logger.error("任务成功率预警,定时任务执行jobTime={}异常", MonitorDateUtils.format(jobTime), e);
        } finally {
            logger.info("任务成功率预警,定时任务执行jobTime={}完成,耗时{}ms", MonitorDateUtils.format(jobTime), System.currentTimeMillis() - start);
        }

    }
}