package com.treefinance.saas.monitor.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.datatrees.notify.async.body.mail.MailEnum;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.biz.service.EcommerceMonitorAllAlarmService;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.domain.dto.EcommerceAllStatAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.EcommerceMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.domain.dto.TaskStatAccessAlarmMsgDTO;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatAccess;
import com.treefinance.saas.monitor.dao.entity.EcommerceAllStatAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.EcommerceAllStatAccessMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by haojiahong on 2018/1/17.
 */
@Service
public class EcommerceMonitorAllAlarmServiceImpl implements EcommerceMonitorAllAlarmService {

    private static final Logger logger = LoggerFactory.getLogger(EcommerceMonitorAllAlarmService.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private EcommerceAllStatAccessMapper ecommerceAllStatAccessMapper;
    @Autowired
    private AlarmMessageProducer alarmMessageProducer;
    @Autowired
    private DiamondConfig diamondConfig;

    @Override
    public void alarm(Date jobTime, EcommerceMonitorAlarmConfigDTO config, ETaskStatDataType statType) {
        try {
            Integer intervalMins = config.getIntervalMins();
            //由于任务执行需要时间,保证预警的精确,预警统计向前一段时间(各业务任务的超时时间),此时此段时间的任务可以保证都已统计完毕.
            //好处:预警时间即使每隔1分钟预警,依然可以保证预警的准确.坏处:收到预警消息时间向后延迟了相应时间.
            //如:jobTime=14:11,但是电商超时时间为600s,则statTime=14:01
            Date statTime = DateUtils.addSeconds(jobTime, -2);
            //取得预警原点时间,如:statTime=14:01分,30分钟间隔统计一次,则beginTime为14:00.
            Date baseTime = TaskOperatorMonitorKeyHelper.getRedisStatDateTime(statTime, intervalMins);

            //判断此时刻是否预警预警过
            String alarmTimeKey = EcommerceAlarmKeyHelper.strKeyOfAlarmTimeLog(baseTime, config.getAlarmType(), statType);
            if (stringRedisTemplate.hasKey(alarmTimeKey)) {
                logger.info("电商预警,预警定时任务执行jobTime={},baseTime={},statType={},alarmType={}已预警,不再预警",
                        MonitorDateUtils.format(baseTime), JSON.toJSONString(statType), config.getAlarmType());
                return;
            }
            stringRedisTemplate.opsForValue().set(alarmTimeKey, "1");
            stringRedisTemplate.expire(alarmTimeKey, 2, TimeUnit.HOURS);

            //获取基础数据
            Date startTime = DateUtils.addMinutes(baseTime, -intervalMins);
            Date endTime = baseTime;
            EcommerceAllStatAccessDTO dataDTO = this.getBaseData(jobTime, startTime, endTime, config, statType);
            if (dataDTO == null) {
                logger.info("电商预警,预警定时任务执行jobTime={},要统计的数据时刻startTime={},endTime={},此段时间内,未查询到所有电商的统计数据",
                        MonitorDateUtils.format(jobTime), MonitorDateUtils.format(startTime), MonitorDateUtils.format(endTime));
                return;
            }

            //获取前n天内,相同时刻电商统计的平均值(登录转化率平均值,抓取成功率平均值,洗数成功率平均值)
            EcommerceAllStatAccessDTO compareDTO = getPreviousCompareData(jobTime, baseTime, config, statType);
            logger.info("电商预警,预警定时任务执行jobTime={},要统计的数据时刻baseTime={},获取前n天内,相同时刻所有电商统计的平均值compareDTO={}",
                    MonitorDateUtils.format(jobTime), MonitorDateUtils.format(baseTime), JSON.toJSONString(compareDTO));
            if (compareDTO == null) {
                return;
            }

            //获取需要预警的数据信息
            List<TaskStatAccessAlarmMsgDTO> msgList = getAlarmMsgList(dataDTO, compareDTO, config);
            logger.info("电商预警,预警定时任务执行jobTime={},要统计的数据时刻baseTime={},所有电商统计需要预警的数据信息msgList={}",
                    MonitorDateUtils.format(jobTime), MonitorDateUtils.format(baseTime), JSON.toJSONString(msgList));
            if (CollectionUtils.isEmpty(msgList)) {
                return;
            }
            //发送预警
            alarmMsg(msgList, jobTime, startTime, endTime, config, statType);

        } catch (Exception e) {
            logger.error("电商预警,预警定时任务执行jobTime={}异常", MonitorDateUtils.format(jobTime), e);
        }

    }

    private void alarmMsg(List<TaskStatAccessAlarmMsgDTO> msgList,
                          Date jobTime,
                          Date startTime,
                          Date endTime,
                          EcommerceMonitorAlarmConfigDTO config,
                          ETaskStatDataType statType) {
        String baseTile;
        String mailSwitch = config.getMailAlarmSwitch();
        String weChatSwitch = config.getWeChatAlarmSwitch();


        String boundStr = diamondConfig.getEcommerceMonitorAlarmBounds();

        String[] bounds = boundStr.split(",");

        BigDecimal warning = new BigDecimal(Integer.valueOf(bounds[0]));
        BigDecimal info = new BigDecimal(Integer.valueOf(bounds[1]));


        if (ETaskStatDataType.TASK.equals(statType)) {
            baseTile = "【总】电商监控(按任务数统计)";
        } else {
            baseTile = "【总】电商监控(按人数统计)";
        }
        EAlarmLevel alarmLevel = null;
        for (TaskStatAccessAlarmMsgDTO msgDTO:msgList){
            if(msgDTO.getOffset().compareTo(info)<0){
                if(alarmLevel == null){
                    alarmLevel = EAlarmLevel.info;
                }
            }else if(msgDTO.getOffset().compareTo(warning)< 0){
                if(alarmLevel == null || alarmLevel.equals(EAlarmLevel.info)){
                    alarmLevel = EAlarmLevel.warning;
                }
            }else {
                alarmLevel = EAlarmLevel.error;
            }
        }
        if(alarmLevel == null){
            logger.info("预警等级为空，需要预警的数据：{}",msgList);
            return;
        }

        if(EAlarmLevel.info.equals(alarmLevel)){
            sendWechat(alarmLevel,msgList,jobTime,startTime,endTime,baseTile,weChatSwitch);
        }else {
            sendWechat(alarmLevel,msgList,jobTime,startTime,endTime,baseTile,weChatSwitch);
            sendEmail(alarmLevel,msgList,jobTime,startTime,endTime,baseTile,mailSwitch);
        }
    }

    private void sendEmail(EAlarmLevel alarmLevel,List<TaskStatAccessAlarmMsgDTO> msgList, Date jobTime, Date startTime, Date endTime, String baseTile, String mailSwitch) {
        if (StringUtils.equalsIgnoreCase(mailSwitch, AlarmConstants.SWITCH_ON)) {
            String mailDataBody = generateMailDataBody(alarmLevel,msgList, startTime, endTime, baseTile);
            String title = generateTitle(baseTile);
            alarmMessageProducer.sendMail(title, mailDataBody, MailEnum.HTML_MAIL);
        } else {
            logger.info("电商预警,预警定时任务执行jobTime={},发送邮件开关已关闭", MonitorDateUtils.format(jobTime));
        }
    }

    private void sendWechat(EAlarmLevel alarmLevel,List<TaskStatAccessAlarmMsgDTO> msgList, Date jobTime, Date startTime, Date endTime, String baseTile, String weChatSwitch) {
        if (StringUtils.equalsIgnoreCase(weChatSwitch, AlarmConstants.SWITCH_ON)) {
            String weChatBody = generateWeChatBody(alarmLevel,msgList, startTime, endTime, baseTile);
            alarmMessageProducer.sendWebChart(weChatBody);
        } else {
            logger.info("电商预警,预警定时任务执行jobTime={},发送微信开关已关闭", MonitorDateUtils.format(jobTime));
        }
    }


    private String generateTitle(String baseTile) {
        return "saas-" + diamondConfig.getMonitorEnvironment() + baseTile + "发生预警";
    }

    private String generateMailDataBody(EAlarmLevel alarmLevel,List<TaskStatAccessAlarmMsgDTO> msgList, Date
            startTime, Date
            endTime,
                                        String baseTile) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<br>").append("【").append(alarmLevel).append("】").append("您好，").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append(baseTile)
                .append("预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段数据存在问题").append("，此时监控数据如下，请及时处理：").append("</br>");
        buffer.append("<table border=\"1\" cellspacing=\"0\" bordercolor=\"#BDBDBD\">");
        buffer.append("<tr bgcolor=\"#C9C9C9\">")
                .append("<th>").append("预警描述").append("</th>")
                .append("<th>").append("当前指标值").append("</th>")
                .append("<th>").append("指标阀值").append("</th>")
                .append("<th>").append("偏离阀值程度").append("</th>")
                .append("</tr>");
        for (TaskStatAccessAlarmMsgDTO msg : msgList) {
            buffer.append("<tr>")
                    .append("<td>").append(msg.getAlarmDesc()).append(" ").append("</td>")
                    .append("<td>").append(msg.getValueDesc()).append(" ").append("</td>")
                    .append("<td>").append(msg.getThresholdDesc()).append(" ").append("</td>")
                    .append("<td>").append(msg.getOffset()).append("%").append(" ").append("</td>").append("</tr>");

        }
        buffer.append("</table>");
        return buffer.toString();
    }

    private String generateWeChatBody(EAlarmLevel alarmLevel,List<TaskStatAccessAlarmMsgDTO> msgList, Date startTime,
                                      Date
            endTime, String
            baseTile) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("【").append(alarmLevel).append("】")
                .append("您好，").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append(baseTile)
                .append("预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段数据存在问题").append("，此时监控数据如下，请及时处理：").append("\n");
        for (TaskStatAccessAlarmMsgDTO msg : msgList) {
            buffer.append("【").append(msg.getAlarmSimpleDesc()).append("】")
                    .append("当前指标值:").append("【").append(msg.getValueDesc()).append("】")
                    .append("指标阀值:").append("【").append(msg.getThresholdDesc()).append("】")
                    .append("偏离阀值程度:").append("【").append(msg.getOffset()).append("】")
                    .append("\n");
        }
        return buffer.toString();
    }

    private List<TaskStatAccessAlarmMsgDTO> getAlarmMsgList(EcommerceAllStatAccessDTO dataDTO,
                                                            EcommerceAllStatAccessDTO compareDTO,
                                                            EcommerceMonitorAlarmConfigDTO config) {

        List<TaskStatAccessAlarmMsgDTO> msgList = Lists.newArrayList();
        Integer previousDays = config.getPreviousDays();

        BigDecimal loginConversionCompareVal = compareDTO.getPreviousLoginConversionRate().multiply(new BigDecimal(config.getLoginConversionRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal loginSuccessCompareVal = compareDTO.getPreviousLoginSuccessRate().multiply(new BigDecimal(config.getLoginSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal crawlCompareVal = compareDTO.getPreviousCrawlSuccessRate().multiply(new BigDecimal(config.getCrawlSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal processCompareVal = compareDTO.getPreviousProcessSuccessRate().multiply(new BigDecimal(config.getProcessSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal callbackCompareVal = compareDTO.getPreviousCallbackSuccessRate().multiply(new BigDecimal(config.getCallbackSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal wholeConversionCompareVal = compareDTO.getPreviousWholeConversionRate().multiply(new BigDecimal(config.getWholeConversionRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);

        if (dataDTO.getLoginConversionRate().compareTo(loginConversionCompareVal) < 0) {//登录转化率小于前7天平均值
            TaskStatAccessAlarmMsgDTO msg = new TaskStatAccessAlarmMsgDTO();
            msg.setAlarmDesc("登陆转化率低于前" + previousDays + "天平均值的" + config.getLoginConversionRate() + "%");
            msg.setAlarmSimpleDesc("开始登陆");
            msg.setValue(dataDTO.getLoginConversionRate());
            msg.setThreshold(loginConversionCompareVal);
            String valueDesc = new StringBuilder()
                    .append(dataDTO.getLoginConversionRate()).append("%").append(" ").append("(")
                    .append(dataDTO.getLoginSuccessCount()).append("/")
                    .append(dataDTO.getEntryCount()).append(")").toString();
            msg.setValueDesc(valueDesc);
            String thresholdDesc = new StringBuilder()
                    .append(loginConversionCompareVal).append("%").append(" ").append("(")
                    .append(compareDTO.getPreviousStartLoginAvgCount()).append("/")
                    .append(compareDTO.getPreviousEntryAvgCount()).append("*")
                    .append(new BigDecimal(config.getLoginConversionRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
            msg.setThresholdDesc(thresholdDesc);

            if (BigDecimal.ZERO.compareTo(loginConversionCompareVal) == 0) {
                msg.setOffset(BigDecimal.ZERO);
            } else {
                BigDecimal value = BigDecimal.ONE.subtract(dataDTO.getLoginConversionRate().divide(loginConversionCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));

                msg.setOffset(value);
            }
            msgList.add(msg);
        }
        if (dataDTO.getLoginSuccessRate().compareTo(loginSuccessCompareVal) < 0) {//登录成功率小于前7天平均值
            TaskStatAccessAlarmMsgDTO msg = new TaskStatAccessAlarmMsgDTO();
            msg.setAlarmDesc("登陆成功率低于前" + previousDays + "天平均值的" + config.getLoginSuccessRate() + "%");
            msg.setAlarmSimpleDesc("登陆");
            msg.setValue(dataDTO.getLoginSuccessRate());
            msg.setThreshold(loginSuccessCompareVal);
            String valueDesc = new StringBuilder()
                    .append(dataDTO.getLoginSuccessRate()).append("%").append(" ").append("(")
                    .append(dataDTO.getLoginSuccessCount()).append("/")
                    .append(dataDTO.getStartLoginCount()).append(")").toString();
            msg.setValueDesc(valueDesc);
            String thresholdDesc = new StringBuilder()
                    .append(loginSuccessCompareVal).append("%").append(" ").append("(")
                    .append(compareDTO.getPreviousLoginSuccessAvgCount()).append("/")
                    .append(compareDTO.getPreviousStartLoginAvgCount()).append("*")
                    .append(new BigDecimal(config.getLoginSuccessRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
            msg.setThresholdDesc(thresholdDesc);
            if (BigDecimal.ZERO.compareTo(loginSuccessCompareVal) == 0) {
                msg.setOffset(BigDecimal.ZERO);
            } else {
                BigDecimal value = BigDecimal.ONE.subtract(dataDTO.getLoginSuccessRate().divide(loginSuccessCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                msg.setOffset(value);
            }
            msgList.add(msg);
        }
        if (dataDTO.getCrawlSuccessRate().compareTo(crawlCompareVal) < 0) {//抓取成功率小于前7天平均值
            TaskStatAccessAlarmMsgDTO msg = new TaskStatAccessAlarmMsgDTO();
            msg.setAlarmDesc("抓取成功率低于前" + previousDays + "天平均值的" + config.getCrawlSuccessRate() + "%");
            msg.setAlarmSimpleDesc("抓取");
            msg.setValue(dataDTO.getCrawlSuccessRate());
            msg.setThreshold(crawlCompareVal);
            String valueDesc = new StringBuilder()
                    .append(dataDTO.getCrawlSuccessRate()).append("%").append(" ").append("(")
                    .append(dataDTO.getCrawlSuccessCount()).append("/")
                    .append(dataDTO.getLoginSuccessCount()).append(")").toString();
            msg.setValueDesc(valueDesc);
            String thresholdDesc = new StringBuilder()
                    .append(crawlCompareVal).append("%").append(" ").append("(")
                    .append(compareDTO.getPreviousCrawlSuccessAvgCount()).append("/")
                    .append(compareDTO.getPreviousLoginSuccessAvgCount()).append("*")
                    .append(new BigDecimal(config.getCrawlSuccessRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
            msg.setThresholdDesc(thresholdDesc);
            if (BigDecimal.ZERO.compareTo(crawlCompareVal) == 0) {
                msg.setOffset(BigDecimal.ZERO);
            } else {
                BigDecimal value = BigDecimal.ONE.subtract(dataDTO.getCrawlSuccessRate().divide(crawlCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                msg.setOffset(value);
            }
            msgList.add(msg);
        }
        if (dataDTO.getProcessSuccessRate().compareTo(processCompareVal) < 0) {//洗数成功率小于前7天平均值
            TaskStatAccessAlarmMsgDTO msg = new TaskStatAccessAlarmMsgDTO();
            msg.setAlarmDesc("洗数成功率低于前" + previousDays + "天平均值的" + config.getProcessSuccessRate() + "%");
            msg.setAlarmSimpleDesc("洗数");
            msg.setValue(dataDTO.getProcessSuccessRate());
            msg.setThreshold(processCompareVal);
            String valueDesc = new StringBuilder()
                    .append(dataDTO.getProcessSuccessRate()).append("%").append(" ").append("(")
                    .append(dataDTO.getProcessSuccessCount()).append("/")
                    .append(dataDTO.getCrawlSuccessCount()).append(")").toString();
            msg.setValueDesc(valueDesc);
            String thresholdDesc = new StringBuilder()
                    .append(processCompareVal).append("%").append(" ").append("(")
                    .append(compareDTO.getPreviousProcessSuccessAvgCount()).append("/")
                    .append(compareDTO.getPreviousCrawlSuccessAvgCount()).append("*")
                    .append(new BigDecimal(config.getProcessSuccessRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
            msg.setThresholdDesc(thresholdDesc);
            if (BigDecimal.ZERO.compareTo(processCompareVal) == 0) {
                msg.setOffset(BigDecimal.ZERO);
            } else {
                BigDecimal value = BigDecimal.ONE.subtract(dataDTO.getProcessSuccessRate().divide(processCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                msg.setOffset(value);
            }
            msgList.add(msg);
        }
        if (dataDTO.getCallbackSuccessRate().compareTo(callbackCompareVal) < 0) {//回调成功率小于前7天平均值
            TaskStatAccessAlarmMsgDTO msg = new TaskStatAccessAlarmMsgDTO();
            msg.setAlarmDesc("回调成功率低于前" + previousDays + "天平均值的" + config.getCallbackSuccessRate() + "%");
            msg.setAlarmSimpleDesc("回调");
            msg.setValue(dataDTO.getCallbackSuccessRate());
            msg.setThreshold(callbackCompareVal);
            String valueDesc = new StringBuilder()
                    .append(dataDTO.getCallbackSuccessRate()).append("%").append(" ").append("(")
                    .append(dataDTO.getCallbackSuccessCount()).append("/")
                    .append(dataDTO.getProcessSuccessCount()).append(")").toString();
            msg.setValueDesc(valueDesc);
            String thresholdDesc = new StringBuilder()
                    .append(callbackCompareVal).append("%").append(" ").append("(")
                    .append(compareDTO.getPreviousCallbackSuccessAvgCount()).append("/")
                    .append(compareDTO.getPreviousProcessSuccessAvgCount()).append("*")
                    .append(new BigDecimal(config.getCallbackSuccessRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
            msg.setThresholdDesc(thresholdDesc);
            if (BigDecimal.ZERO.compareTo(callbackCompareVal) == 0) {
                msg.setOffset(BigDecimal.ZERO);
            } else {
                BigDecimal value = BigDecimal.ONE.subtract(dataDTO.getCallbackSuccessRate().divide(callbackCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                msg.setOffset(value);
            }
            msgList.add(msg);

        }

        if (dataDTO.getWholeConversionRate().compareTo(wholeConversionCompareVal) < 0) {//总转化率小于前7天平均值
            TaskStatAccessAlarmMsgDTO msg = new TaskStatAccessAlarmMsgDTO();
            msg.setAlarmDesc("总转化率低于前" + previousDays + "天平均值的" + config.getWholeConversionRate() + "%");
            msg.setAlarmSimpleDesc("总转化率");
            msg.setValue(dataDTO.getWholeConversionRate());
            msg.setThreshold(wholeConversionCompareVal);
            String valueDesc = new StringBuilder()
                    .append(dataDTO.getWholeConversionRate()).append("%").append(" ").append("(")
                    .append(dataDTO.getCallbackSuccessCount()).append("/")
                    .append(dataDTO.getEntryCount()).append(")").toString();
            msg.setValueDesc(valueDesc);
            String thresholdDesc = new StringBuilder()
                    .append(wholeConversionCompareVal).append("%").append(" ").append("(")
                    .append(compareDTO.getPreviousCallbackSuccessAvgCount()).append("/")
                    .append(compareDTO.getPreviousEntryAvgCount()).append("*")
                    .append(new BigDecimal(config.getWholeConversionRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
            msg.setThresholdDesc(thresholdDesc);
            if (BigDecimal.ZERO.compareTo(wholeConversionCompareVal) == 0) {
                msg.setOffset(BigDecimal.ZERO);
            } else {
                BigDecimal value = BigDecimal.ONE.subtract(dataDTO.getWholeConversionRate().divide(wholeConversionCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                msg.setOffset(value);
            }
            msgList.add(msg);

        }

        return msgList;
    }

    private EcommerceAllStatAccessDTO getPreviousCompareData(Date jobTime,
                                                             Date baseTime,
                                                             EcommerceMonitorAlarmConfigDTO config,
                                                             ETaskStatDataType statType) {
        Integer previousDays = config.getPreviousDays();
        List<Date> previousOClockList = MonitorDateUtils.getPreviousOClockTime(baseTime, previousDays);

        List<EcommerceAllStatAccessDTO> previousDTOList = Lists.newArrayList();
        for (Date previousOClock : previousOClockList) {
            Date startTime = DateUtils.addMinutes(previousOClock, -config.getIntervalMins());
            Date endTime = previousOClock;
            EcommerceAllStatAccessDTO dto = this.getBaseData(jobTime, startTime, endTime, config, statType);
            if (dto != null) {
                previousDTOList.add(dto);
            }
        }
        if (CollectionUtils.isEmpty(previousDTOList)) {
            logger.info("电商预警,预警定时任务执行jobTime={},在此时间前{}天内,未查询到所有电商统计数据previousOClockList={},list={}",
                    MonitorDateUtils.format(jobTime), previousDays, JSON.toJSONString(previousOClockList), JSON.toJSONString(previousDTOList));
            return null;
        }

        //如果列表数量大于1,则去掉相同时段最低的数据,再取平均值,排除数据异常情况.
        if (previousDTOList.size() > 1) {
            previousDTOList = previousDTOList.stream()
                    .sorted((o1, o2) -> o2.getEntryCount().compareTo(o1.getEntryCount()))
                    .collect(Collectors.toList());
            previousDTOList.remove(previousDTOList.size() - 1);
        }
        BigDecimal previousLoginConversionRateCount = BigDecimal.ZERO;
        BigDecimal previousLoginSuccessRateCount = BigDecimal.ZERO;
        BigDecimal previousCrawlSuccessRateCount = BigDecimal.ZERO;
        BigDecimal previousProcessSuccessRateCount = BigDecimal.ZERO;
        BigDecimal previousCallbackSuccessRateCount = BigDecimal.ZERO;
        BigDecimal previousWholeConversionRateCount = BigDecimal.ZERO;
        Integer previousEntryCount = 0, previousStartLoginCount = 0, previousLoginSuccessCount = 0,
                previousCrawlSuccessCount = 0, previousProcessSuccessCount = 0, previousCallbackSuccessCount = 0;

        for (EcommerceAllStatAccessDTO previousDTO : previousDTOList) {
            previousLoginConversionRateCount = previousLoginConversionRateCount.add(previousDTO.getLoginConversionRate());
            previousLoginSuccessRateCount = previousLoginSuccessRateCount.add(previousDTO.getLoginSuccessRate());
            previousCrawlSuccessRateCount = previousCrawlSuccessRateCount.add(previousDTO.getCrawlSuccessRate());
            previousProcessSuccessRateCount = previousProcessSuccessRateCount.add(previousDTO.getProcessSuccessRate());
            previousCallbackSuccessRateCount = previousCallbackSuccessRateCount.add(previousDTO.getCallbackSuccessRate());
            previousWholeConversionRateCount = previousWholeConversionRateCount.add(previousDTO.getWholeConversionRate());

            previousEntryCount = previousEntryCount + previousDTO.getEntryCount();
            previousStartLoginCount = previousStartLoginCount + previousDTO.getStartLoginCount();
            previousLoginSuccessCount = previousLoginSuccessCount + previousDTO.getLoginSuccessCount();
            previousCrawlSuccessCount = previousCrawlSuccessCount + previousDTO.getCrawlSuccessCount();
            previousProcessSuccessCount = previousProcessSuccessCount + previousDTO.getProcessSuccessCount();
            previousCallbackSuccessCount = previousCallbackSuccessCount + previousDTO.getCallbackSuccessCount();
        }
        EcommerceAllStatAccessDTO compareDto = new EcommerceAllStatAccessDTO();
        BigDecimal size = BigDecimal.valueOf(previousDTOList.size());
        compareDto.setPreviousLoginConversionRate(this.calcRate(previousLoginConversionRateCount, size, 2));
        compareDto.setPreviousLoginSuccessRate(this.calcRate(previousLoginSuccessRateCount, size, 2));
        compareDto.setPreviousCrawlSuccessRate(this.calcRate(previousCrawlSuccessRateCount, size, 2));
        compareDto.setPreviousProcessSuccessRate(this.calcRate(previousProcessSuccessRateCount, size, 2));
        compareDto.setPreviousCallbackSuccessRate(this.calcRate(previousCallbackSuccessRateCount, size, 2));
        compareDto.setPreviousWholeConversionRate(this.calcRate(previousWholeConversionRateCount, size, 2));

        compareDto.setPreviousEntryAvgCount(this.calcRate(BigDecimal.valueOf(previousEntryCount), size, 1));
        compareDto.setPreviousStartLoginAvgCount(this.calcRate(BigDecimal.valueOf(previousStartLoginCount), size, 1));
        compareDto.setPreviousLoginSuccessAvgCount(this.calcRate(BigDecimal.valueOf(previousLoginSuccessCount), size, 1));
        compareDto.setPreviousCrawlSuccessAvgCount(this.calcRate(BigDecimal.valueOf(previousCrawlSuccessCount), size, 1));
        compareDto.setPreviousProcessSuccessAvgCount(this.calcRate(BigDecimal.valueOf(previousProcessSuccessCount), size, 1));
        compareDto.setPreviousCallbackSuccessAvgCount(this.calcRate(BigDecimal.valueOf(previousCallbackSuccessCount), size, 1));

        return compareDto;
    }

    private EcommerceAllStatAccessDTO getBaseData(Date jobTime,
                                                  Date startTime,
                                                  Date endTime,
                                                  EcommerceMonitorAlarmConfigDTO config,
                                                  ETaskStatDataType statType) {
        EcommerceAllStatAccessCriteria criteria = new EcommerceAllStatAccessCriteria();
        criteria.createCriteria().andDataTypeEqualTo(statType.getCode())
                .andAppIdEqualTo(config.getAppId())
                .andSourceTypeEqualTo(config.getSourceType().byteValue())
                .andDataTimeGreaterThanOrEqualTo(startTime)
                .andDataTimeLessThan(endTime);
        List<EcommerceAllStatAccess> list = ecommerceAllStatAccessMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            logger.info("电商预警,预警定时任务执行jobTime={},要统计的数据时刻startTime={},endTime={},此段时间内,未查询到所有电商的统计数据list={}",
                    MonitorDateUtils.format(jobTime), MonitorDateUtils.format(startTime), MonitorDateUtils.format(endTime), JSON.toJSONString(list));
            return null;
        }
        EcommerceAllStatAccess ecommerceAllStatAccess = list.get(0);
        EcommerceAllStatAccessDTO dataDTO = DataConverterUtils.convert(ecommerceAllStatAccess, EcommerceAllStatAccessDTO.class);
        int entryCount = 0, startLoginCount = 0, loginSuccessCount = 0,
                crawlSuccessCount = 0, processSuccessCount = 0, callbackSuccessCount = 0;
        for (EcommerceAllStatAccess item : list) {
            entryCount = entryCount + item.getEntryCount();
            startLoginCount = startLoginCount + item.getStartLoginCount();
            loginSuccessCount = loginSuccessCount + item.getLoginSuccessCount();
            crawlSuccessCount = crawlSuccessCount + item.getCrawlSuccessCount();
            processSuccessCount = processSuccessCount + item.getProcessSuccessCount();
            callbackSuccessCount = callbackSuccessCount + item.getCallbackSuccessCount();
        }
        dataDTO.setEntryCount(entryCount);
        dataDTO.setStartLoginCount(startLoginCount);
        dataDTO.setLoginSuccessCount(loginSuccessCount);
        dataDTO.setCrawlSuccessCount(crawlSuccessCount);
        dataDTO.setProcessSuccessCount(processSuccessCount);
        dataDTO.setCallbackSuccessCount(callbackSuccessCount);
        dataDTO.setLoginConversionRate(calcRate(startLoginCount, entryCount));
        dataDTO.setLoginSuccessRate(calcRate(loginSuccessCount, startLoginCount));
        dataDTO.setCrawlSuccessRate(calcRate(crawlSuccessCount, loginSuccessCount));
        dataDTO.setProcessSuccessRate(calcRate(processSuccessCount, crawlSuccessCount));
        dataDTO.setCallbackSuccessRate(calcRate(callbackSuccessCount, processSuccessCount));
        dataDTO.setWholeConversionRate(calcRate(callbackSuccessCount, entryCount));
        return dataDTO;
    }


    /**
     * 计算比率
     *
     * @param a 分子
     * @param b 分母
     * @return
     */
    private BigDecimal calcRate(Integer a, Integer b) {
        if (Integer.valueOf(0).compareTo(b) == 0) {
            return BigDecimal.valueOf(0, 2);
        }

        BigDecimal rate = BigDecimal.valueOf(a, 2)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(b, 2), 2, BigDecimal.ROUND_HALF_UP);
        return rate;
    }

    private BigDecimal calcRate(BigDecimal a, BigDecimal b, int scale) {
        BigDecimal rate = a.divide(b, scale, BigDecimal.ROUND_HALF_UP);
        return rate;
    }

    private static class EcommerceAlarmKeyHelper {
        private static final String KEY_PREFIX = "saas-monitor-task-ecommerce-monitor";
        private static final String KEY_ALARM_TIMES = "key-alarm-times";

        public static String strKeyOfAlarmTimeLog(Date baseTime, Integer alarmType, ETaskStatDataType statType) {
            String intervalDateStr = DateFormatUtils.format(baseTime, "yyyy-MM-dd");
            return Joiner.on(":").useForNull("null").join(KEY_PREFIX, KEY_ALARM_TIMES, intervalDateStr, alarmType, statType, MonitorDateUtils.format2Hms(baseTime));
        }
    }
}
