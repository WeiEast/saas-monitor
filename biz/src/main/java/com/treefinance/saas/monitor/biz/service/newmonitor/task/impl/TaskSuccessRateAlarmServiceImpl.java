package com.treefinance.saas.monitor.biz.service.newmonitor.task.impl;

import com.alibaba.fastjson.JSON;
import com.datatrees.notify.async.body.mail.MailEnum;
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
import com.treefinance.saas.monitor.common.domain.dto.TaskSuccRateCompareDTO;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.MonitorAlarmLevelConfigDTO;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.TaskSuccRateAlarmTimeListDTO;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.TaskSuccessRateAlarmConfigDTO;
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
import java.util.stream.Collectors;

/**
 * @author haojiahong
 * @date 2017/11/24
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
    private StringRedisTemplate redisTemplate;
    @Autowired
    private MerchantStatAccessMapper merchantStatAccessMapper;
    @Autowired
    private IvrNotifyService ivrNotifyService;
    @Autowired
    private SmsNotifyService smsNotifyService;

    private static final BigDecimal HUNDRED = new BigDecimal(100);

    /**
     * 123|
     * |234|
     * <p>
     * 123|456|
     * 修改预警的频次，将连续的 @param times 个 间隔 @param intervals 时间都放入同一个redis key中作为是否预警的标志；
     *
     * @since 20180702 修改回到原来的模式。连续的时间预警。
     */
    private boolean ifAlarmed(Date baseTime, String alarmTimeKey, TaskSuccessRateAlarmConfigDTO config) {

        int times = config.getTimes();
        int intervals = config.getIntervalMins();
        // times 个 interval分钟之前
        Date keyTime = MonitorDateUtils.addTimeUnit(baseTime, Calendar.MINUTE, -intervals * times);

        String newKey = alarmTimeKey + ":" + MonitorDateUtils.format(keyTime);

        logger.info("任务成功率预警检查key：{}是否在redis中", newKey);
        if (redisTemplate.hasKey(newKey)) {
            return true;
        }
        // 预警时间模式修改
//        for(int i=0;i<=times-1;i++){
        logger.info("add time to redis:{}", MonitorDateUtils.format(keyTime));
        redisTemplate.opsForValue().set(newKey, "1", 2, TimeUnit.DAYS);
//        }

        return false;
    }

    /**
     * 配置中保存了当前配置的环境信息
     */
    @Override
    public void alarm(EBizType bizType, TaskSuccessRateAlarmConfigDTO config, Date jobTime) {

        Integer intervalMins = getInterval(config);

        logger.info("当前的interval：{}",intervalMins);
        //由于任务执行需要时间,保证预警的精确,预警统计向前一段时间(各业务任务的超时时间),此时此段时间的任务可以保证都已统计完毕.
        //好处:预警时间即使每隔1分钟预警,依然可以保证预警的准确.坏处:收到预警消息时间向后延迟了相应时间.
        //如:jobTime=14:11,但是运营商超时时间为600s,则statTime=14:01
        Date statTime = DateUtils.addMinutes(jobTime, -2);
        //取得预警原点时间,如:statTime=14:01分,10分钟间隔统计一次,则beginTime为14:00.统计的数据间隔[13:30-13:40;13:40-13:50;13:50-14:00]
        Date beginTime = TaskMonitorPerMinKeyHelper.getRedisStatDateTime(statTime, intervalMins);

        String alarmTimeKey = TaskMonitorPerMinKeyHelper.strKeyOfAlarmTimeLog(beginTime, bizType, config.getSaasEnv());
        if (ifAlarmed(beginTime, alarmTimeKey, config)) {
            logger.info("任务成功率预警已预警,不再预警,beginTime={},bizType={}", MonitorDateUtils.format(beginTime), JSON.toJSONString(bizType));
            return;
        }

        List<SaasStatAccessDTO> list = getSourceDataList(beginTime, config, intervalMins, bizType);
        logger.info("任务成功率预警,定时任务执行jobTime={},需要预警的数据list={},beginTime={},bizType={},config={}",
                MonitorDateUtils.format(jobTime), JSON.toJSONString(list), MonitorDateUtils.format(beginTime), JSON.toJSONString(bizType), JSON.toJSONString(config));
        //校验数据是不是空的，或者是 不是三个五分钟都发出报警
        if (list.isEmpty() || list.size() != config.getTimes()) {
            return;
        }

        TaskSuccRateCompareDTO compareDTO = getPastData(beginTime, config, intervalMins, bizType);

        logger.info("过去七天平均值数据：{}", JSON.toJSONString(compareDTO));

        if (compareDTO.getTotalCount() == null || compareDTO.getTotalCount().equals(0)) {
            logger.info("过去7天内没有找到数据，不预警");
            return;
        }
        if (BigDecimal.ZERO.compareTo(compareDTO.getSuccessRate()) == 0) {
            logger.info("过去7天内平均值为零，不预警");
            return;
        }

        TaskSuccRateAlarmTimeListDTO taskSuccRateAlarmTimeConfig = findActiveTimeConfig(config);

        EAlarmLevel alarmLevel = isAlarmAndDetermineLevel(list, compareDTO, taskSuccRateAlarmTimeConfig);
        if (alarmLevel == null) {
            logger.info("任务成功率预警,定时任务执行jobTime={},判断所得数据不需要预警,list={},config={},compare={}",
                    MonitorDateUtils.format(jobTime), JSON.toJSONString(list), JSON.toJSONString(config), JSON
                            .toJSONString(compareDTO));
            return;
        }

        Map<String, MonitorAlarmLevelConfigDTO> levelConfigMap = config.getLevelConfig().stream().collect(Collectors
                .toMap(MonitorAlarmLevelConfigDTO::getLevel, monitorAlarmLevelConfigDto -> monitorAlarmLevelConfigDto));

        MonitorAlarmLevelConfigDTO levelConfigDTO = levelConfigMap.get(alarmLevel.name());

        if (levelConfigDTO == null || levelConfigDTO.getChannels().isEmpty()) {
            logger.error("没有配置{}预警等级的渠道！levelConfigMap={}", alarmLevel.name(), JSON.toJSONString(levelConfigMap));
            return;
        }
        logger.info("任务成功率预警,定时任务执行jobTim={},list={},config={},compare={}", jobTime, list, config, compareDTO);
        doAlarm(bizType, list, compareDTO, taskSuccRateAlarmTimeConfig, alarmLevel, levelConfigDTO);

        // 增加ivr服务通知
//        if (EBizType.OPERATOR == bizType) {
//            ivrNotifyService.notifyIvr(EAlarmLevel.error, EAlarmType.conversion_rate_low, "运营商转化率低于阀值");
//        }
    }

    /**
     * 需求：interval根据时间段不同也不同
     */
    private Integer getInterval(TaskSuccessRateAlarmConfigDTO config) {

        TaskSuccRateAlarmTimeListDTO inTimeConfig = findActiveTimeConfig(config);

        return inTimeConfig.getIntervals() == null ? config.getIntervalMins() : inTimeConfig.getIntervals();
    }

    private void doAlarm(EBizType bizType, List<SaasStatAccessDTO> list, TaskSuccRateCompareDTO compareDTO, TaskSuccRateAlarmTimeListDTO taskSuccRateAlarmTimeConfig, EAlarmLevel alarmLevel, MonitorAlarmLevelConfigDTO levelConfigDTO) {
        HashMap<String, String> switches = taskSuccRateAlarmTimeConfig.getSwitches();

        List<String> channels = levelConfigDTO.getChannels();

        for (String channel : channels) {
            EAlarmChannel alarmChannel = EAlarmChannel.getByValue(channel);
            if (alarmChannel == null) {
                logger.info("配置错误 无法找到对应的预警渠道");
                continue;
            }

            if (!AlarmConstants.SWITCH_ON.equals(switches.get(alarmChannel.getValue()))) {
                logger.info("{}预警渠道开关关闭", alarmChannel.getValue());
                continue;
            }

            switch (alarmChannel) {
                case EMAIL:
                    sendMailAlarm(list, bizType, alarmLevel, compareDTO);
                    break;
                case IVR:
                    sendIvr(list, alarmLevel, "");
                    break;
                case SMS:
                    sendSmsAlarm(list, bizType, alarmLevel, compareDTO);
                    break;
                case WECHAT:
                    sendWechatAlarm(list, bizType, alarmLevel, compareDTO);
                    break;
                default:
            }
        }
    }

    protected void sendIvr(List<SaasStatAccessDTO> msgList, EAlarmLevel level, String alarmBiz) {

        logger.info(alarmBiz + "发送ivr请求 {}");

        ivrNotifyService.notifyIvr(level, EAlarmType.operator_alarm, alarmBiz);
    }

    private TaskSuccRateAlarmTimeListDTO findActiveTimeConfig(TaskSuccessRateAlarmConfigDTO config) {
        TaskSuccRateAlarmTimeListDTO taskSuccRateAlarmTimeConfig = null;
        for (TaskSuccRateAlarmTimeListDTO timeConfig : config.getTimeConfig()) {
            if (timeConfig.isInTime()) {
                taskSuccRateAlarmTimeConfig = timeConfig;
            }
        }
        if (taskSuccRateAlarmTimeConfig == null) {
            throw new BizException("该时间段没有配置");
        }
        return taskSuccRateAlarmTimeConfig;
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


    private EAlarmLevel isAlarmAndDetermineLevel(List<SaasStatAccessDTO> list,
                                                 TaskSuccRateCompareDTO compareDTO, TaskSuccRateAlarmTimeListDTO taskSuccRateAlarmTimeConfig) {

        BigDecimal errorThresholdRate = taskSuccRateAlarmTimeConfig.getThresholdError();
        BigDecimal warnThresholdRate = taskSuccRateAlarmTimeConfig.getThresholdWarning();
        BigDecimal infoThresholdRate = taskSuccRateAlarmTimeConfig.getThresholdInfo();

        if (errorThresholdRate == null || warnThresholdRate == null || infoThresholdRate == null) {
            throw new BizException("任务成功率预警，当前时间没有设定错误、警告级别阈值");
        }

        BigDecimal operand = compareDTO.getSuccessRate().divide(HUNDRED, 2, RoundingMode.HALF_UP);

        BigDecimal errorThreshold = errorThresholdRate.multiply(operand);
        BigDecimal warnThreshold = warnThresholdRate.multiply(operand);
        BigDecimal infoThreshold = infoThresholdRate.multiply(operand);

        boolean isError = true;
        boolean isWarn = true;
        boolean isInfo = true;
        EAlarmLevel level = null;

        for (SaasStatAccessDTO saasStatAccessDTO : list) {
            if (saasStatAccessDTO.getConversionRate().compareTo(errorThreshold) < 0) {
                level = EAlarmLevel.error;
                isWarn = false;
                isInfo = false;
            } else if (saasStatAccessDTO.getConversionRate().compareTo(warnThreshold) < 0) {
                level = EAlarmLevel.warning;
                isError = false;
                isInfo = false;
            } else if (saasStatAccessDTO.getConversionRate().compareTo(infoThreshold) < 0) {
                level = EAlarmLevel.info;
                isWarn = false;
                isError = false;
            }else{
                level = null;
                isError = isInfo = isWarn = false;
                break;
            }
            logger.info("任务成功率预警，数据时间：{},传化率：{},命中等级：{}",
                    MonitorDateUtils.format(saasStatAccessDTO.getDataTime()), saasStatAccessDTO.getConversionRate(), level);
        }
        if (level == null){
            logger.info("任务成功率预警,没有命中任务等级，无需预警，直接返回，数据list：{}",list);
            return null;
        }
        if (isError) {
            compareDTO.setThreshold(errorThreshold);
            compareDTO.setThresholdDecs(errorThresholdRate.divide(HUNDRED, 2, RoundingMode.HALF_UP).toPlainString() + "*" + compareDTO.getSuccessRate().toPlainString());
            return EAlarmLevel.error;
        } else if (isWarn) {
            compareDTO.setThreshold(warnThreshold);
            compareDTO.setThresholdDecs(warnThresholdRate.divide(HUNDRED, 2, RoundingMode.HALF_UP).toPlainString() + "*" + compareDTO.getSuccessRate().toPlainString());
            return EAlarmLevel.warning;
        } else if (isInfo) {
            compareDTO.setThreshold(infoThreshold);
            compareDTO.setThresholdDecs(infoThresholdRate.divide(HUNDRED, 2, RoundingMode.HALF_UP).toPlainString() + "*" + compareDTO.getSuccessRate().toPlainString());
            return EAlarmLevel.info;
        }
        logger.info("任务成功率预警,预警命中了不同等级，数据：{}，直接返回",list);

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
        String body = this.genMailBody(list, bizType, EAlarmChannel.EMAIL, alarmLevel, compareDTO);
        alarmMessageProducer.sendMail(title, body, MailEnum.HTML_MAIL);
    }

    private String generateTitle(EBizType type) {
        return "saas-" + diamondConfig.getMonitorEnvironment() + "[" + type.getDesc() + "]任务成功率预警";
    }

    private void sendSmsAlarm(List<SaasStatAccessDTO> list, EBizType type, EAlarmLevel alarmLevel, TaskSuccRateCompareDTO compareDTO) {
        String body = this.generateMessageBody(list, type, EAlarmChannel.SMS, alarmLevel, compareDTO);
        smsNotifyService.send(body);
    }

    private String genMailBody(List<SaasStatAccessDTO> list, EBizType type, EAlarmChannel sendType,
                               EAlarmLevel alarmLevel, TaskSuccRateCompareDTO compareDTO) {
        StringBuffer buffer = new StringBuffer();
        if (EBizType.OPERATOR.equals(type)) {
            buffer.append("【").append(alarmLevel).append("】");
        } else {
            buffer.append("【").append(alarmLevel).append("】");
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

        buffer.append("<table>");
        buffer.append("<tr>");
        buffer.append("<td>环境标志</td>");
        buffer.append("<td colspan ='3'>").append(compareDTO.getEvn()).append("</td>");
        buffer.append("</tr>");

        buffer.append("<tr>");
        buffer.append("<td>阈值</td>");
        buffer.append("<td colspan ='3'>").append(compareDTO.getThreshold()).append("(").append(compareDTO
                .getThresholdDecs()).append(")").append("</td>");
        buffer.append("</tr>");

        buffer.append("<tr>");
        buffer.append("<td>数据时间</td>");
        buffer.append("<td>").append(Joiner.on("</td><td>").useForNull(" ").join(dataTimeList)).append("</td>");
        buffer.append("</tr>");

        buffer.append("<tr>");
        buffer.append("<td>任务总数</td>");
        buffer.append("<td>").append(Joiner.on("</td><td>").useForNull(" ").join(totalCountList)).append("</td>");
        buffer.append("</tr>");

        buffer.append("<tr>");
        buffer.append("<td>转化率(%)</td>");
        buffer.append("<td>").append(Joiner.on("</td><td>").useForNull(" ").join(successRateList)).append("</td>");
        buffer.append("</tr>");

        buffer.append("<tr>");
        buffer.append("<td>成功数</td>");
        buffer.append("<td>").append(Joiner.on("</td><td>").useForNull(" ").join(successCountList)).append("</td>");
        buffer.append("</tr>");

        buffer.append("<tr>");
        buffer.append("<td>失败率(%)</td>");
        buffer.append("<td>").append(Joiner.on("</td><td>").useForNull(" ").join(failRateList)).append("</td>");
        buffer.append("</tr>");

        buffer.append("<tr>");
        buffer.append("<td>失败数</td>");
        buffer.append("<td>").append(Joiner.on("</td><td>").useForNull(" ").join(failCountList)).append("</td>");
        buffer.append("</tr>");

        buffer.append("<tr>");
        buffer.append("<td>取消数</td>");
        buffer.append("<td>").append(Joiner.on("</td><td>").useForNull(" ").join(cancelCountList)).append("</td>");
        buffer.append("</tr>");
        buffer.append("</table>");

        return buffer.toString();

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
        buffer.append(" 阈值(%): " + compareDTO.getThreshold() + "(" + compareDTO.getThresholdDecs() + ")" + "\n");
        buffer.append(" 数据时间: " + Joiner.on(" | ").useForNull(" ").join(dataTimeList) + " \n");
        buffer.append(" 任务总数: " + Joiner.on(" | ").useForNull(" ").join(totalCountList) + " \n");
        buffer.append(" 转化率(%): " + Joiner.on(" | ").useForNull(" ").join(successRateList) + " \n");
        buffer.append(" 成功数: " + Joiner.on(" | ").useForNull(" ").join(successCountList) + " \n");
        buffer.append(" 失败率(%): " + Joiner.on(" | ").useForNull(" ").join(failRateList) + " \n");
        buffer.append(" 失败数: " + Joiner.on(" | ").useForNull(" ").join(failCountList) + " \n");
        buffer.append(" 取消数: " + Joiner.on(" | ").useForNull(" ").join(cancelCountList) + " \n");
        return buffer.toString();
    }
}
