package com.treefinance.saas.monitor.biz.service.newmonitor.task.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.TaskMonitorPerMinKeyHelper;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.biz.service.newmonitor.task.TaskSuccessRateAlarmService;
import com.treefinance.saas.monitor.common.domain.dto.SaasStatAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.TaskSuccessRateAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.common.enumeration.EStatType;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.MerchantStatAccess;
import com.treefinance.saas.monitor.dao.entity.MerchantStatAccessCriteria;
import com.treefinance.saas.monitor.dao.entity.SaasStatAccess;
import com.treefinance.saas.monitor.dao.entity.SaasStatAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.MerchantStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.SaasStatAccessMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private MerchantStatAccessMapper merchantStatAccessMapper;

    @Override
    public void alarm(EBizType bizType, TaskSuccessRateAlarmConfigDTO config, Date jobTime) {
        Integer intervalMins = config.getIntervalMins();
        //由于任务执行需要时间,保证预警的精确,预警统计向前一段时间(各业务任务的超时时间),此时此段时间的任务可以保证都已统计完毕.
        //好处:预警时间即使每隔1分钟预警,依然可以保证预警的准确.坏处:收到预警消息时间向后延迟了相应时间.
        //如:jobTime=14:11,但是运营商超时时间为600s,则statTime=14:01
        Date statTime = DateUtils.addSeconds(jobTime, -config.getTaskTimeoutSecs());
        //取得预警原点时间,如:statTime=14:01分,10分钟间隔统计一次,则beginTime为14:00.统计的数据间隔[13:30-13:40;13:40-13:50;13:50-14:00]
        Date beginTime = TaskMonitorPerMinKeyHelper.getRedisStatDateTime(statTime, intervalMins);
        int times = config.getTimes();
        EStatType statType = null;
        for (EStatType item : EStatType.values()) {
            if (item.getText().equals(bizType.getText())) {
                statType = item;
            }
        }
        if (statType == null) {
            return;
        }

        String alarmTimeKey = TaskMonitorPerMinKeyHelper.keyOfAlarmTimeLog(beginTime, statType);
        BoundSetOperations<String, Object> setOperations = redisTemplate.boundSetOps(alarmTimeKey);
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(alarmTimeKey))) {
            setOperations.expire(2, TimeUnit.DAYS);
        }
        if (setOperations.isMember(MonitorDateUtils.format(beginTime))) {
            logger.info("任务成功率预警,beginTime={},statType={}已预警,不再预警", MonitorDateUtils.format(beginTime), JSON.toJSONString(statType));
            return;
        }
        setOperations.add(MonitorDateUtils.format(beginTime));

        List<SaasStatAccessDTO> list = getNeedAlarmDataList(beginTime, times, intervalMins, statType);
        logger.info("任务成功率预警,定时任务执行jobTime={},需要预警的数据list={},beginTime={},statType={},config={}",
                MonitorDateUtils.format(jobTime), JSON.toJSONString(list), MonitorDateUtils.format(beginTime), JSON.toJSONString(statType), JSON.toJSONString(config));
        boolean isAlarm = isAlarm(list, config);
        if (!isAlarm) {
            logger.info("任务成功率预警,定时任务执行jobTime={},判断所得数据不需要预警,list={},config={}",
                    MonitorDateUtils.format(jobTime), JSON.toJSONString(list), JSON.toJSONString(config));
            return;
        }
        if (StringUtils.equalsIgnoreCase(config.getMailAlarmSwitch(), "on")) {
            sendMailAlarm(list, statType);
        }
        if (StringUtils.equalsIgnoreCase(config.getWeChatAlarmSwitch(), "on")) {
            sendWechatAlarm(list, statType);
        }
    }

    private boolean isAlarm(List<SaasStatAccessDTO> list, TaskSuccessRateAlarmConfigDTO config) {
        Integer threshold = config.getSuccesThreshold();
        for (SaasStatAccessDTO data : list) {
            if (data.getTotalCount() == 0) {
                return false;
            }
            if (data.getConversionRate().compareTo(BigDecimal.valueOf(threshold)) > 0) {
                return false;
            }
        }
        return true;
    }

    private List<SaasStatAccessDTO> getNeedAlarmDataList(Date beginTime, Integer times, Integer intervalMins, EStatType statType) {
        List<SaasStatAccessDTO> list = Lists.newArrayList();
        //左闭右开
        for (int i = 0; i < times; i++) {
            Date startTime = DateUtils.addMinutes(beginTime, -((times - i) * intervalMins));
            Date endTime = DateUtils.addMinutes(beginTime, -((times - i - 1) * intervalMins));

            SaasStatAccessCriteria saasStatAccessCriteria = new SaasStatAccessCriteria();
            saasStatAccessCriteria.createCriteria().andDataTypeEqualTo(statType.getType())
                    .andDataTimeGreaterThanOrEqualTo(startTime)
                    .andDataTimeLessThan(endTime);
            List<SaasStatAccess> dataList = saasStatAccessMapper.selectByExample(saasStatAccessCriteria);
            int totalCount = 0, successCount = 0, failCount = 0, cancelCount = 0;
            for (SaasStatAccess data : dataList) {
                totalCount = totalCount + data.getTotalCount();
                successCount = successCount + data.getSuccessCount();
                failCount = failCount + data.getFailCount();
                cancelCount = cancelCount + data.getCancelCount();
            }

            SaasStatAccessDTO excludeData = getExcludeAlarmList(statType, startTime, endTime);
            if (excludeData != null) {
                totalCount = totalCount - excludeData.getTotalCount();
                successCount = successCount - excludeData.getSuccessCount();
                failCount = failCount - excludeData.getFailCount();
                cancelCount = cancelCount - excludeData.getCancelCount();
                logger.info("任务成功率预警,排除数据data={}", JSON.toJSONString(excludeData));
            }

            SaasStatAccessDTO saasStatAccessDTO = new SaasStatAccessDTO();
            saasStatAccessDTO.setDataTime(startTime);
            saasStatAccessDTO.setDataType(statType.getType());
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

    private SaasStatAccessDTO getExcludeAlarmList(EStatType statType, Date startTime, Date endTime) {
        List<String> excludeAppIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(diamondConfig.getMonitorAlarmExcludeAppIdsAll())) {
            excludeAppIds = Splitter.on(",").trimResults().splitToList(diamondConfig.getMonitorAlarmExcludeAppIdsAll());
        }
        logger.info("任务成功率预警,排除预警商户appIds={}", JSON.toJSONString(excludeAppIds));
        if (!CollectionUtils.isEmpty(excludeAppIds)) {
            MerchantStatAccessCriteria merchantCriteria = new MerchantStatAccessCriteria();
            merchantCriteria.createCriteria().andAppIdIn(excludeAppIds)
                    .andDataTypeEqualTo(statType.getType())
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
            excludeData.setDataType(statType.getType());
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

    private void sendWechatAlarm(List<SaasStatAccessDTO> list, EStatType type) {
        String body = this.generateMessageBody(list, type);
        alarmMessageProducer.sendWechantAlarm(body);
    }

    private void sendMailAlarm(List<SaasStatAccessDTO> list, EStatType statType) {
        String title = this.generateTitle(statType);
        String body = this.generateMessageBody(list, statType);
        alarmMessageProducer.sendMailAlarm(title, body);
    }

    private String generateTitle(EStatType type) {
        return "saas-" + diamondConfig.getMonitorEnvironment() + "[" + type.getName() + "]任务成功率预警";
    }

    private String generateMessageBody(List<SaasStatAccessDTO> list, EStatType type) {
        StringBuffer buffer = new StringBuffer();
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
