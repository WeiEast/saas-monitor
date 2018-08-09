package com.treefinance.saas.monitor.biz.alarm.service;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.alarm.job.AlarmJob;
import com.treefinance.saas.monitor.biz.alarm.service.handler.AlarmHandlerChain;
import com.treefinance.saas.monitor.biz.autostat.elasticjob.ElasticSimpleJobService;
import com.treefinance.saas.monitor.common.domain.Constants;
import com.treefinance.saas.monitor.common.enumeration.ESwitch;
import com.treefinance.saas.monitor.dao.entity.AsAlarm;
import com.treefinance.saas.monitor.dao.entity.AsAlarmCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yh-treefinance on 2018/7/31.
 */
@Component
public class AlaramJobService implements SimpleJob, InitializingBean {
    /**
     * logger
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * elastic-job
     */
    @Autowired
    private ElasticSimpleJobService elasticSimpleJobService;

    /**
     * 预警配置查询
     */
    @Autowired
    private AlarmConfigService alarmConfigService;
    /**
     * 预警处理链
     */
    @Autowired
    private AlarmHandlerChain alarmHandlerChain;

    /**
     * 创建job
     *
     * @param alarmId
     */
    public void startJob(Long alarmId) {
        String autoJobName = getJobName(alarmId);
        // 预警主表
        AsAlarm alarm = alarmConfigService.alarmMapper.selectByPrimaryKey(alarmId);
        if (ESwitch.isOff(alarm.getAlarmSwitch())) {
            logger.info("start job failed : job is off  alarm={}", JSON.toJSONString(alarm));
            return;
        }
        JobCoreConfiguration statCalculateJobConf = JobCoreConfiguration
                .newBuilder(autoJobName, alarm.getRunInterval(), 1)
                .failover(true)
                .description(alarm.getName())
                .build();
        elasticSimpleJobService.createJob(new AlarmJob(alarmId, alarmConfigService, alarmHandlerChain), statCalculateJobConf);
        logger.info("start job success : alarm={}", JSON.toJSONString(alarm));
    }

    private String getJobName(Long alarmId) {
        return "alarm-job-" + alarmId;
    }

    /**
     * 关闭job
     *
     * @param alarmId
     */
    public void stopJob(Long alarmId) {
        String autoJobName = getJobName(alarmId);
        // 预警主表
        AsAlarm alarm = alarmConfigService.alarmMapper.selectByPrimaryKey(alarmId);
        if (ESwitch.isOn(alarm.getAlarmSwitch())) {
            logger.info("stop job failed : job is on  alarm={}", JSON.toJSONString(alarm));
            return;
        }
        elasticSimpleJobService.removeJob(autoJobName);
        logger.info("stop job success : alarm={}", JSON.toJSONString(alarm));
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        Byte currentEnv = Byte.valueOf(Constants.SAAS_ENV_VALUE);
        List<Byte> saasEnvs = Lists.newArrayList(currentEnv, Byte.valueOf("0"));
        // start job
        AsAlarmCriteria criteria = new AsAlarmCriteria();
        criteria.createCriteria().andAlarmSwitchEqualTo(ESwitch.ON.getCode());
        List<AsAlarm> startAlarms = alarmConfigService.alarmMapper.selectByExample(criteria);
        startAlarms.stream()
                .filter(asAlarm -> asAlarm.getRunEnv() == null || saasEnvs.contains(asAlarm.getRunEnv()))
                .map(AsAlarm::getId).forEach(alarmId -> startJob(alarmId));

        // stop job
        criteria = new AsAlarmCriteria();
        criteria.createCriteria().andAlarmSwitchEqualTo(ESwitch.OFF.getCode());
        List<AsAlarm> stopAlarms = alarmConfigService.alarmMapper.selectByExample(criteria);
        stopAlarms.stream()
                .filter(asAlarm -> asAlarm.getRunEnv() == null || saasEnvs.contains(asAlarm.getRunEnv()))
                .map(AsAlarm::getId).forEach(alarmId -> stopJob(alarmId));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String autoJobName = "flush-alarm-job";
        JobCoreConfiguration statCalculateJobConf = JobCoreConfiguration
                .newBuilder(autoJobName, "0 0/5 * * * ? ", 1)
                .failover(true)
                .description("预警任务刷新")
                .build();
        elasticSimpleJobService.createJob(this, statCalculateJobConf);
        elasticSimpleJobService.triggerJob(autoJobName);
        logger.info("auto load alarm job running...");
    }
}
