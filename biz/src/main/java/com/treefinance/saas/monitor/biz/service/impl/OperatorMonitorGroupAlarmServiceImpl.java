package com.treefinance.saas.monitor.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.biz.service.OperatorMonitorGroupAlarmService;
import com.treefinance.saas.monitor.common.domain.dto.OperatorStatAccessAlarmMsgDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorStatAccessDTO;
import com.treefinance.saas.monitor.common.enumeration.ETaskOperatorStatType;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.OperatorStatAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorStatAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.OperatorStatAccessMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by haojiahong on 2017/11/13.
 */
@Service
public class OperatorMonitorGroupAlarmServiceImpl implements OperatorMonitorGroupAlarmService {

    private static final Logger logger = LoggerFactory.getLogger(OperatorMonitorGroupAlarmService.class);

    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private OperatorStatAccessMapper operatorStatAccessMapper;
    @Autowired
    private AlarmMessageProducer alarmMessageProducer;

    @Override
    public void alarm(Date now, Date dataTime, ETaskOperatorStatType statType) {
        try {
            List<String> operatorNameList = Splitter.on(",").splitToList(diamondConfig.getOperatorAlarmOperatorNameList());
            if (CollectionUtils.isEmpty(operatorNameList)) {
                logger.error("运营商监控,预警定时任务执行jobTime={},未配置需要预警的运营商,operator.alarm.operator.name.list未配置",
                        MonitorDateUtils.format(now));
                return;
            }
            OperatorStatAccessCriteria criteria = new OperatorStatAccessCriteria();
            criteria.createCriteria()
                    .andDataTypeEqualTo(statType.getCode())
                    .andGroupNameIn(operatorNameList)
                    .andDataTimeEqualTo(MonitorDateUtils.getIntervalTime(dataTime, diamondConfig.getOperatorMonitorIntervalMinutes()));
            List<OperatorStatAccess> list = operatorStatAccessMapper.selectByExample(criteria);
            if (CollectionUtils.isEmpty(list)) {
                logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},此段时间内,未查询到区分运营商的统计数据list={}",
                        MonitorDateUtils.format(now), MonitorDateUtils.format(dataTime), JSON.toJSONString(list));
                return;
            }
            List<OperatorStatAccessDTO> dtoList = DataConverterUtils.convert(list, OperatorStatAccessDTO.class);

            //获取前7天内,相同时刻运营商统计的平均值(登录转化率平均值,抓取成功率平均值,洗数成功率平均值)
            //<groupCode,OperatorStatAccessDTO>
            Map<String, OperatorStatAccessDTO> compareMap = getPreviousCompareDataMap(now, dataTime, dtoList, statType);
            logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},获取前n天内,相同时刻区分运营商统计的平均值compareMap={}",
                    MonitorDateUtils.format(now), MonitorDateUtils.format(dataTime), JSON.toJSONString(compareMap));
            if (MapUtils.isEmpty(compareMap)) {
                return;
            }

            //获取需要预警的数据信息
            List<OperatorStatAccessAlarmMsgDTO> msgList = getAlarmMsgList(now, dataTime, dtoList, compareMap, statType);
            logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},区分运营商统计需要预警的数据信息msgList={}",
                    MonitorDateUtils.format(now), MonitorDateUtils.format(dataTime), JSON.toJSONString(msgList));
            if (CollectionUtils.isEmpty(msgList)) {
                return;
            }
            //发送预警
            alarmMsg(msgList, now, dataTime, statType);
        } catch (Exception e) {
            logger.error("运营商监控,预警定时任务执行jobTime={},statType={}异常", MonitorDateUtils.format(now), statType, e);
        }
    }

    /**
     * 发送预警
     *
     * @param msgList
     * @param jobTime
     * @param dataTime
     * @param statType
     */
    private void alarmMsg(List<OperatorStatAccessAlarmMsgDTO> msgList, Date jobTime, Date dataTime, ETaskOperatorStatType statType) {
        String mailSwitch, weChatSwitch, baseTitle;
        if (ETaskOperatorStatType.TASK.equals(statType)) {
            mailSwitch = diamondConfig.getOperatorAlarmMailSwitch();
            weChatSwitch = diamondConfig.getOperatorAlarmWechatSwitch();
            baseTitle = "运营商监控(按任务数统计)";
        } else {
            mailSwitch = diamondConfig.getOperatorAlarmUserMailSwitch();
            weChatSwitch = diamondConfig.getOperatorAlarmUserWechatSwitch();
            baseTitle = "运营商监控(按人数统计)";
        }
        if (StringUtils.equalsIgnoreCase(mailSwitch, "on")) {
            String mailDataBody = generateMailDataBody(msgList, dataTime, baseTitle);
            String title = generateTitle(baseTitle);
            alarmMessageProducer.sendMail4OperatorMonitor(title, mailDataBody, jobTime);
        } else {
            logger.info("运营商监控,预警定时任务执行jobTime={},发送邮件开关已关闭", MonitorDateUtils.format(jobTime));

        }
        if (StringUtils.equalsIgnoreCase(weChatSwitch, "on")) {
            String weChatBody = generateWeChatBody(msgList, dataTime, baseTitle);
            alarmMessageProducer.sendWebChart4OperatorMonitor(weChatBody, jobTime);
        } else {
            logger.info("运营商监控,预警定时任务执行jobTime={},发送微信开关已关闭", MonitorDateUtils.format(jobTime));
        }
    }

    private String generateMailDataBody(List<OperatorStatAccessAlarmMsgDTO> msgList, Date jobTime, String baseTitle) {
        Integer intervalMins = diamondConfig.getOperatorMonitorIntervalMinutes();
        StringBuffer buffer = new StringBuffer();
        buffer.append("<br>").append("您好，").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append(baseTitle)
                .append("预警,在")
                .append(MonitorDateUtils.format(MonitorDateUtils.getIntervalTime(jobTime, intervalMins)))
                .append("--")
                .append(MonitorDateUtils.format(MonitorDateUtils.getIntervalTime(DateUtils.addMinutes(jobTime, intervalMins), intervalMins)))
                .append("时段数据存在问题").append("，此时监控数据如下，请及时处理：").append("</br>");
        buffer.append("<table border=\"1\" cellspacing=\"0\" bordercolor=\"#BDBDBD\" >");
        buffer.append("<tr bgcolor=\"#C9C9C9\">")
                .append("<th>").append("运营商").append("</th>")
                .append("<th>").append("预警描述").append("</th>")
                .append("<th>").append("当前指标值").append("</th>")
                .append("<th>").append("指标阀值").append("</th>")
                .append("<th>").append("偏离阀值程度").append("</th>")
                .append("</tr>");
        for (OperatorStatAccessAlarmMsgDTO msg : msgList) {
            buffer.append("<tr>").append("<td>").append(msg.getGroupName()).append("</td>")
                    .append("<td>").append(msg.getAlarmDesc()).append(" ").append("</td>")
                    .append("<td>").append(msg.getValueDesc()).append(" ").append("</td>")
                    .append("<td>").append(msg.getThresholdDesc()).append(" ").append("</td>")
                    .append("<td>").append(msg.getOffset()).append("%").append(" ").append("</td>").append("</tr>");

        }
        buffer.append("</table>");
        return buffer.toString();
    }

    private String generateTitle(String baseTitle) {
        return "saas-" + diamondConfig.getMonitorEnvironment() + baseTitle + "发生预警";
    }

    private String generateWeChatBody(List<OperatorStatAccessAlarmMsgDTO> msgList, Date dataTime, String baseTitle) {
        Integer intervalMins = diamondConfig.getOperatorMonitorIntervalMinutes();
        StringBuffer buffer = new StringBuffer();
        buffer.append("您好，").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append(baseTitle)
                .append("预警,在")
                .append(MonitorDateUtils.format(MonitorDateUtils.getIntervalTime(dataTime, intervalMins)))
                .append("--")
                .append(MonitorDateUtils.format(MonitorDateUtils.getIntervalTime(DateUtils.addMinutes(dataTime, intervalMins), intervalMins)))
                .append("时段数据存在问题").append("，此时监控数据如下，请及时处理：").append("\n");
        for (OperatorStatAccessAlarmMsgDTO msg : msgList) {
            buffer.append("【").append(msg.getGroupName()).append("】")
                    .append("【").append(msg.getAlarmSimpleDesc()).append("】")
                    .append("当前指标值:").append("【").append(msg.getValueDesc()).append("】")
                    .append("指标阀值:").append("【").append(msg.getThresholdDesc()).append("】")
                    .append("偏离阀值程度:").append("【").append(msg.getOffset()).append("%").append("】")
                    .append("\n");
        }
        return buffer.toString();
    }


    /**
     * 获取前7天内,相同时刻运营商统计的平均值(登录转化率平均值,抓取成功率平均值,洗数成功率平均值)
     *
     * @param now
     * @param dataTime
     * @param dtoList
     * @param statType
     * @return
     */
    private Map<String, OperatorStatAccessDTO> getPreviousCompareDataMap(Date now, Date dataTime,
                                                                         List<OperatorStatAccessDTO> dtoList,
                                                                         ETaskOperatorStatType statType) {
        List<String> groupCodeList = dtoList.stream().map(OperatorStatAccessDTO::getGroupCode).collect(Collectors.toList());
        Integer previousDays;
        if (ETaskOperatorStatType.TASK.equals(statType)) {
            previousDays = diamondConfig.getOperatorAlarmPreviousDays();
        } else {
            previousDays = diamondConfig.getOperatorAlarmUserPreviousDays();
        }
        List<Date> previousOClockList = MonitorDateUtils.getPreviousOClockTime(MonitorDateUtils.getIntervalTime(dataTime, diamondConfig.getOperatorMonitorIntervalMinutes()), previousDays);
        OperatorStatAccessCriteria previousCriteria = new OperatorStatAccessCriteria();
        previousCriteria.createCriteria().andDataTypeEqualTo(statType.getCode())
                .andDataTimeIn(previousOClockList)
                .andGroupCodeIn(groupCodeList);
        List<OperatorStatAccess> previousList = operatorStatAccessMapper.selectByExample(previousCriteria);
        if (CollectionUtils.isEmpty(previousList)) {
            logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},在此时间前{}天内,未查询到区分运营商统计数据groupCodeList={},previousOClockList={},list={}",
                    MonitorDateUtils.format(now), MonitorDateUtils.format(dataTime), previousDays, JSON.toJSONString(groupCodeList),
                    JSON.toJSONString(previousOClockList), JSON.toJSONString(previousList));
            return Maps.newHashMap();
        }
        List<OperatorStatAccessDTO> previousDTOList = DataConverterUtils.convert(previousList, OperatorStatAccessDTO.class);
        //<groupCode,List<OperatorStatAccessDTO>>
        Map<String, List<OperatorStatAccessDTO>> previousMap = previousDTOList.stream().collect(Collectors.groupingBy(OperatorStatAccessDTO::getGroupCode));
        Map<String, OperatorStatAccessDTO> compareMap = Maps.newHashMap();
        for (Map.Entry<String, List<OperatorStatAccessDTO>> entry : previousMap.entrySet()) {
            List<OperatorStatAccessDTO> entryList = entry.getValue();
            if (CollectionUtils.isEmpty(entryList)) {
                continue;
            }
            //如果列表数量大于1,则去掉相同时段最低的数据,再取平均值,排除数据异常情况.
            if (entryList.size() > 1) {
                entryList = entryList.stream()
                        .sorted((o1, o2) -> o2.getConfirmMobileCount().compareTo(o1.getConfirmMobileCount()))
                        .collect(Collectors.toList());
                entryList.remove(entryList.size() - 1);
            }
            BigDecimal previousLoginConversionRateCount = BigDecimal.ZERO;
            BigDecimal previousLoginSuccessRateCount = BigDecimal.ZERO;
            BigDecimal previousCrawlSuccessRateCount = BigDecimal.ZERO;
            BigDecimal previousProcessSuccessRateCount = BigDecimal.ZERO;
            Integer previousConfirmMobileCount = 0, previousStartLoginCount = 0, previousLoginSuccessCount = 0,
                    previousCrawlSuccessCount = 0, previousProcessSuccessCount = 0;
            for (OperatorStatAccessDTO dto : entryList) {
                previousLoginConversionRateCount = previousLoginConversionRateCount.add(dto.getLoginConversionRate());
                previousLoginSuccessRateCount = previousLoginSuccessRateCount.add(dto.getLoginSuccessRate());
                previousCrawlSuccessRateCount = previousCrawlSuccessRateCount.add(dto.getCrawlSuccessRate());
                previousProcessSuccessRateCount = previousProcessSuccessRateCount.add(dto.getProcessSuccessRate());
                previousConfirmMobileCount = previousConfirmMobileCount + dto.getConfirmMobileCount();
                previousStartLoginCount = previousStartLoginCount + dto.getStartLoginCount();
                previousLoginSuccessCount = previousLoginSuccessCount + dto.getLoginSuccessCount();
                previousCrawlSuccessCount = previousCrawlSuccessCount + dto.getCrawlSuccessCount();
                previousProcessSuccessCount = previousProcessSuccessCount + dto.getProcessSuccessCount();
            }
            OperatorStatAccessDTO compareDto = new OperatorStatAccessDTO();
            compareDto.setGroupCode(entry.getKey());
            compareDto.setPreviousLoginConversionRate(previousLoginConversionRateCount.divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousLoginSuccessRate(previousLoginSuccessRateCount.divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousCrawlSuccessRate(previousCrawlSuccessRateCount.divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousProcessSuccessRate(previousProcessSuccessRateCount.divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousConfirmMobileAvgCount(previousConfirmMobileCount / entryList.size());
            compareDto.setPreviousStartLoginAvgCount(previousStartLoginCount / entryList.size());
            compareDto.setPreviousLoginSuccessAvgCount(previousLoginSuccessCount / entryList.size());
            compareDto.setPreviousCrawlSuccessAvgCount(previousCrawlSuccessCount / entryList.size());
            compareDto.setPreviousProcessSuccessAvgCount(previousProcessSuccessCount / entryList.size());
            compareMap.put(entry.getKey(), compareDto);
        }
        return compareMap;
    }

    /**
     * 获取需要预警的数据信息
     *
     * @param now
     * @param dataTime
     * @param dtoList
     * @param compareMap
     * @return
     */
    private List<OperatorStatAccessAlarmMsgDTO> getAlarmMsgList(Date now, Date dataTime,
                                                                List<OperatorStatAccessDTO> dtoList,
                                                                Map<String, OperatorStatAccessDTO> compareMap,
                                                                ETaskOperatorStatType statType) {
        List<OperatorStatAccessAlarmMsgDTO> msgList = Lists.newArrayList();
        Integer previousDays;
        Integer threshold;
        if (ETaskOperatorStatType.TASK.equals(statType)) {
            previousDays = diamondConfig.getOperatorAlarmPreviousDays();
            threshold = diamondConfig.getOperatorAlarmThresholdPercent();
        } else {
            previousDays = diamondConfig.getOperatorAlarmUserPreviousDays();
            threshold = diamondConfig.getOperatorAlarmUserThresholdPercent();
        }
        for (OperatorStatAccessDTO dto : dtoList) {
            if (compareMap.get(dto.getGroupCode()) == null) {
                logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},groupCode={}的运营商前{}天未查询到统计数据",
                        MonitorDateUtils.format(now), MonitorDateUtils.format(dataTime), dto.getGroupCode(), previousDays);
                continue;
            }
            OperatorStatAccessDTO compareDTO = compareMap.get(dto.getGroupCode());
            BigDecimal loginConversionCompareVal = compareDTO.getPreviousLoginConversionRate().multiply(new BigDecimal(threshold)).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP);
            BigDecimal loginSuccessCompareVal = compareDTO.getPreviousLoginSuccessRate().multiply(new BigDecimal(threshold)).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP);
            BigDecimal crawlCompareVal = compareDTO.getPreviousCrawlSuccessRate().multiply(new BigDecimal(threshold)).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP);
            BigDecimal processCompareVal = compareDTO.getPreviousProcessSuccessRate().multiply(new BigDecimal(threshold)).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP);
            if (dto.getLoginConversionRate().compareTo(loginConversionCompareVal) < 0) {//登录转化率小于前7天平均值
                OperatorStatAccessAlarmMsgDTO msg = new OperatorStatAccessAlarmMsgDTO();
                msg.setGroupCode(dto.getGroupCode());
                msg.setGroupName(dto.getGroupName());
                msg.setAlarmDesc("登陆转化率低于前" + previousDays + "天平均值的" + threshold + "%");
                msg.setAlarmSimpleDesc("开始登陆");
                msg.setValue(dto.getLoginConversionRate());
                msg.setThreshold(loginConversionCompareVal);
                String valueDesc = new StringBuilder()
                        .append(dto.getLoginConversionRate()).append("%").append(" ").append("(")
                        .append(dto.getStartLoginCount()).append("/")
                        .append(dto.getConfirmMobileCount()).append(")").toString();
                msg.setValueDesc(valueDesc);
                String thresholdDesc = new StringBuilder()
                        .append(loginConversionCompareVal).append("%").append(" ").append("(")
                        .append(compareDTO.getPreviousStartLoginAvgCount()).append("/")
                        .append(compareDTO.getPreviousConfirmMobileAvgCount()).append("*")
                        .append(new BigDecimal(threshold).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
                msg.setThresholdDesc(thresholdDesc);
                if (BigDecimal.ZERO.compareTo(loginConversionCompareVal) == 0) {
                    msg.setOffset(BigDecimal.ZERO);
                } else {
                    BigDecimal value = BigDecimal.ONE.subtract(dto.getLoginConversionRate().divide(loginConversionCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                    msg.setOffset(value);
                }
                msgList.add(msg);
            }
            if (dto.getLoginSuccessRate().compareTo(loginSuccessCompareVal) < 0) {//登录成功率小于前7天平均值
                OperatorStatAccessAlarmMsgDTO msg = new OperatorStatAccessAlarmMsgDTO();
                msg.setGroupCode(dto.getGroupCode());
                msg.setGroupName(dto.getGroupName());
                msg.setAlarmDesc("登陆成功率低于前" + previousDays + "天平均值的" + threshold + "%");
                msg.setAlarmSimpleDesc("登陆");
                msg.setValue(dto.getLoginSuccessRate());
                msg.setThreshold(loginSuccessCompareVal);
                String valueDesc = new StringBuilder()
                        .append(dto.getLoginSuccessRate()).append("%").append(" ").append("(")
                        .append(dto.getLoginSuccessCount()).append("/")
                        .append(dto.getStartLoginCount()).append(")").toString();
                msg.setValueDesc(valueDesc);
                String thresholdDesc = new StringBuilder()
                        .append(loginSuccessCompareVal).append("%").append(" ").append("(")
                        .append(compareDTO.getPreviousLoginSuccessAvgCount()).append("/")
                        .append(compareDTO.getPreviousStartLoginAvgCount()).append("*")
                        .append(new BigDecimal(threshold).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
                msg.setThresholdDesc(thresholdDesc);
                if (BigDecimal.ZERO.compareTo(loginSuccessCompareVal) == 0) {
                    msg.setOffset(BigDecimal.ZERO);
                } else {
                    BigDecimal value = BigDecimal.ONE.subtract(dto.getLoginSuccessRate().divide(loginSuccessCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                    msg.setOffset(value);
                }
                msgList.add(msg);
            }
            if (dto.getCrawlSuccessRate().compareTo(crawlCompareVal) < 0) {//抓取成功率小于前7天平均值
                OperatorStatAccessAlarmMsgDTO msg = new OperatorStatAccessAlarmMsgDTO();
                msg.setGroupCode(dto.getGroupCode());
                msg.setGroupName(dto.getGroupName());
                msg.setAlarmDesc("抓取成功率低于前" + previousDays + "天平均值的" + threshold + "%");
                msg.setAlarmSimpleDesc("抓取");
                msg.setValue(dto.getCrawlSuccessRate());
                msg.setThreshold(crawlCompareVal);
                String valueDesc = new StringBuilder()
                        .append(dto.getCrawlSuccessRate()).append("%").append(" ").append("(")
                        .append(dto.getCrawlSuccessCount()).append("/")
                        .append(dto.getLoginSuccessCount()).append(")").toString();
                msg.setValueDesc(valueDesc);
                String thresholdDesc = new StringBuilder()
                        .append(crawlCompareVal).append("%").append(" ").append("(")
                        .append(compareDTO.getPreviousCrawlSuccessAvgCount()).append("/")
                        .append(compareDTO.getPreviousLoginSuccessAvgCount()).append("*")
                        .append(new BigDecimal(threshold).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
                msg.setThresholdDesc(thresholdDesc);

                if (BigDecimal.ZERO.compareTo(crawlCompareVal) == 0) {
                    msg.setOffset(BigDecimal.ZERO);
                } else {
                    BigDecimal value = BigDecimal.ONE.subtract(dto.getCrawlSuccessRate().divide(crawlCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                    msg.setOffset(value);
                }
                msgList.add(msg);
            }
            if (dto.getProcessSuccessRate().compareTo(processCompareVal) < 0) {//洗数成功率小于前7天平均值
                OperatorStatAccessAlarmMsgDTO msg = new OperatorStatAccessAlarmMsgDTO();
                msg.setGroupCode(dto.getGroupCode());
                msg.setGroupName(dto.getGroupName());
                msg.setAlarmDesc("洗数成功率低于前" + previousDays + "天平均值的" + threshold + "%");
                msg.setAlarmSimpleDesc("洗数");
                msg.setValue(dto.getProcessSuccessRate());
                msg.setThreshold(processCompareVal);
                String valueDesc = new StringBuilder()
                        .append(dto.getProcessSuccessRate()).append("%").append(" ").append("(")
                        .append(dto.getProcessSuccessCount()).append("/")
                        .append(dto.getCrawlSuccessCount()).append(")").toString();
                msg.setValueDesc(valueDesc);
                String thresholdDesc = new StringBuilder()
                        .append(processCompareVal).append("%").append(" ").append("(")
                        .append(compareDTO.getPreviousProcessSuccessAvgCount()).append("/")
                        .append(compareDTO.getPreviousCrawlSuccessAvgCount()).append("*")
                        .append(new BigDecimal(threshold).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
                msg.setThresholdDesc(thresholdDesc);

                if (BigDecimal.ZERO.compareTo(processCompareVal) == 0) {
                    msg.setOffset(BigDecimal.ZERO);
                } else {
                    BigDecimal value = BigDecimal.ONE.subtract(dto.getProcessSuccessRate().divide(processCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                    msg.setOffset(value);
                }
                msgList.add(msg);
            }

        }
        msgList = msgList.stream().sorted((o1, o2) -> o1.getGroupName().compareTo(o2.getGroupName())).collect(Collectors.toList());
        return msgList;
    }
}
