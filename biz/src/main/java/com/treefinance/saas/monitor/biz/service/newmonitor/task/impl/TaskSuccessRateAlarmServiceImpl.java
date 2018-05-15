package com.treefinance.saas.monitor.biz.service.newmonitor.task.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.TaskMonitorPerMinKeyHelper;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.biz.service.IvrNotifyService;
import com.treefinance.saas.monitor.biz.service.SmsNotifyService;
import com.treefinance.saas.monitor.biz.service.newmonitor.task.TaskSuccessRateAlarmService;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.domain.dto.SaasStatAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.TaskSuccRateAlarmTimeListDTO;
import com.treefinance.saas.monitor.common.domain.dto.TaskSuccRateCompareDTO;
import com.treefinance.saas.monitor.common.domain.dto.TaskSuccessRateAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.EAlarmChannel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.MerchantStatAccess;
import com.treefinance.saas.monitor.dao.entity.MerchantStatAccessCriteria;
import com.treefinance.saas.monitor.dao.entity.SaasStatAccess;
import com.treefinance.saas.monitor.dao.entity.SaasStatAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.MerchantStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.SaasStatAccessMapper;
import com.treefinance.saas.monitor.exception.BizException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by haojiahong on 2017/11/24.
 */
@Service
public class TaskSuccessRateAlarmServiceImpl implements TaskSuccessRateAlarmService {
    private static final Logger logger = LoggerFactory.getLogger(TaskSuccessRateAlarmService.class);

    @Autowired
    private SaasStatAccessMapper saasStatAccessMapper;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private AlarmMessageProducer alarmMessageProducer;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private MerchantStatAccessMapper merchantStatAccessMapper;
    @Autowired
    private IvrNotifyService ivrNotifyService;
    @Autowired
    private SmsNotifyService smsNotifyService;


    private static final BigDecimal HUNDRED = new BigDecimal(100);

    /**
     * 配置中保存了当前配置的环境信息
     */
    @Override
    public void alarm(EBizType bizType, TaskSuccessRateAlarmConfigDTO config, Date jobTime) {
        Integer intervalMins = config.getIntervalMins();
        //由于任务执行需要时间,保证预警的精确,预警统计向前一段时间(各业务任务的超时时间),此时此段时间的任务可以保证都已统计完毕.
        //好处:预警时间即使每隔1分钟预警,依然可以保证预警的准确.坏处:收到预警消息时间向后延迟了相应时间.
        //如:jobTime=14:11,但是运营商超时时间为600s,则statTime=14:01
        Date statTime = DateUtils.addSeconds(jobTime, -config.getTaskTimeoutSecs());
        //取得预警原点时间,如:statTime=14:01分,10分钟间隔统计一次,则beginTime为14:00.统计的数据间隔[13:30-13:40;13:40-13:50;13:50-14:00]
        Date beginTime = TaskMonitorPerMinKeyHelper.getRedisStatDateTime(statTime, intervalMins);

        String alarmTimeKey = TaskMonitorPerMinKeyHelper.strKeyOfAlarmTimeLog(beginTime, bizType,config.getSaasEnv());
        if (stringRedisTemplate.hasKey(alarmTimeKey)) {
            logger.info("任务成功率预警已预警,不再预警,beginTime={},bizType={}", MonitorDateUtils.format(beginTime), JSON.toJSONString(bizType));
            return;
        }
        stringRedisTemplate.opsForValue().set(alarmTimeKey, "1");
        stringRedisTemplate.expire(alarmTimeKey, 2, TimeUnit.HOURS);

        List<SaasStatAccessDTO> list = getSourceDataList(beginTime, config, intervalMins, bizType);
        logger.info("任务成功率预警,定时任务执行jobTime={},需要预警的数据list={},beginTime={},bizType={},config={}",
                MonitorDateUtils.format(jobTime), JSON.toJSONString(list), MonitorDateUtils.format(beginTime), JSON.toJSONString(bizType), JSON.toJSONString(config));
        //校验数据是不是空的，或者是 不是三个五分钟都发出报警
        if (list.isEmpty() || list.size() != config.getTimes()) {
            return;
        }

        TaskSuccRateCompareDTO compareDTO = getPastData(beginTime, config, intervalMins, bizType);

        if (compareDTO.getTotalCount() == null || compareDTO.getTotalCount().equals(0)) {
            logger.info("过去7天内没有找到数据，不预警");
            return;
        }


        EAlarmLevel alarmLevel = isAlarmAndDetermineLevel(list, config, compareDTO);
        if (alarmLevel == null) {
            logger.info("任务成功率预警,定时任务执行jobTime={},判断所得数据不需要预警,list={},config={},compare={}",
                    MonitorDateUtils.format(jobTime), JSON.toJSONString(list), JSON.toJSONString(config), JSON
                            .toJSONString(compareDTO));
            return;
        }

        Map<String, String> switches = config.getSwitches();

        if (AlarmConstants.SWITCH_ON.equals(switches.get(EAlarmChannel.EMAIL.getValue()))) {
            sendMailAlarm(list, bizType, alarmLevel, compareDTO);
        }
        if (AlarmConstants.SWITCH_ON.equals(switches.get(EAlarmChannel.WECHAT.getValue()))) {
            sendWechatAlarm(list, bizType, alarmLevel, compareDTO);
        }
        if (AlarmConstants.SWITCH_ON.equals(switches.get(EAlarmChannel.SMS.getValue()))) {
            sendSmsAlarm(list, bizType, alarmLevel, compareDTO);
        }

        // 增加ivr服务通知
        if (EBizType.OPERATOR == bizType) {
            ivrNotifyService.notifyIvr(EAlarmLevel.error, EAlarmType.conversion_rate_low, "运营商转化率低于阀值");
        }
    }

    private TaskSuccRateCompareDTO getPastData(Date beginTime, TaskSuccessRateAlarmConfigDTO config,
                                               Integer intervalMins, EBizType bizType) {
        List<Date> pastDays = MonitorDateUtils.getPreviousOClockTime(beginTime, 7);

        TaskSuccRateCompareDTO compareDTO = new TaskSuccRateCompareDTO();
        for (Date time : pastDays) {
            List<SaasStatAccessDTO> list = getSourceDataList(time, config, intervalMins, bizType);

            if (list == null || list.isEmpty()) {
                logger.info("{}的数据为空", time);
                continue;
            }

            for (SaasStatAccessDTO saasStatAccessDTO : list) {
                compareDTO.add(saasStatAccessDTO);
            }
        }

        compareDTO.setEvn(config.getSaasEnvDesc());

        return compareDTO;

    }


    private EAlarmLevel isAlarmAndDetermineLevel(List<SaasStatAccessDTO> list, TaskSuccessRateAlarmConfigDTO config,
                                                 TaskSuccRateCompareDTO compareDTO) {
        List<TaskSuccRateAlarmTimeListDTO> timeConfigs = config.getTimeConfig();
        BigDecimal errorThresholdRate = null;
        BigDecimal warnThresholdRate = null;
        BigDecimal infoThresholdRate = null;
        for (TaskSuccRateAlarmTimeListDTO timeConfig : timeConfigs) {
            if (timeConfig.isInTime()) {
                errorThresholdRate = timeConfig.getThresholdError();
                warnThresholdRate = timeConfig.getThresholdWarning();
                infoThresholdRate = timeConfig.getThresholdInfo();
            }
        }

        if (errorThresholdRate == null || warnThresholdRate == null || infoThresholdRate == null
                ) {
            throw new BizException("任务成功率预警，当前时间没有设定错误、警告级别阈值");
        }

        BigDecimal operand = compareDTO.getSuccessRate().divide(HUNDRED, 2, RoundingMode.HALF_UP);

        BigDecimal errorThreshold = errorThresholdRate.multiply(operand);
        BigDecimal warnThreshold = warnThresholdRate.multiply(operand);
        BigDecimal infoThreshold = infoThresholdRate.multiply(operand);

        int successCount = 0, total = 0;

        for (SaasStatAccessDTO saasStatAccessDTO : list) {
            successCount += saasStatAccessDTO.getSuccessCount();
            total += saasStatAccessDTO.getTotalCount();
        }

        BigDecimal averSuccRate = new BigDecimal(successCount).multiply(HUNDRED).divide(new BigDecimal(total), 2,
                RoundingMode
                        .HALF_UP);

        if (averSuccRate.compareTo(errorThreshold) <= 0) {
            compareDTO.setThreshold(errorThreshold);
            compareDTO.setThresholdDecs(errorThresholdRate.divide(HUNDRED, 2, RoundingMode.HALF_UP).toPlainString() + "*" + compareDTO
                    .getSuccessRate()
                    .toPlainString());
            return EAlarmLevel.error;
        } else if (averSuccRate.compareTo(warnThreshold) <= 0) {
            compareDTO.setThreshold(warnThreshold);
            compareDTO.setThresholdDecs(warnThresholdRate.divide(HUNDRED, 2, RoundingMode.HALF_UP).toPlainString() + "*" + compareDTO.getSuccessRate().toPlainString());
            return EAlarmLevel.warning;
        } else if (averSuccRate.compareTo(infoThreshold) <= 0) {
            compareDTO.setThreshold(infoThreshold);
            compareDTO.setThresholdDecs(infoThresholdRate.divide(HUNDRED, 2, RoundingMode.HALF_UP).toPlainString() + "*" + compareDTO.getSuccessRate().toPlainString());
            return EAlarmLevel.info;
        }

        return null;
    }

    private List<SaasStatAccessDTO> getSourceDataList(Date beginTime, TaskSuccessRateAlarmConfigDTO config, Integer intervalMins, EBizType bizType) {
        List<SaasStatAccessDTO> list = Lists.newArrayList();
        int times = config.getTimes();
        //左闭右开
        for (int i = 0; i < times; i++) {
            Date startTime = DateUtils.addMinutes(beginTime, -((times - i) * intervalMins));
            Date endTime = DateUtils.addMinutes(beginTime, -((times - i - 1) * intervalMins));

            SaasStatAccessCriteria saasStatAccessCriteria = new SaasStatAccessCriteria();
            saasStatAccessCriteria.createCriteria().andDataTypeEqualTo(bizType.getCode())
                    .andSaasEnvEqualTo(config.getSaasEnv())
                    .andDataTimeGreaterThanOrEqualTo(startTime)
                    .andDataTimeLessThan(endTime);
            logger.info("获取saas_stat_access中环境为{}，时间段{}-{}，dataType为{}的数据", config.getSaasEnvDesc(), MonitorDateUtils
                    .format(startTime), MonitorDateUtils.format(endTime), bizType.getDesc());
            List<SaasStatAccess> dataList = saasStatAccessMapper.selectByExample(saasStatAccessCriteria);

            if (dataList.isEmpty()) {
                logger.info("该时间段内数据为空，没有任务");
                continue;
            }

            int totalCount = 0, successCount = 0, failCount = 0, cancelCount = 0;
            for (SaasStatAccess data : dataList) {
                totalCount = totalCount + data.getTotalCount();
                successCount = successCount + data.getSuccessCount();
                failCount = failCount + data.getFailCount();
                cancelCount = cancelCount + data.getCancelCount();
            }

            SaasStatAccessDTO excludeData = getExcludeAlarmList(bizType, startTime, endTime, config);
            if (excludeData != null) {
                totalCount = totalCount - excludeData.getTotalCount();
                successCount = successCount - excludeData.getSuccessCount();
                failCount = failCount - excludeData.getFailCount();
                cancelCount = cancelCount - excludeData.getCancelCount();
                logger.info("任务成功率预警,排除数据data={}", JSON.toJSONString(excludeData));
            }

            SaasStatAccessDTO saasStatAccessDTO = new SaasStatAccessDTO();
            saasStatAccessDTO.setDataTime(startTime);
            saasStatAccessDTO.setDataType(bizType.getCode());
            saasStatAccessDTO.setTotalCount(totalCount);
            saasStatAccessDTO.setSuccessCount(successCount);
            saasStatAccessDTO.setFailCount(failCount);
            saasStatAccessDTO.setCancelCount(cancelCount);
            saasStatAccessDTO.setConversionRate(calcRate(totalCount, successCount));
            saasStatAccessDTO.setFailRate(calcRate(totalCount, failCount));
            list.add(saasStatAccessDTO);
        }
        return list;
    }

    private SaasStatAccessDTO getExcludeAlarmList(EBizType bizType, Date startTime, Date endTime, TaskSuccessRateAlarmConfigDTO config) {
        List<String> excludeAppIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(diamondConfig.getMonitorAlarmExcludeAppIdsAll())) {
            excludeAppIds = Splitter.on(",").trimResults().splitToList(diamondConfig.getMonitorAlarmExcludeAppIdsAll());
        }
        logger.info("任务成功率预警,排除预警商户appIds={}", JSON.toJSONString(excludeAppIds));
        if (!CollectionUtils.isEmpty(excludeAppIds)) {
            MerchantStatAccessCriteria merchantCriteria = new MerchantStatAccessCriteria();
            merchantCriteria.createCriteria().andAppIdIn(excludeAppIds)
                    .andDataTypeEqualTo(bizType.getCode())
                    .andSaasEnvEqualTo(config.getSaasEnv())
                    .andDataTimeGreaterThanOrEqualTo(startTime)
                    .andDataTimeLessThan(endTime);
            List<MerchantStatAccess> merchantStatAccessList = merchantStatAccessMapper.selectByExample(merchantCriteria);
            int totalCount = 0, successCount = 0, failCount = 0, cancelCount = 0;
            for (MerchantStatAccess data : merchantStatAccessList) {
                totalCount = totalCount + data.getTotalCount();
                successCount = successCount + data.getSuccessCount();
                failCount = failCount + data.getFailCount();
                cancelCount = cancelCount + data.getCancelCount();
            }
            SaasStatAccessDTO excludeData = new SaasStatAccessDTO();
            excludeData.setDataTime(startTime);
            excludeData.setDataType(bizType.getCode());
            excludeData.setTotalCount(totalCount);
            excludeData.setSuccessCount(successCount);
            excludeData.setFailCount(failCount);
            excludeData.setCancelCount(cancelCount);
            return excludeData;
        }
        return null;
    }

    /**
     * 计算比率
     *
     * @param totalCount 总数
     * @param rateCount  比率数
     * @return
     */
    private BigDecimal calcRate(Integer totalCount, Integer rateCount) {
        if (totalCount <= 0) {
            return BigDecimal.valueOf(0, 2);
        }
        BigDecimal rate = BigDecimal.valueOf(rateCount, 2)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalCount, 2), 2, BigDecimal.ROUND_HALF_UP);
        return rate;
    }

    private void sendWechatAlarm(List<SaasStatAccessDTO> list, EBizType type, EAlarmLevel alarmLevel, TaskSuccRateCompareDTO compareDTO) {
        String body = this.generateMessageBody(list, type, EAlarmChannel.WECHAT, alarmLevel, compareDTO);
        alarmMessageProducer.sendWechantAlarm(body);
    }

    private void sendMailAlarm(List<SaasStatAccessDTO> list, EBizType bizType, EAlarmLevel alarmLevel,
                               TaskSuccRateCompareDTO compareDTO) {
        String title = this.generateTitle(bizType);
        String body = this.generateMessageBody(list, bizType, EAlarmChannel.EMAIL, alarmLevel, compareDTO);
        alarmMessageProducer.sendMailAlarm(title, body);
    }

    private String generateTitle(EBizType type) {
        return "saas-" + diamondConfig.getMonitorEnvironment() + "[" + type.getDesc() + "]任务成功率预警";
    }

    private void sendSmsAlarm(List<SaasStatAccessDTO> list, EBizType type, EAlarmLevel alarmLevel, TaskSuccRateCompareDTO compareDTO) {
        String body = this.generateMessageBody(list, type, EAlarmChannel.SMS, alarmLevel, compareDTO);
        smsNotifyService.send(body);
    }

    private String generateMessageBody(List<SaasStatAccessDTO> list, EBizType type, EAlarmChannel sendType,
                                       EAlarmLevel alarmLevel, TaskSuccRateCompareDTO compareDTO) {
        StringBuffer buffer = new StringBuffer();
        if (Objects.equals(EAlarmChannel.SMS, sendType)) {
            //短信的花括号文字是需要备案的
            if (EBizType.OPERATOR.equals(type)) {
                buffer.append(alarmLevel).append(",");
            } else {
                buffer.append(alarmLevel).append(",");
            }
        } else {
            if (EBizType.OPERATOR.equals(type)) {
                buffer.append("【").append(alarmLevel).append("】");
            } else {
                buffer.append("【").append(alarmLevel).append("】");
            }
        }
        buffer.append("您好，").append(generateTitle(type)).append("，监控数据如下，请及时处理：").append("\n");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<String> dataTimeList = Lists.newArrayList();
        List<Integer> totalCountList = Lists.newArrayList();
        List<BigDecimal> successRateList = Lists.newArrayList();
        List<Integer> successCountList = Lists.newArrayList();
        List<BigDecimal> failRateList = Lists.newArrayList();
        List<Integer> failCountList = Lists.newArrayList();
        List<Integer> cancelCountList = Lists.newArrayList();
        list.forEach(access -> {
            dataTimeList.add(fmt.format(access.getDataTime()));
            totalCountList.add(access.getTotalCount());
            successRateList.add(access.getConversionRate());
            successCountList.add(access.getSuccessCount());
            failRateList.add(access.getFailRate());
            failCountList.add(access.getFailCount());
            cancelCountList.add(access.getCancelCount());
        });

        buffer.append(" 环境标志: " + compareDTO.getEvn() + "\n");
        buffer.append(" 数据时间: " + Joiner.on(" | ").useForNull(" ").join(dataTimeList) + " \n");
        buffer.append(" 任务总数: " + Joiner.on(" | ").useForNull(" ").join(totalCountList) + " \n");
        buffer.append(" 转化率(%): " + Joiner.on(" | ").useForNull(" ").join(successRateList) + " \n");
        buffer.append(" 阈值(%): " + compareDTO.getThreshold() + "(" + compareDTO.getThresholdDecs() + ")" + "\n");
        buffer.append(" 成功数: " + Joiner.on(" | ").useForNull(" ").join(successCountList) + " \n");
        buffer.append(" 失败率(%): " + Joiner.on(" | ").useForNull(" ").join(failRateList) + " \n");
        buffer.append(" 失败数: " + Joiner.on(" | ").useForNull(" ").join(failCountList) + " \n");
        buffer.append(" 取消数: " + Joiner.on(" | ").useForNull(" ").join(cancelCountList) + " \n");
        return buffer.toString();
    }
}
