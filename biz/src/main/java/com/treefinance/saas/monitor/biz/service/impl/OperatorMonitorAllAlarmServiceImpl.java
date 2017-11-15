package com.treefinance.saas.monitor.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.biz.service.OperatorMonitorAllAlarmService;
import com.treefinance.saas.monitor.common.domain.dto.OperatorAllStatAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorStatAccessAlarmMsgDTO;
import com.treefinance.saas.monitor.common.enumeration.ETaskOperatorStatType;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.OperatorAllStatAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorAllStatAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.OperatorAllStatAccessMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by haojiahong on 2017/11/13.
 */
@Service
public class OperatorMonitorAllAlarmServiceImpl implements OperatorMonitorAllAlarmService {

    private static final Logger logger = LoggerFactory.getLogger(OperatorMonitorAllAlarmService.class);

    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private OperatorAllStatAccessMapper operatorAllStatAccessMapper;
    @Autowired
    private AlarmMessageProducer alarmMessageProducer;


    @Override
    public void alarm(Date now, Date dataTime, ETaskOperatorStatType statType) {
        try {
            OperatorAllStatAccessCriteria criteria = new OperatorAllStatAccessCriteria();
            criteria.createCriteria().andDataTypeEqualTo(statType.getCode())
                    .andDataTimeEqualTo(MonitorDateUtils.getIntervalTime(dataTime, diamondConfig.getOperatorMonitorIntervalMinutes()));
            List<OperatorAllStatAccess> list = operatorAllStatAccessMapper.selectByExample(criteria);
            if (CollectionUtils.isEmpty(list)) {
                logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},此段时间内,未查询到所有运营商的统计数据list={}",
                        MonitorDateUtils.format(now), MonitorDateUtils.format(dataTime), JSON.toJSONString(list));
                return;
            }

            OperatorAllStatAccess operatorAllStatAccess = list.get(0);
            OperatorAllStatAccessDTO dataDTO = DataConverterUtils.convert(operatorAllStatAccess, OperatorAllStatAccessDTO.class);
            //获取前7天内,相同时刻运营商统计的平均值(登录转化率平均值,抓取成功率平均值,洗数成功率平均值)
            OperatorAllStatAccessDTO compareDTO = getPreviousCompareData(now, dataTime, dataDTO, statType);
            logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},获取前n天内,相同时刻所有运营商统计的平均值compareDTO={}",
                    MonitorDateUtils.format(now), MonitorDateUtils.format(dataTime), JSON.toJSONString(compareDTO));
            if (compareDTO == null) {
                return;
            }

            //获取需要预警的数据信息
            List<OperatorStatAccessAlarmMsgDTO> msgList = getAlarmMsgList(now, dataTime, dataDTO, compareDTO, statType);
            logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},区分运营商统计需要预警的数据信息msgList={}",
                    MonitorDateUtils.format(now), MonitorDateUtils.format(dataTime), JSON.toJSONString(msgList));
            if (CollectionUtils.isEmpty(msgList)) {
                return;
            }
            //发送预警
            alarmMsg(msgList, now, dataTime, statType);
        } catch (Exception e) {
            logger.error("运营商监控,预警定时任务执行jobTime={}异常", MonitorDateUtils.format(now), e);
        }

    }

    private void alarmMsg(List<OperatorStatAccessAlarmMsgDTO> msgList, Date jobTime, Date dataTime, ETaskOperatorStatType statType) {
        String mailSwitch, weChatSwitch, baseTile;
        if (ETaskOperatorStatType.TASK.equals(statType)) {
            mailSwitch = diamondConfig.getOperatorAlarmMailSwitch();
            weChatSwitch = diamondConfig.getOperatorAlarmWechatSwitch();
            baseTile = "总运营商监控(按任务数统计)";

        } else {
            mailSwitch = diamondConfig.getOperatorAlarmUserMailSwitch();
            weChatSwitch = diamondConfig.getOperatorAlarmUserWechatSwitch();
            baseTile = "总运营商监控(按人数统计)";

        }
        if (StringUtils.equalsIgnoreCase(mailSwitch, "on")) {
            String mailDataBody = generateMailDataBody(msgList, dataTime, baseTile);
            String title = generateTitle(baseTile);
            alarmMessageProducer.sendMail4OperatorMonitor(title, mailDataBody, jobTime);
        } else {
            logger.info("运营商监控,预警定时任务执行jobTime={},发送邮件开关已关闭", MonitorDateUtils.format(jobTime));

        }
        if (StringUtils.equalsIgnoreCase(weChatSwitch, "on")) {
            String weChatBody = generateWeChatBody(msgList, dataTime, baseTile);
            alarmMessageProducer.sendWebChart4OperatorMonitor(weChatBody, jobTime);
        } else {
            logger.info("运营商监控,预警定时任务执行jobTime={},发送微信开关已关闭", MonitorDateUtils.format(jobTime));
        }
    }

    private String generateTitle(String baseTile) {
        return "saas-" + diamondConfig.getMonitorEnvironment() + baseTile + "发生预警";
    }

    private String generateMailDataBody(List<OperatorStatAccessAlarmMsgDTO> msgList, Date dataTime, String baseTile) {
        Integer intervalMins = diamondConfig.getOperatorMonitorIntervalMinutes();
        StringBuffer buffer = new StringBuffer();
        buffer.append("<br>").append("您好，").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append(baseTile)
                .append("预警,在")
                .append(MonitorDateUtils.format(MonitorDateUtils.getIntervalTime(dataTime, intervalMins)))
                .append("--")
                .append(MonitorDateUtils.format(MonitorDateUtils.getIntervalTime(DateUtils.addMinutes(dataTime, intervalMins), intervalMins)))
                .append("时段数据存在问题").append("，此时监控数据如下，请及时处理：").append("</br>");
        buffer.append("<table border=\"1\" cellspacing=\"0\" bordercolor=\"#BDBDBD\" width=\"80%\">");
        buffer.append("<tr bgcolor=\"#C9C9C9\">")
                .append("<th>").append("预警描述").append("</th>")
                .append("<th>").append("当前指标值(%)").append("</th>")
                .append("<th>").append("指标阀值(%)").append("</th>")
                .append("<th>").append("偏离阀值程度(%)").append("</th>")
                .append("</tr>");
        for (OperatorStatAccessAlarmMsgDTO msg : msgList) {
            buffer.append("<tr>")
                    .append("<td>").append(msg.getAlarmDesc()).append("</td>")
                    .append("<td>").append(msg.getValue()).append("</td>")
                    .append("<td>").append(msg.getThreshold()).append("</td>")
                    .append("<td>").append(msg.getOffset()).append("</td>").append("</tr>");

        }
        buffer.append("</table>");
        return buffer.toString();
    }

    private String generateWeChatBody(List<OperatorStatAccessAlarmMsgDTO> msgList, Date dataTime, String baseTile) {
        Integer intervalMins = diamondConfig.getOperatorMonitorIntervalMinutes();
        StringBuffer buffer = new StringBuffer();
        buffer.append("您好，").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append(baseTile)
                .append("预警,在")
                .append(MonitorDateUtils.format(MonitorDateUtils.getIntervalTime(dataTime, intervalMins)))
                .append("--")
                .append(MonitorDateUtils.format(MonitorDateUtils.getIntervalTime(DateUtils.addMinutes(dataTime, intervalMins), intervalMins)))
                .append("时段数据存在问题").append("，此时监控数据如下，请及时处理：").append("\n");
        for (OperatorStatAccessAlarmMsgDTO msg : msgList) {
            buffer.append("【").append(msg.getAlarmSimpleDesc()).append("】")
                    .append("当前指标值:").append("【").append(msg.getValue()).append("%").append("】")
                    .append("指标阀值:").append("【").append(msg.getThreshold()).append("%").append("】")
                    .append("偏离阀值程度:").append("【").append(msg.getOffset()).append("%").append("】")
                    .append("\n");
        }
        return buffer.toString();
    }

    /**
     * 获取需要预警的数据信息
     *
     * @param now
     * @param dataTime
     * @param dataDTO
     * @param compareDTO
     * @param statType
     * @return
     */
    private List<OperatorStatAccessAlarmMsgDTO> getAlarmMsgList(Date now, Date dataTime, OperatorAllStatAccessDTO dataDTO, OperatorAllStatAccessDTO compareDTO, ETaskOperatorStatType statType) {
        List<OperatorStatAccessAlarmMsgDTO> msgList = Lists.newArrayList();
        Integer threshold, previousDays;
        if (ETaskOperatorStatType.TASK.equals(statType)) {
            threshold = diamondConfig.getOperatorAlarmThresholdPercent();
            previousDays = diamondConfig.getOperatorAlarmPreviousDays();
        } else {
            threshold = diamondConfig.getOperatorAlarmUserThresholdPercent();
            previousDays = diamondConfig.getOperatorAlarmUserPreviousDays();
        }

        BigDecimal confirmMobileCompareVal = compareDTO.getPreviousConfirmMobileConversionRate().multiply(new BigDecimal(threshold)).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal loginConversionCompareVal = compareDTO.getPreviousLoginConversionRate().multiply(new BigDecimal(threshold)).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal loginSuccessCompareVal = compareDTO.getPreviousLoginSuccessRate().multiply(new BigDecimal(threshold)).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal crawlCompareVal = compareDTO.getPreviousCrawlSuccessRate().multiply(new BigDecimal(threshold)).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal processCompareVal = compareDTO.getPreviousProcessSuccessRate().multiply(new BigDecimal(threshold)).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal callbackCompareVal = compareDTO.getPreviousCallbackSuccessRate().multiply(new BigDecimal(threshold)).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);

        if (dataDTO.getConfirmMobileConversionRate().compareTo(confirmMobileCompareVal) < 0) {//确认手机转化率小于前7天平均值
            OperatorStatAccessAlarmMsgDTO msg = new OperatorStatAccessAlarmMsgDTO();
            msg.setAlarmDesc("确认手机转化率低于前" + previousDays + "天平均值的" + threshold + "%");
            msg.setAlarmSimpleDesc("确认手机");
            msg.setValue(dataDTO.getConfirmMobileConversionRate());
            msg.setThreshold(confirmMobileCompareVal);
            if (BigDecimal.ZERO.compareTo(confirmMobileCompareVal) == 0) {
                msg.setOffset(BigDecimal.ZERO);
            } else {
                BigDecimal value = BigDecimal.ONE.subtract(dataDTO.getConfirmMobileConversionRate().divide(confirmMobileCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                msg.setOffset(value);
            }
            msgList.add(msg);
        }

        if (dataDTO.getLoginConversionRate().compareTo(loginConversionCompareVal) < 0) {//登录转化率小于前7天平均值
            OperatorStatAccessAlarmMsgDTO msg = new OperatorStatAccessAlarmMsgDTO();
            msg.setAlarmDesc("登陆转化率低于前" + previousDays + "天平均值的" + threshold + "%");
            msg.setAlarmSimpleDesc("开始登陆");
            msg.setValue(dataDTO.getLoginConversionRate());
            msg.setThreshold(loginConversionCompareVal);
            if (BigDecimal.ZERO.compareTo(loginConversionCompareVal) == 0) {
                msg.setOffset(BigDecimal.ZERO);
            } else {
                BigDecimal value = BigDecimal.ONE.subtract(dataDTO.getLoginConversionRate().divide(loginConversionCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                msg.setOffset(value);
            }
            msgList.add(msg);
        }
        if (dataDTO.getLoginSuccessRate().compareTo(loginSuccessCompareVal) < 0) {//登录成功率小于前7天平均值
            OperatorStatAccessAlarmMsgDTO msg = new OperatorStatAccessAlarmMsgDTO();
            msg.setAlarmDesc("登陆成功率低于前" + previousDays + "天平均值的" + threshold + "%");
            msg.setAlarmSimpleDesc("登陆");
            msg.setValue(dataDTO.getLoginSuccessRate());
            msg.setThreshold(loginSuccessCompareVal);
            if (BigDecimal.ZERO.compareTo(loginSuccessCompareVal) == 0) {
                msg.setOffset(BigDecimal.ZERO);
            } else {
                BigDecimal value = BigDecimal.ONE.subtract(dataDTO.getLoginSuccessRate().divide(loginSuccessCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                msg.setOffset(value);
            }
            msgList.add(msg);
        }
        if (dataDTO.getCrawlSuccessRate().compareTo(crawlCompareVal) < 0) {//抓取成功率小于前7天平均值
            OperatorStatAccessAlarmMsgDTO msg = new OperatorStatAccessAlarmMsgDTO();
            msg.setAlarmDesc("抓取成功率低于前" + previousDays + "天平均值的" + threshold + "%");
            msg.setAlarmSimpleDesc("抓取");
            msg.setValue(dataDTO.getCrawlSuccessRate());
            msg.setThreshold(crawlCompareVal);
            if (BigDecimal.ZERO.compareTo(crawlCompareVal) == 0) {
                msg.setOffset(BigDecimal.ZERO);
            } else {
                BigDecimal value = BigDecimal.ONE.subtract(dataDTO.getCrawlSuccessRate().divide(crawlCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                msg.setOffset(value);
            }
            msgList.add(msg);
        }
        if (dataDTO.getProcessSuccessRate().compareTo(processCompareVal) < 0) {//洗数成功率小于前7天平均值
            OperatorStatAccessAlarmMsgDTO msg = new OperatorStatAccessAlarmMsgDTO();
            msg.setAlarmDesc("洗数成功率低于前" + previousDays + "天平均值的" + threshold + "%");
            msg.setAlarmSimpleDesc("洗数");
            msg.setValue(dataDTO.getProcessSuccessRate());
            msg.setThreshold(processCompareVal);
            if (BigDecimal.ZERO.compareTo(processCompareVal) == 0) {
                msg.setOffset(BigDecimal.ZERO);
            } else {
                BigDecimal value = BigDecimal.ONE.subtract(dataDTO.getProcessSuccessRate().divide(processCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                msg.setOffset(value);
            }
            msgList.add(msg);
        }
        if (dataDTO.getCallbackSuccessRate().compareTo(callbackCompareVal) < 0) {//回调成功率小于前7天平均值
            OperatorStatAccessAlarmMsgDTO msg = new OperatorStatAccessAlarmMsgDTO();
            msg.setAlarmDesc("回调成功率低于前" + previousDays + "天平均值的" + threshold + "%");
            msg.setAlarmSimpleDesc("回调");
            msg.setValue(dataDTO.getCallbackSuccessRate());
            msg.setThreshold(callbackCompareVal);
            if (BigDecimal.ZERO.compareTo(callbackCompareVal) == 0) {
                msg.setOffset(BigDecimal.ZERO);
            } else {
                BigDecimal value = BigDecimal.ONE.subtract(dataDTO.getCallbackSuccessRate().divide(callbackCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                msg.setOffset(value);
            }
            msgList.add(msg);

        }

        return null;
    }

    /**
     * 获取前7天内,相同时刻运营商统计的平均值(登录转化率平均值,抓取成功率平均值,洗数成功率平均值)
     *
     * @param now
     * @param dataTime
     * @param dataDTO
     * @param statType
     * @return
     */
    private OperatorAllStatAccessDTO getPreviousCompareData(Date now, Date dataTime, OperatorAllStatAccessDTO dataDTO, ETaskOperatorStatType statType) {
        Integer previousDays;
        if (ETaskOperatorStatType.TASK.equals(statType)) {
            previousDays = diamondConfig.getOperatorAlarmPreviousDays();
        } else {
            previousDays = diamondConfig.getOperatorAlarmUserPreviousDays();
        }
        List<Date> previousOClockList = MonitorDateUtils.getPreviousOClockTime(MonitorDateUtils.getIntervalTime(dataTime, diamondConfig.getOperatorMonitorIntervalMinutes()), previousDays);
        OperatorAllStatAccessCriteria previousCriteria = new OperatorAllStatAccessCriteria();
        previousCriteria.createCriteria().andDataTypeEqualTo(statType.getCode()).andDataTimeIn(previousOClockList);
        List<OperatorAllStatAccess> previousList = operatorAllStatAccessMapper.selectByExample(previousCriteria);
        if (CollectionUtils.isEmpty(previousList)) {
            logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},在此时间前{}天内,未查询到所有运营商统计数据previousOClockList={},list={}",
                    MonitorDateUtils.format(now), MonitorDateUtils.format(dataTime), previousDays,
                    JSON.toJSONString(previousOClockList), JSON.toJSONString(previousList));
            return null;
        }
        List<OperatorAllStatAccessDTO> previousDTOList = DataConverterUtils.convert(previousList, OperatorAllStatAccessDTO.class);
        BigDecimal previousConfirmMobileConversionRateCount = BigDecimal.ZERO;
        BigDecimal previousLoginConversionRateCount = BigDecimal.ZERO;
        BigDecimal previousLoginSuccessRateCount = BigDecimal.ZERO;
        BigDecimal previousCrawlSuccessRateCount = BigDecimal.ZERO;
        BigDecimal previousProcessSuccessRateCount = BigDecimal.ZERO;
        BigDecimal previousCallbackSuccessRateCount = BigDecimal.ZERO;

        for (OperatorAllStatAccessDTO previousDTO : previousDTOList) {
            previousConfirmMobileConversionRateCount = previousConfirmMobileConversionRateCount.add(previousDTO.getConfirmMobileConversionRate());
            previousLoginConversionRateCount = previousLoginConversionRateCount.add(previousDTO.getLoginConversionRate());
            previousLoginSuccessRateCount = previousLoginSuccessRateCount.add(previousDTO.getLoginSuccessRate());
            previousCrawlSuccessRateCount = previousCrawlSuccessRateCount.add(previousDTO.getCrawlSuccessRate());
            previousProcessSuccessRateCount = previousProcessSuccessRateCount.add(previousDTO.getProcessSuccessRate());
            previousCallbackSuccessRateCount = previousCallbackSuccessRateCount.add(previousDTO.getCallbackSuccessRate());
        }
        OperatorAllStatAccessDTO compareDto = new OperatorAllStatAccessDTO();
        compareDto.setPreviousConfirmMobileConversionRate(previousConfirmMobileConversionRateCount.divide(BigDecimal.valueOf(previousDTOList.size()), 2, BigDecimal.ROUND_HALF_UP));
        compareDto.setPreviousLoginConversionRate(previousLoginConversionRateCount.divide(BigDecimal.valueOf(previousDTOList.size()), 2, BigDecimal.ROUND_HALF_UP));
        compareDto.setPreviousLoginSuccessRate(previousLoginSuccessRateCount.divide(BigDecimal.valueOf(previousDTOList.size()), 2, BigDecimal.ROUND_HALF_UP));
        compareDto.setPreviousCrawlSuccessRate(previousCrawlSuccessRateCount.divide(BigDecimal.valueOf(previousDTOList.size()), 2, BigDecimal.ROUND_HALF_UP));
        compareDto.setPreviousProcessSuccessRate(previousProcessSuccessRateCount.divide(BigDecimal.valueOf(previousDTOList.size()), 2, BigDecimal.ROUND_HALF_UP));
        compareDto.setPreviousCallbackSuccessRate(previousCallbackSuccessRateCount.divide(BigDecimal.valueOf(previousDTOList.size()), 2, BigDecimal.ROUND_HALF_UP));
        return compareDto;
    }
}
