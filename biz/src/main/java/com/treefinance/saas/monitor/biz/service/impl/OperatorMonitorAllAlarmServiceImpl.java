package com.treefinance.saas.monitor.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.biz.service.OperatorMonitorAllAlarmService;
import com.treefinance.saas.monitor.common.domain.dto.OperatorAllStatAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorMonitorAlarmConfigDTO;
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
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Override
    public void alarm(Date jobTime, OperatorMonitorAlarmConfigDTO config, ETaskOperatorStatType statType) {
        try {
            Integer intervalMins = config.getIntervalMins();
            //由于任务执行需要时间,保证预警的精确,预警统计向前一段时间(各业务任务的超时时间),此时此段时间的任务可以保证都已统计完毕.
            //好处:预警时间即使每隔1分钟预警,依然可以保证预警的准确.坏处:收到预警消息时间向后延迟了相应时间.
            //如:jobTime=14:11,但是运营商超时时间为600s,则statTime=14:01
            Date statTime = DateUtils.addSeconds(jobTime, -config.getTaskTimeoutSecs());
            //取得预警原点时间,如:statTime=14:01分,30分钟间隔统计一次,则beginTime为14:00.统计的数据间隔[13:30-13:40;13:40-13:50;13:50-14:00]
            Date baseTime = TaskOperatorMonitorKeyHelper.getRedisStatDateTime(statTime, intervalMins);

            //判断此时刻是否预警预警过
            String alarmTimeKey = TaskOperatorMonitorKeyHelper.keyOfAlarmTimeLog(baseTime, config.getAlarmType(), statType);
            BoundSetOperations<String, Object> setOperations = redisTemplate.boundSetOps(alarmTimeKey);
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(alarmTimeKey))) {
                setOperations.expire(2, TimeUnit.DAYS);
            }
            if (setOperations.isMember(MonitorDateUtils.format(baseTime))) {
                logger.info("运营商监控,预警定时任务执行jobTime={},baseTime={},statType={},alarmType={}已预警,不再预警",
                        MonitorDateUtils.format(baseTime), JSON.toJSONString(statType), config.getAlarmType());
                return;
            }
            setOperations.add(MonitorDateUtils.format(baseTime));

            //获取基础数据
            Date startTime = DateUtils.addMinutes(baseTime, -intervalMins);
            Date endTime = baseTime;
            OperatorAllStatAccessDTO dataDTO = this.getBaseData(jobTime, startTime, endTime, config, statType);
            if (dataDTO == null) {
                logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻startTime={},endTime={},此段时间内,未查询到所有运营商的统计数据",
                        MonitorDateUtils.format(jobTime), MonitorDateUtils.format(startTime), MonitorDateUtils.format(endTime));
                return;
            }

            //获取前7天内,相同时刻运营商统计的平均值(登录转化率平均值,抓取成功率平均值,洗数成功率平均值)
            OperatorAllStatAccessDTO compareDTO = getPreviousCompareData(jobTime, baseTime, config, statType);
            logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻baseTime={},获取前n天内,相同时刻所有运营商统计的平均值compareDTO={}",
                    MonitorDateUtils.format(jobTime), MonitorDateUtils.format(baseTime), JSON.toJSONString(compareDTO));
            if (compareDTO == null) {
                return;
            }

            //获取需要预警的数据信息
            List<OperatorStatAccessAlarmMsgDTO> msgList = getAlarmMsgList(dataDTO, compareDTO, config);
            logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻baseTime={},所有运营商统计需要预警的数据信息msgList={}",
                    MonitorDateUtils.format(jobTime), MonitorDateUtils.format(baseTime), JSON.toJSONString(msgList));
            if (CollectionUtils.isEmpty(msgList)) {
                return;
            }
            //发送预警
            alarmMsg(msgList, jobTime, startTime, endTime, config, statType);
        } catch (Exception e) {
            logger.error("运营商监控,预警定时任务执行jobTime={}异常", MonitorDateUtils.format(jobTime), e);
        }

    }

    private OperatorAllStatAccessDTO getBaseData(Date jobTime, Date startTime, Date endTime, OperatorMonitorAlarmConfigDTO config, ETaskOperatorStatType statType) {
        OperatorAllStatAccessCriteria criteria = new OperatorAllStatAccessCriteria();
        criteria.createCriteria().andDataTypeEqualTo(statType.getCode())
                .andAppIdEqualTo(config.getAppId())
                .andDataTimeGreaterThanOrEqualTo(startTime)
                .andDataTimeLessThan(endTime);
        List<OperatorAllStatAccess> list = operatorAllStatAccessMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻startTime={},endTime={},此段时间内,未查询到所有运营商的统计数据list={}",
                    MonitorDateUtils.format(jobTime), MonitorDateUtils.format(startTime), MonitorDateUtils.format(endTime), JSON.toJSONString(list));
            return null;
        }
        OperatorAllStatAccess operatorAllStatAccess = list.get(0);
        OperatorAllStatAccessDTO dataDTO = DataConverterUtils.convert(operatorAllStatAccess, OperatorAllStatAccessDTO.class);
        int entryCount = 0, confirmMobileCount = 0, startLoginCount = 0, loginSuccessCount = 0,
                crawlSuccessCount = 0, processSuccessCount = 0, callbackSuccessCount = 0;
        for (OperatorAllStatAccess item : list) {
            entryCount = entryCount + item.getEntryCount();
            confirmMobileCount = confirmMobileCount + item.getConfirmMobileCount();
            startLoginCount = startLoginCount + item.getStartLoginCount();
            loginSuccessCount = loginSuccessCount + item.getLoginSuccessCount();
            crawlSuccessCount = crawlSuccessCount + item.getCrawlSuccessCount();
            processSuccessCount = processSuccessCount + item.getProcessSuccessCount();
            callbackSuccessCount = callbackSuccessCount + item.getCallbackSuccessCount();
        }
        dataDTO.setEntryCount(entryCount);
        dataDTO.setConfirmMobileCount(confirmMobileCount);
        dataDTO.setStartLoginCount(startLoginCount);
        dataDTO.setLoginSuccessCount(loginSuccessCount);
        dataDTO.setCrawlSuccessCount(crawlSuccessCount);
        dataDTO.setProcessSuccessCount(processSuccessCount);
        dataDTO.setCallbackSuccessCount(callbackSuccessCount);
        dataDTO.setConfirmMobileConversionRate(BigDecimal.valueOf(confirmMobileCount).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(entryCount), 2, BigDecimal.ROUND_HALF_UP));
        dataDTO.setLoginConversionRate(BigDecimal.valueOf(startLoginCount).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(confirmMobileCount), 2, BigDecimal.ROUND_HALF_UP));
        dataDTO.setLoginSuccessRate(BigDecimal.valueOf(loginSuccessCount).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(startLoginCount), 2, BigDecimal.ROUND_HALF_UP));
        dataDTO.setCrawlSuccessRate(BigDecimal.valueOf(crawlSuccessCount).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(loginSuccessCount), 2, BigDecimal.ROUND_HALF_UP));
        dataDTO.setProcessSuccessRate(BigDecimal.valueOf(processSuccessCount).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(crawlSuccessCount), 2, BigDecimal.ROUND_HALF_UP));
        dataDTO.setCallbackSuccessRate(BigDecimal.valueOf(callbackSuccessCount).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(processSuccessCount), 2, BigDecimal.ROUND_HALF_UP));
        dataDTO.setWholeConversionRate(BigDecimal.valueOf(callbackSuccessCount).multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(entryCount), 2, BigDecimal.ROUND_HALF_UP));
        return dataDTO;
    }

    private void alarmMsg(List<OperatorStatAccessAlarmMsgDTO> msgList, Date jobTime, Date startTime, Date endTime,
                          OperatorMonitorAlarmConfigDTO config, ETaskOperatorStatType statType) {
        String baseTile;
        String mailSwitch = config.getMailAlarmSwitch();
        String weChatSwitch = config.getWeChatAlarmSwitch();
        if (ETaskOperatorStatType.TASK.equals(statType)) {
            baseTile = "【总】运营商监控(按任务数统计)";
        } else {
            baseTile = "【总】运营商监控(按人数统计)";

        }
        if (StringUtils.equalsIgnoreCase(mailSwitch, "on")) {
            String mailDataBody = generateMailDataBody(msgList, startTime, endTime, baseTile);
            String title = generateTitle(baseTile);
            alarmMessageProducer.sendMail4OperatorMonitor(title, mailDataBody, jobTime);
        } else {
            logger.info("运营商监控,预警定时任务执行jobTime={},发送邮件开关已关闭", MonitorDateUtils.format(jobTime));

        }
        if (StringUtils.equalsIgnoreCase(weChatSwitch, "on")) {
            String weChatBody = generateWeChatBody(msgList, startTime, endTime, baseTile);
            alarmMessageProducer.sendWebChart4OperatorMonitor(weChatBody, jobTime);
        } else {
            logger.info("运营商监控,预警定时任务执行jobTime={},发送微信开关已关闭", MonitorDateUtils.format(jobTime));
        }
    }

    private String generateTitle(String baseTile) {
        return "saas-" + diamondConfig.getMonitorEnvironment() + baseTile + "发生预警";
    }

    private String generateMailDataBody(List<OperatorStatAccessAlarmMsgDTO> msgList, Date startTime, Date endTime, String baseTile) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<br>").append("您好，").append("saas-").append(diamondConfig.getMonitorEnvironment())
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
        for (OperatorStatAccessAlarmMsgDTO msg : msgList) {
            buffer.append("<tr>")
                    .append("<td>").append(msg.getAlarmDesc()).append(" ").append("</td>")
                    .append("<td>").append(msg.getValueDesc()).append(" ").append("</td>")
                    .append("<td>").append(msg.getThresholdDesc()).append(" ").append("</td>")
                    .append("<td>").append(msg.getOffset()).append("%").append(" ").append("</td>").append("</tr>");

        }
        buffer.append("</table>");
        return buffer.toString();
    }

    private String generateWeChatBody(List<OperatorStatAccessAlarmMsgDTO> msgList, Date startTime, Date endTime, String baseTile) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("您好，").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append(baseTile)
                .append("预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段数据存在问题").append("，此时监控数据如下，请及时处理：").append("\n");
        for (OperatorStatAccessAlarmMsgDTO msg : msgList) {
            buffer.append("【").append(msg.getAlarmSimpleDesc()).append("】")
                    .append("当前指标值:").append("【").append(msg.getValueDesc()).append("】")
                    .append("指标阀值:").append("【").append(msg.getThresholdDesc()).append("】")
                    .append("偏离阀值程度:").append("【").append(msg.getOffset()).append("】")
                    .append("\n");
        }
        return buffer.toString();
    }

    /**
     * 获取需要预警的数据信息
     *
     * @param dataDTO
     * @param compareDTO
     * @param config
     * @param statType   @return
     * @return
     */
    private List<OperatorStatAccessAlarmMsgDTO> getAlarmMsgList(OperatorAllStatAccessDTO dataDTO,
                                                                OperatorAllStatAccessDTO compareDTO,
                                                                OperatorMonitorAlarmConfigDTO config) {
        List<OperatorStatAccessAlarmMsgDTO> msgList = Lists.newArrayList();
        Integer previousDays = config.getPreviousDays();

        BigDecimal confirmMobileCompareVal = compareDTO.getPreviousConfirmMobileConversionRate().multiply(new BigDecimal(config.getConfirmMobileConversionRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal loginConversionCompareVal = compareDTO.getPreviousLoginConversionRate().multiply(new BigDecimal(config.getLoginConversionRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal loginSuccessCompareVal = compareDTO.getPreviousLoginSuccessRate().multiply(new BigDecimal(config.getLoginSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal crawlCompareVal = compareDTO.getPreviousCrawlSuccessRate().multiply(new BigDecimal(config.getCrawlSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal processCompareVal = compareDTO.getPreviousProcessSuccessRate().multiply(new BigDecimal(config.getProcessSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal callbackCompareVal = compareDTO.getPreviousCallbackSuccessRate().multiply(new BigDecimal(config.getCallbackSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal wholeConversionCompareVal = compareDTO.getPreviousWholeConversionRate().multiply(new BigDecimal(config.getWholeConversionRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);

        if (dataDTO.getConfirmMobileConversionRate().compareTo(confirmMobileCompareVal) < 0) {//确认手机转化率小于前7天平均值
            OperatorStatAccessAlarmMsgDTO msg = new OperatorStatAccessAlarmMsgDTO();
            msg.setAlarmDesc("确认手机转化率低于前" + previousDays + "天平均值的" + config.getConfirmMobileConversionRate() + "%");
            msg.setAlarmSimpleDesc("确认手机");
            msg.setValue(dataDTO.getConfirmMobileConversionRate());
            msg.setThreshold(confirmMobileCompareVal);
            String valueDesc = new StringBuilder()
                    .append(dataDTO.getConfirmMobileConversionRate()).append("%").append(" ").append("(")
                    .append(dataDTO.getConfirmMobileCount()).append("/")
                    .append(dataDTO.getEntryCount()).append(")").toString();
            msg.setValueDesc(valueDesc);
            String thresholdDesc = new StringBuilder()
                    .append(confirmMobileCompareVal).append("%").append(" ").append("(")
                    .append(compareDTO.getPreviousConfirmMobileAvgCount()).append("/")
                    .append(compareDTO.getPreviousEntryAvgCount()).append("*")
                    .append(new BigDecimal(config.getConfirmMobileConversionRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
            msg.setThresholdDesc(thresholdDesc);
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
            msg.setAlarmDesc("登陆转化率低于前" + previousDays + "天平均值的" + config.getLoginConversionRate() + "%");
            msg.setAlarmSimpleDesc("开始登陆");
            msg.setValue(dataDTO.getLoginConversionRate());
            msg.setThreshold(loginConversionCompareVal);
            String valueDesc = new StringBuilder()
                    .append(dataDTO.getLoginConversionRate()).append("%").append(" ").append("(")
                    .append(dataDTO.getLoginSuccessCount()).append("/")
                    .append(dataDTO.getConfirmMobileCount()).append(")").toString();
            msg.setValueDesc(valueDesc);
            String thresholdDesc = new StringBuilder()
                    .append(loginConversionCompareVal).append("%").append(" ").append("(")
                    .append(compareDTO.getPreviousStartLoginAvgCount()).append("/")
                    .append(compareDTO.getPreviousConfirmMobileAvgCount()).append("*")
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
            OperatorStatAccessAlarmMsgDTO msg = new OperatorStatAccessAlarmMsgDTO();
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
            OperatorStatAccessAlarmMsgDTO msg = new OperatorStatAccessAlarmMsgDTO();
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
            OperatorStatAccessAlarmMsgDTO msg = new OperatorStatAccessAlarmMsgDTO();
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
            OperatorStatAccessAlarmMsgDTO msg = new OperatorStatAccessAlarmMsgDTO();
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
            OperatorStatAccessAlarmMsgDTO msg = new OperatorStatAccessAlarmMsgDTO();
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


    /**
     * 获取前7天内,相同时刻运营商统计的平均值(登录转化率平均值,抓取成功率平均值,洗数成功率平均值)
     *
     * @param jobTime
     * @param baseTime
     * @param config
     * @param statType @return
     * @return
     */
    private OperatorAllStatAccessDTO getPreviousCompareData(Date jobTime, Date baseTime,
                                                            OperatorMonitorAlarmConfigDTO config, ETaskOperatorStatType statType) {
        Integer previousDays = config.getPreviousDays();
        List<Date> previousOClockList = MonitorDateUtils.getPreviousOClockTime(baseTime, previousDays);

        List<OperatorAllStatAccessDTO> previousDTOList = Lists.newArrayList();
        for (Date previousOClock : previousOClockList) {
            Date startTime = DateUtils.addMinutes(previousOClock, -config.getIntervalMins());
            Date endTime = previousOClock;
            OperatorAllStatAccessDTO dto = this.getBaseData(jobTime, startTime, endTime, config, statType);
            if (dto != null) {
                previousDTOList.add(dto);
            }
        }
        if (CollectionUtils.isEmpty(previousDTOList)) {
            logger.info("运营商监控,预警定时任务执行jobTime={},,在此时间前{}天内,未查询到所有运营商统计数据previousOClockList={},list={}",
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
        BigDecimal previousConfirmMobileConversionRateCount = BigDecimal.ZERO;
        BigDecimal previousLoginConversionRateCount = BigDecimal.ZERO;
        BigDecimal previousLoginSuccessRateCount = BigDecimal.ZERO;
        BigDecimal previousCrawlSuccessRateCount = BigDecimal.ZERO;
        BigDecimal previousProcessSuccessRateCount = BigDecimal.ZERO;
        BigDecimal previousCallbackSuccessRateCount = BigDecimal.ZERO;
        BigDecimal previousWholeConversionRateCount = BigDecimal.ZERO;
        Integer previousEntryCount = 0, previousConfirmMobileCount = 0, previousStartLoginCount = 0, previousLoginSuccessCount = 0,
                previousCrawlSuccessCount = 0, previousProcessSuccessCount = 0, previousCallbackSuccessCount = 0;

        for (OperatorAllStatAccessDTO previousDTO : previousDTOList) {
            previousConfirmMobileConversionRateCount = previousConfirmMobileConversionRateCount.add(previousDTO.getConfirmMobileConversionRate());
            previousLoginConversionRateCount = previousLoginConversionRateCount.add(previousDTO.getLoginConversionRate());
            previousLoginSuccessRateCount = previousLoginSuccessRateCount.add(previousDTO.getLoginSuccessRate());
            previousCrawlSuccessRateCount = previousCrawlSuccessRateCount.add(previousDTO.getCrawlSuccessRate());
            previousProcessSuccessRateCount = previousProcessSuccessRateCount.add(previousDTO.getProcessSuccessRate());
            previousCallbackSuccessRateCount = previousCallbackSuccessRateCount.add(previousDTO.getCallbackSuccessRate());
            previousWholeConversionRateCount = previousWholeConversionRateCount.add(previousDTO.getWholeConversionRate());

            previousEntryCount = previousEntryCount + previousDTO.getEntryCount();
            previousConfirmMobileCount = previousConfirmMobileCount + previousDTO.getConfirmMobileCount();
            previousStartLoginCount = previousStartLoginCount + previousDTO.getStartLoginCount();
            previousLoginSuccessCount = previousLoginSuccessCount + previousDTO.getLoginSuccessCount();
            previousCrawlSuccessCount = previousCrawlSuccessCount + previousDTO.getCrawlSuccessCount();
            previousProcessSuccessCount = previousProcessSuccessCount + previousDTO.getProcessSuccessCount();
            previousCallbackSuccessCount = previousCallbackSuccessCount + previousDTO.getCallbackSuccessCount();
        }
        OperatorAllStatAccessDTO compareDto = new OperatorAllStatAccessDTO();
        compareDto.setPreviousConfirmMobileConversionRate(previousConfirmMobileConversionRateCount.divide(BigDecimal.valueOf(previousDTOList.size()), 2, BigDecimal.ROUND_HALF_UP));
        compareDto.setPreviousLoginConversionRate(previousLoginConversionRateCount.divide(BigDecimal.valueOf(previousDTOList.size()), 2, BigDecimal.ROUND_HALF_UP));
        compareDto.setPreviousLoginSuccessRate(previousLoginSuccessRateCount.divide(BigDecimal.valueOf(previousDTOList.size()), 2, BigDecimal.ROUND_HALF_UP));
        compareDto.setPreviousCrawlSuccessRate(previousCrawlSuccessRateCount.divide(BigDecimal.valueOf(previousDTOList.size()), 2, BigDecimal.ROUND_HALF_UP));
        compareDto.setPreviousProcessSuccessRate(previousProcessSuccessRateCount.divide(BigDecimal.valueOf(previousDTOList.size()), 2, BigDecimal.ROUND_HALF_UP));
        compareDto.setPreviousCallbackSuccessRate(previousCallbackSuccessRateCount.divide(BigDecimal.valueOf(previousDTOList.size()), 2, BigDecimal.ROUND_HALF_UP));
        compareDto.setPreviousWholeConversionRate(previousWholeConversionRateCount.divide(BigDecimal.valueOf(previousDTOList.size()), 2, BigDecimal.ROUND_HALF_UP));

        compareDto.setPreviousEntryAvgCount(BigDecimal.valueOf(previousEntryCount).divide(BigDecimal.valueOf(previousDTOList.size()), 1, BigDecimal.ROUND_HALF_UP));
        compareDto.setPreviousConfirmMobileAvgCount(BigDecimal.valueOf(previousConfirmMobileCount).divide(BigDecimal.valueOf(previousDTOList.size()), 1, BigDecimal.ROUND_HALF_UP));
        compareDto.setPreviousStartLoginAvgCount(BigDecimal.valueOf(previousStartLoginCount).divide(BigDecimal.valueOf(previousDTOList.size()), 1, BigDecimal.ROUND_HALF_UP));
        compareDto.setPreviousLoginSuccessAvgCount(BigDecimal.valueOf(previousLoginSuccessCount).divide(BigDecimal.valueOf(previousDTOList.size()), 1, BigDecimal.ROUND_HALF_UP));
        compareDto.setPreviousCrawlSuccessAvgCount(BigDecimal.valueOf(previousCrawlSuccessCount).divide(BigDecimal.valueOf(previousDTOList.size()), 1, BigDecimal.ROUND_HALF_UP));
        compareDto.setPreviousProcessSuccessAvgCount(BigDecimal.valueOf(previousProcessSuccessCount).divide(BigDecimal.valueOf(previousDTOList.size()), 1, BigDecimal.ROUND_HALF_UP));
        compareDto.setPreviousCallbackSuccessAvgCount(BigDecimal.valueOf(previousCallbackSuccessCount).divide(BigDecimal.valueOf(previousDTOList.size()), 1, BigDecimal.ROUND_HALF_UP));

        return compareDto;
    }
}
