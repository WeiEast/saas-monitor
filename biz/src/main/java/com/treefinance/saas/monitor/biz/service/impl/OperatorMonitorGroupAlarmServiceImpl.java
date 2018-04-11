package com.treefinance.saas.monitor.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.config.OperatorMonitorConfig;
import com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.biz.service.IvrNotifyService;
import com.treefinance.saas.monitor.biz.service.OperatorMonitorGroupAlarmService;
import com.treefinance.saas.monitor.biz.service.SmsNotifyService;
import com.treefinance.saas.monitor.common.constants.MonitorConstants;
import com.treefinance.saas.monitor.common.domain.dto.OperatorMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorStatAccessDTO;
import com.treefinance.saas.monitor.common.domain.dto.TaskStatAccessAlarmMsgDTO;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmType;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.common.utils.StatisticCalcUtil;
import com.treefinance.saas.monitor.dao.entity.OperatorStatAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorStatAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.OperatorStatAccessMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.treefinance.saas.monitor.common.constants.AlarmConstants.SWITCH_ON;
import static java.util.stream.Collectors.groupingBy;

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
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IvrNotifyService ivrNotifyService;

    @Autowired
    private SmsNotifyService smsNotifyService;
    @Autowired
    private OperatorMonitorConfig operatorMonitorConfig;


    @Override
    public void alarm(Date jobTime, OperatorMonitorAlarmConfigDTO config) {
        try {

            Integer intervalMins = config.getIntervalMins();
            //由于任务执行需要时间,保证预警的精确,预警统计向前一段时间(各业务任务的超时时间),此时此段时间的任务可以保证都已统计完毕.
            //好处:预警时间即使每隔1分钟预警,依然可以保证预警的准确.坏处:收到预警消息时间向后延迟了相应时间.
            //如:jobTime=14:11,但是运营商超时时间为600s,则statTime=14:01
            Date statTime = DateUtils.addSeconds(jobTime, -config.getTaskTimeoutSecs());
            //取得预警原点时间,如:statTime=14:01分,30分钟间隔统计一次,则beginTime为14:00.统计的数据间隔[13:30-13:40;13:40-13:50;13:50-14:00]
            Date baseTime = TaskOperatorMonitorKeyHelper.getRedisStatDateTime(statTime, intervalMins);

            //判断此时刻是否预警预警过
            String alarmTimeKey = TaskOperatorMonitorKeyHelper.keyOfAlarmTimeLog(baseTime, config);
            BoundSetOperations<String, Object> setOperations = redisTemplate.boundSetOps(alarmTimeKey);
            if (setOperations.isMember(MonitorDateUtils.format(baseTime))) {
                logger.info("运营商监控,预警定时任务执行jobTime={},baseTime={},config={}已预警,不再预警",
                        MonitorDateUtils.format(baseTime), JSON.toJSONString(config));
                return;
            }
            setOperations.add(MonitorDateUtils.format(baseTime));
            if (setOperations.getExpire() == -1) {
                setOperations.expire(2, TimeUnit.DAYS);
            }

            //获取基础数据
            Date startTime = DateUtils.addMinutes(baseTime, -intervalMins);
            Date endTime = baseTime;
            List<OperatorStatAccessDTO> dtoList = this.getBaseDataList(jobTime, startTime, endTime, config);
            if (CollectionUtils.isEmpty(dtoList)) {
                logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻startTime={},endTime={},此段时间内,未查询到分运营商的统计数据",
                        MonitorDateUtils.format(jobTime), MonitorDateUtils.format(startTime), MonitorDateUtils.format(endTime));
                return;
            }

            //获取前7天内,相同时刻运营商统计的平均值(登录转化率平均值,抓取成功率平均值,洗数成功率平均值)
            //<groupCode,OperatorStatAccessDTO>
            Map<String, OperatorStatAccessDTO> compareMap = getPreviousCompareDataMap(jobTime, baseTime, dtoList, config);
            logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},获取前n天内,相同时刻区分运营商统计的平均值compareMap={}",
                    MonitorDateUtils.format(jobTime), MonitorDateUtils.format(baseTime), JSON.toJSONString(compareMap));
            if (MapUtils.isEmpty(compareMap)) {
                return;
            }

            //获取需要预警的数据信息
            List<TaskStatAccessAlarmMsgDTO> msgList = getAlarmMsgList(jobTime, dtoList, compareMap, config);
            logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},区分运营商统计需要预警的数据信息msgList={}",
                    MonitorDateUtils.format(jobTime), MonitorDateUtils.format(baseTime), JSON.toJSONString(msgList));
            if (CollectionUtils.isEmpty(msgList)) {
                return;
            }
            //发送预警
            alarmMsg(msgList, jobTime, startTime, endTime, config);
        } catch (Exception e) {
            logger.error("运营商监控,预警定时任务执行jobTime={},config={}异常", MonitorDateUtils.format(jobTime), JSON.toJSONString(config), e);
        }
    }

    private List<OperatorStatAccessDTO> getBaseDataList(Date jobTime, Date startTime, Date endTime,
                                                        OperatorMonitorAlarmConfigDTO config) {
        List<String> operatorNameList = Splitter.on(",").splitToList(diamondConfig.getOperatorAlarmOperatorNameList());
        if (CollectionUtils.isEmpty(operatorNameList)) {
            logger.error("运营商监控,预警定时任务执行jobTime={},未配置需要预警的运营商,operator.alarm.operator.name.list未配置",
                    MonitorDateUtils.format(jobTime));
            return Lists.newArrayList();
        }
        OperatorStatAccessCriteria criteria = new OperatorStatAccessCriteria();
        OperatorStatAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        innerCriteria.andAppIdEqualTo(config.getAppId())
                .andDataTypeEqualTo(config.getDataType())
                .andSaasEnvEqualTo(config.getSaasEnv())
                .andGroupNameIn(operatorNameList)
                .andDataTimeGreaterThanOrEqualTo(startTime)
                .andDataTimeLessThan(endTime);
        //总运营商
        if (config.getAlarmType() == 1) {
            innerCriteria.andGroupCodeEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_OPERATOR);
        } else {
            innerCriteria.andGroupCodeNotEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_OPERATOR);
        }
        List<OperatorStatAccess> list = operatorStatAccessMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻startTime={},endTime={},此段时间内,未查询到区分运营商的统计数据list={}",
                    MonitorDateUtils.format(jobTime), MonitorDateUtils.format(startTime), MonitorDateUtils.format(endTime), JSON.toJSONString(list));
            return Lists.newArrayList();
        }
        List<OperatorStatAccessDTO> dataDTOList = Lists.newArrayList();
        Map<String, List<OperatorStatAccess>> groupCodeDataMap = list.stream().collect(groupingBy(OperatorStatAccess::getGroupCode));
        for (Map.Entry<String, List<OperatorStatAccess>> entry : groupCodeDataMap.entrySet()) {
            OperatorStatAccess data = entry.getValue().get(0);
            OperatorStatAccessDTO dataDTO = DataConverterUtils.convert(data, OperatorStatAccessDTO.class);
            List<OperatorStatAccess> valueList = entry.getValue();
            int entryCount = 0, confirmMobileCount = 0, startLoginCount = 0, loginSuccessCount = 0,
                    crawlSuccessCount = 0, processSuccessCount = 0, callbackSuccessCount = 0;
            for (OperatorStatAccess item : valueList) {
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
            dataDTO.setConfirmMobileConversionRate(StatisticCalcUtil.calcRate(confirmMobileCount, entryCount));
            dataDTO.setLoginConversionRate(StatisticCalcUtil.calcRate(startLoginCount, confirmMobileCount));
            dataDTO.setLoginSuccessRate(StatisticCalcUtil.calcRate(loginSuccessCount, startLoginCount));
            dataDTO.setCrawlSuccessRate(StatisticCalcUtil.calcRate(crawlSuccessCount, loginSuccessCount));
            dataDTO.setProcessSuccessRate(StatisticCalcUtil.calcRate(processSuccessCount, crawlSuccessCount));
            dataDTO.setCallbackSuccessRate(StatisticCalcUtil.calcRate(callbackSuccessCount, processSuccessCount));
            dataDTO.setWholeConversionRate(StatisticCalcUtil.calcRate(callbackSuccessCount, entryCount));
            dataDTOList.add(dataDTO);
        }
        return dataDTOList;
    }

    /**
     * 发送预警
     *
     * @param msgList   报警信息的列表
     * @param jobTime   任务时间
     * @param startTime 时间区间的开始时间
     * @param endTime   时间区间的结束时间
     * @param config    配置文件
     */
    private void alarmMsg(List<TaskStatAccessAlarmMsgDTO> msgList,
                          Date jobTime,
                          Date startTime,
                          Date endTime,
                          OperatorMonitorAlarmConfigDTO config) {
        String baseTitle;
        String mailSwitch = config.getMailAlarmSwitch();
        String weChatSwitch = config.getWeChatAlarmSwitch();

        String smsSwitch = operatorMonitorConfig.getSmsSwitch();
        String ivrSwitch = operatorMonitorConfig.getIvrSwitch();
        ETaskStatDataType statType = ETaskStatDataType.getByValue(config.getDataType());

        if (ETaskStatDataType.TASK.equals(statType)) {
            baseTitle = "运营商监控(按任务数统计)";
        } else {
            baseTitle = "运营商监控(按人数统计)";
        }

        Map<String, List<TaskStatAccessAlarmMsgDTO>> operatorNameGroup = msgList.stream().collect(Collectors
                .groupingBy(TaskStatAccessAlarmMsgDTO::getGroupName));

        boolean isError = msgList.stream().anyMatch(TaskStatAccessAlarmMsgDTO -> TaskStatAccessAlarmMsgDTO
                .getAlarmLevel().equals(EAlarmLevel.error));
        boolean isWarning = msgList.stream().anyMatch(TaskStatAccessAlarmMsgDTO -> TaskStatAccessAlarmMsgDTO
                .getAlarmLevel().equals(EAlarmLevel.warning)) || operatorNameGroup.keySet().size() >= 3;

        if (isError) {
            sendMail(msgList, jobTime, startTime, endTime, statType, baseTitle, mailSwitch, EAlarmLevel.error);
            sendIvr(msgList, jobTime, ivrSwitch);
            sendWeChat(msgList, jobTime, startTime, endTime, baseTitle, weChatSwitch, EAlarmLevel.error);
        } else if (isWarning) {
            sendMail(msgList, jobTime, startTime, endTime, statType, baseTitle, mailSwitch, EAlarmLevel.warning);
            sendSms(msgList, jobTime, startTime, endTime, statType, smsSwitch, EAlarmLevel.warning);
            sendWeChat(msgList, jobTime, startTime, endTime, baseTitle, weChatSwitch, EAlarmLevel.warning);
        } else {
            sendMail(msgList, jobTime, startTime, endTime, statType, baseTitle, mailSwitch, EAlarmLevel.info);
            sendWeChat(msgList, jobTime, startTime, endTime, baseTitle, weChatSwitch, EAlarmLevel.info);
        }
    }

    private void sendSms(List<TaskStatAccessAlarmMsgDTO> msgList, Date jobTime, Date startTime, Date endTime,
                         ETaskStatDataType statType, String smsSwitch, EAlarmLevel alarmLevel) {
        if (StringUtils.equalsIgnoreCase(smsSwitch, SWITCH_ON)) {

            String template = "${level} ${type} 时间段:${startTime}至${endTime},运营商:${groupName} " +
                    "预警类型:${alarmDesc},偏离阀值程度${offset}%";
            Map<String, Object> map = Maps.newHashMap();

            List<TaskStatAccessAlarmMsgDTO> warningMsg = msgList.stream().filter(TaskStatAccessAlarmMsgDTO ->
                    EAlarmLevel.warning.equals(TaskStatAccessAlarmMsgDTO.getAlarmLevel())).collect(Collectors.toList());

            String type = "SAAS-" + diamondConfig.getMonitorEnvironment() + "-" + (ETaskStatDataType.TASK.equals(statType) ? "运营商-分时任务" : "运营商-分时人数");

            String format = "yyyy-MM-dd HH:mm:SS";
            String startTimeStr = new SimpleDateFormat(format).format(startTime);
            String endTimeStr = new SimpleDateFormat(format).format(endTime);
            TaskStatAccessAlarmMsgDTO dto = msgList.get(0);
            if (!warningMsg.isEmpty()) {
                dto = warningMsg.get(0);
            }

            map.put("level", alarmLevel.name());
            map.put("type", type);
            map.put("startTime", startTimeStr);
            map.put("endTime", endTimeStr);
            map.put("groupName", dto.getGroupName());
            map.put("alarmDesc", dto.getAlarmDesc());
            map.put("offset", dto.getOffset());

            smsNotifyService.send(StrSubstitutor.replace(template, map));
        } else {
            logger.info("运营商监控,预警定时任务执行jobTime={},发送短信开关已关闭", MonitorDateUtils.format(jobTime));
        }
    }

    private void sendIvr(List<TaskStatAccessAlarmMsgDTO> msgList, Date jobTime, String ivrSwitch) {
        if (StringUtils.equalsIgnoreCase(ivrSwitch, SWITCH_ON)) {

            List<TaskStatAccessAlarmMsgDTO> errorMsgs = msgList.stream().filter(TaskStatAccessAlarmMsgDTO ->
                    EAlarmLevel.error.equals(TaskStatAccessAlarmMsgDTO.getAlarmLevel())).collect(Collectors.toList());

            logger.info("特定运营商预警 发送ivr请求 {}", errorMsgs.get(0).getAlarmDesc());

            ivrNotifyService.notifyIvr(EAlarmLevel.error, EAlarmType.operator_alarm, errorMsgs.get(0).getAlarmDesc());
        } else {
            logger.info("运营商监控,预警定时任务执行jobTime={},发送ivr开关已关闭", MonitorDateUtils.format(jobTime));
        }

    }

    private void sendMail(List<TaskStatAccessAlarmMsgDTO> msgList, Date jobTime, Date startTime, Date endTime,
                          ETaskStatDataType statType, String baseTile, String mailSwitch, EAlarmLevel alarmLevel) {
        if (StringUtils.equalsIgnoreCase(mailSwitch, SWITCH_ON)) {

            String mailBaseTitle = "【${level}】【${module}】【${type}】发生 ${detail} 预警";

            Map<String, Object> map = new HashMap<>(4);
            map.put("type", ETaskStatDataType.TASK.equals(statType) ? "运营商-任务" : "运营商-人数");
            map.put("level", alarmLevel.name());

            String mailDataBody = generateMailDataBody(msgList, startTime, endTime, baseTile, map, alarmLevel);

            alarmMessageProducer.sendMail4OperatorMonitor(StrSubstitutor.replace(mailBaseTitle, map), mailDataBody, jobTime);
        } else {
            logger.info("运营商监控,预警定时任务执行jobTime={},发送邮件开关已关闭", MonitorDateUtils.format(jobTime));
        }
    }

    private void sendWeChat(List<TaskStatAccessAlarmMsgDTO> msgList, Date jobTime, Date startTime, Date endTime,
                            String baseTile, String weChatSwitch, EAlarmLevel alarmLevel) {
        if (StringUtils.equalsIgnoreCase(weChatSwitch, SWITCH_ON)) {
            String weChatBody = generateWeChatBody(msgList, startTime, endTime, baseTile, alarmLevel);
            alarmMessageProducer.sendWebChart4OperatorMonitor(weChatBody, jobTime);
        } else {
            logger.info("运营商监控,预警定时任务执行jobTime={},发送微信开关已关闭", MonitorDateUtils.format(jobTime));
        }
    }


    private String generateMailDataBody(List<TaskStatAccessAlarmMsgDTO> msgList, Date startTime, Date endTime,
                                        String baseTitle, Map<String, Object> map, EAlarmLevel eAlarmLevel) {

        StringBuilder pageHtml = new StringBuilder();

        StringBuilder tableTrs = new StringBuilder();
        //title里面的具体内容
        StringBuilder detail = new StringBuilder();

        detail.append("【");
        for (TaskStatAccessAlarmMsgDTO msg : msgList) {
            tableTrs.append("<tr>").append("<td>").append(msg.getGroupName()).append("</td>")
                    .append("<td>").append(msg.getAlarmDesc()).append(" ").append("</td>")
                    .append("<td>").append(msg.getValueDesc()).append(" ").append("</td>")
                    .append("<td>").append(msg.getThresholdDesc()).append(" ").append("</td>")
                    .append("<td>").append(msg.getOffset()).append("%").append(" ").append("</td>").append("</tr>");
            detail.append(msg.getGroupName()).append("-").append(msg.getAlarmType()).append("(").append(msg.getOffset
                    ()).append("%").append(")").append("，");
        }
        String detailsStr = detail.substring(0, detail.length() - 1);
        detailsStr += "】";

        String module = "saas-" + diamondConfig.getMonitorEnvironment();
        pageHtml.append("<br>").append("【").append(eAlarmLevel.name()).append("】").append
                ("您好，").append
                (module)
                .append(baseTitle)
                .append("预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段数据存在问题").append("，此时监控数据如下，请及时处理：").append("</br>");
        pageHtml.append("<table border=\"1\" cellspacing=\"0\" bordercolor=\"#BDBDBD\" >");
        pageHtml.append("<tr bgcolor=\"#C9C9C9\">")
                .append("<th>").append("运营商").append("</th>")
                .append("<th>").append("预警描述").append("</th>")
                .append("<th>").append("当前指标值").append("</th>")
                .append("<th>").append("指标阀值").append("</th>")
                .append("<th>").append("偏离阀值程度").append("</th>")
                .append("</tr>");

        pageHtml.append(tableTrs);
        pageHtml.append("</table>");


        map.put("module", module);
        map.put("detail", detailsStr);

        return pageHtml.toString();
    }

    private String generateWeChatBody(List<TaskStatAccessAlarmMsgDTO> msgList, Date startTime, Date endTime,
                                      String baseTitle, EAlarmLevel alarmLevel) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("【").append(alarmLevel.name()).append("】")
                .append("您好，").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append(baseTitle)
                .append("预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段数据存在问题").append("，此时监控数据如下，请及时处理：").append("\n");
        for (TaskStatAccessAlarmMsgDTO msg : msgList) {
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
     * @param jobTime  任务时间
     * @param baseTime 任务时间区间的结束时间
     * @param dtoList  基础数据的列表
     * @param config   配置
     * @return
     */
    private Map<String, OperatorStatAccessDTO> getPreviousCompareDataMap(Date jobTime, Date baseTime,
                                                                         List<OperatorStatAccessDTO> dtoList,
                                                                         OperatorMonitorAlarmConfigDTO config) {
        List<String> groupCodeList = dtoList.stream().map(OperatorStatAccessDTO::getGroupCode).distinct().collect(Collectors.toList());
        Integer previousDays = config.getPreviousDays();
        List<Date> previousOClockList = MonitorDateUtils.getPreviousOClockTime(baseTime, previousDays);
        List<OperatorStatAccessDTO> previousDTOList = Lists.newArrayList();
        for (Date previousOClock : previousOClockList) {
            Date startTime = DateUtils.addMinutes(previousOClock, -config.getIntervalMins());
            Date endTime = previousOClock;
            List<OperatorStatAccessDTO> list = this.getBaseDataList(jobTime, startTime, endTime, config);
            previousDTOList.addAll(list);
        }


        if (CollectionUtils.isEmpty(previousDTOList)) {
            logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},在此时间前{}天内,未查询到区分运营商统计数据groupCodeList={},previousOClockList={},list={}",
                    MonitorDateUtils.format(jobTime), MonitorDateUtils.format(baseTime), previousDays, JSON.toJSONString(groupCodeList),
                    JSON.toJSONString(previousOClockList), JSON.toJSONString(previousDTOList));
            return Maps.newHashMap();
        }
        //<groupCode,List<OperatorStatAccessDTO>>
        Map<String, List<OperatorStatAccessDTO>> previousMap = previousDTOList.stream().collect(groupingBy(OperatorStatAccessDTO::getGroupCode));
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
            BigDecimal previousConfirmMobileConversionRateCount = BigDecimal.ZERO;
            BigDecimal previousLoginConversionRateCount = BigDecimal.ZERO;
            BigDecimal previousLoginSuccessRateCount = BigDecimal.ZERO;
            BigDecimal previousCrawlSuccessRateCount = BigDecimal.ZERO;
            BigDecimal previousProcessSuccessRateCount = BigDecimal.ZERO;
            BigDecimal previousCallbackSuccessRateCount = BigDecimal.ZERO;
            BigDecimal previousWholeConversionRateCount = BigDecimal.ZERO;

            Integer previousEntryCount = 0, previousConfirmMobileCount = 0, previousStartLoginCount = 0, previousLoginSuccessCount = 0,
                    previousCrawlSuccessCount = 0, previousProcessSuccessCount = 0, previousCallbackSuccessCount = 0;
            for (OperatorStatAccessDTO dto : entryList) {
                previousConfirmMobileConversionRateCount = previousConfirmMobileConversionRateCount.add(dto.getConfirmMobileConversionRate());
                previousLoginConversionRateCount = previousLoginConversionRateCount.add(dto.getLoginConversionRate());
                previousLoginSuccessRateCount = previousLoginSuccessRateCount.add(dto.getLoginSuccessRate());
                previousCrawlSuccessRateCount = previousCrawlSuccessRateCount.add(dto.getCrawlSuccessRate());
                previousProcessSuccessRateCount = previousProcessSuccessRateCount.add(dto.getProcessSuccessRate());
                previousCallbackSuccessRateCount = previousCallbackSuccessRateCount.add(dto.getCallbackSuccessRate());
                previousWholeConversionRateCount = previousWholeConversionRateCount.add(dto.getWholeConversionRate());

                previousEntryCount = previousEntryCount + dto.getEntryCount();
                previousConfirmMobileCount = previousConfirmMobileCount + dto.getConfirmMobileCount();
                previousStartLoginCount = previousStartLoginCount + dto.getStartLoginCount();
                previousLoginSuccessCount = previousLoginSuccessCount + dto.getLoginSuccessCount();
                previousCrawlSuccessCount = previousCrawlSuccessCount + dto.getCrawlSuccessCount();
                previousProcessSuccessCount = previousProcessSuccessCount + dto.getProcessSuccessCount();
                previousCallbackSuccessCount = previousCallbackSuccessCount + dto.getCallbackSuccessCount();
            }
            OperatorStatAccessDTO compareDto = new OperatorStatAccessDTO();
            compareDto.setGroupCode(entry.getKey());
            compareDto.setPreviousConfirmMobileConversionRate(previousConfirmMobileConversionRateCount.divide(BigDecimal.valueOf(entryList.size()), 2, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousLoginConversionRate(previousLoginConversionRateCount.divide(BigDecimal.valueOf(entryList.size()), 2, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousLoginSuccessRate(previousLoginSuccessRateCount.divide(BigDecimal.valueOf(entryList.size()), 2, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousCrawlSuccessRate(previousCrawlSuccessRateCount.divide(BigDecimal.valueOf(entryList.size()), 2, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousProcessSuccessRate(previousProcessSuccessRateCount.divide(BigDecimal.valueOf(entryList.size()), 2, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousCallbackSuccessRateRate(previousCallbackSuccessRateCount.divide(BigDecimal.valueOf(entryList.size()), 2, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousWholeConversionRate(previousWholeConversionRateCount.divide(BigDecimal.valueOf(entryList.size()), 2, BigDecimal.ROUND_HALF_UP));

            compareDto.setPreviousEntryAvgCount(BigDecimal.valueOf(previousEntryCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousConfirmMobileAvgCount(BigDecimal.valueOf(previousConfirmMobileCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousStartLoginAvgCount(BigDecimal.valueOf(previousStartLoginCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousLoginSuccessAvgCount(BigDecimal.valueOf(previousLoginSuccessCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousCrawlSuccessAvgCount(BigDecimal.valueOf(previousCrawlSuccessCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousProcessSuccessAvgCount(BigDecimal.valueOf(previousProcessSuccessCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousCallbackSuccessAvgCount(BigDecimal.valueOf(previousCallbackSuccessCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareMap.put(entry.getKey(), compareDto);
        }
        return compareMap;
    }

    /**
     * 获取需要预警的数据信息
     *
     * @param now        时间
     * @param dtoList    数据列表
     * @param compareMap 用于比较属性值的map
     * @param config     配置
     * @return 确认手机号、登录转化率，登录、抓取、洗数、回调成功率
     */
    private List<TaskStatAccessAlarmMsgDTO> getAlarmMsgList(Date now,
                                                            List<OperatorStatAccessDTO> dtoList,
                                                            Map<String, OperatorStatAccessDTO> compareMap,
                                                            OperatorMonitorAlarmConfigDTO config) {
        List<TaskStatAccessAlarmMsgDTO> msgList = Lists.newArrayList();
        Integer previousDays = config.getPreviousDays();
        for (OperatorStatAccessDTO dto : dtoList) {
            if (compareMap.get(dto.getGroupCode()) == null) {
                logger.info("运营商监控,预警定时任务执行jobTime={},groupCode={}的运营商前{}天未查询到统计数据",
                        MonitorDateUtils.format(now), dto.getGroupCode(), previousDays);
                continue;
            }

            OperatorStatAccessDTO compareDTO = compareMap.get(dto.getGroupCode());
            BigDecimal loginConversionCompareVal = compareDTO.getPreviousLoginConversionRate().multiply(new BigDecimal(config.getLoginConversionRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal loginSuccessCompareVal = compareDTO.getPreviousLoginSuccessRate().multiply(new BigDecimal(config.getLoginSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal crawlCompareVal = compareDTO.getPreviousCrawlSuccessRate().multiply(new BigDecimal(config.getCrawlSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal processCompareVal = compareDTO.getPreviousProcessSuccessRate().multiply(new BigDecimal(config.getProcessSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal callbackCompareVal = compareDTO.getPreviousCallbackSuccessRateRate().multiply(new BigDecimal(config.getCallbackSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);

            if (config.getAlarmType() == 1) {
                BigDecimal confirmMobileCompareVal = compareDTO.getPreviousConfirmMobileConversionRate().multiply(new BigDecimal(config.getConfirmMobileConversionRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                BigDecimal wholeConversionCompareVal = compareDTO.getPreviousWholeConversionRate().multiply(new BigDecimal(config.getWholeConversionRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);

                //确认手机转化率小于前7天平均值
                if (dto.getConfirmMobileConversionRate().compareTo(confirmMobileCompareVal) < 0) {
                    TaskStatAccessAlarmMsgDTO msg = new TaskStatAccessAlarmMsgDTO();
                    msg.setGroupCode(dto.getGroupCode());
                    msg.setGroupName(dto.getGroupName());
                    msg.setAlarmDesc("确认手机转化率低于前" + previousDays + "天平均值的" + config.getConfirmMobileConversionRate() + "%");
                    msg.setAlarmSimpleDesc("确认手机");
                    msg.setAlarmType("确认手机转化率");
                    msg.setValue(dto.getConfirmMobileConversionRate());
                    msg.setThreshold(confirmMobileCompareVal);
                    String valueDesc = new StringBuilder()
                            .append(dto.getConfirmMobileConversionRate()).append("%").append(" ").append("(")
                            .append(dto.getConfirmMobileCount()).append("/")
                            .append(dto.getEntryCount()).append(")").toString();
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
                        BigDecimal value = BigDecimal.ONE.subtract(dto.getConfirmMobileConversionRate().divide(confirmMobileCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                        msg.setOffset(value);
                        determineAllLevel(msg, value);
                    }
                    msgList.add(msg);
                }

                //总转化率小于前7天平均值
                if (dto.getWholeConversionRate().compareTo(wholeConversionCompareVal) < 0) {
                    TaskStatAccessAlarmMsgDTO msg = new TaskStatAccessAlarmMsgDTO();
                    msg.setGroupCode(dto.getGroupCode());
                    msg.setGroupName(dto.getGroupName());
                    msg.setAlarmDesc("总转化率低于前" + previousDays + "天平均值的" + config.getWholeConversionRate() + "%");
                    msg.setAlarmType("总转化率");
                    msg.setAlarmSimpleDesc("总转化率");
                    msg.setValue(dto.getWholeConversionRate());
                    msg.setThreshold(wholeConversionCompareVal);
                    String valueDesc = new StringBuilder()
                            .append(dto.getWholeConversionRate()).append("%").append(" ").append("(")
                            .append(dto.getCallbackSuccessCount()).append("/")
                            .append(dto.getEntryCount()).append(")").toString();
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
                        BigDecimal value = BigDecimal.ONE.subtract(dto.getWholeConversionRate().divide(wholeConversionCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                        msg.setOffset(value);
                        determineLevel(msg, value);
                    }
                    msgList.add(msg);

                }
            }


            //登录转化率小于前7天平均值
            if (isAlarm(dto.getConfirmMobileCount(), dto.getLoginConversionRate(), loginConversionCompareVal)) {
                TaskStatAccessAlarmMsgDTO msg = new TaskStatAccessAlarmMsgDTO();
                msg.setGroupCode(dto.getGroupCode());
                msg.setGroupName(dto.getGroupName());
                msg.setAlarmDesc("登陆转化率低于前" + previousDays + "天平均值的" + config.getLoginConversionRate() + "%");
                msg.setAlarmType("登陆转化率");
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
                        .append(new BigDecimal(config.getLoginConversionRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
                msg.setThresholdDesc(thresholdDesc);
                calcOffsetAndLevel(loginConversionCompareVal, msg, dto.getLoginConversionRate());
                msgList.add(msg);
            }
            //登录成功率小于前7天平均值
            if (isAlarm(dto.getStartLoginCount(), dto.getLoginSuccessRate(), loginSuccessCompareVal)) {
                TaskStatAccessAlarmMsgDTO msg = new TaskStatAccessAlarmMsgDTO();
                msg.setGroupCode(dto.getGroupCode());
                msg.setGroupName(dto.getGroupName());
                msg.setAlarmDesc("登陆成功率低于前" + previousDays + "天平均值的" + config.getLoginSuccessRate() + "%");
                msg.setAlarmType("登陆成功率");
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
                        .append(new BigDecimal(config.getLoginSuccessRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
                msg.setThresholdDesc(thresholdDesc);
                calcOffsetAndLevel(loginSuccessCompareVal, msg, dto.getLoginSuccessRate());
                msgList.add(msg);
            }
            //抓取成功率小于前7天平均值
            if (isAlarm(dto.getLoginSuccessCount(), dto.getCrawlSuccessRate(), crawlCompareVal)) {
                TaskStatAccessAlarmMsgDTO msg = new TaskStatAccessAlarmMsgDTO();
                msg.setGroupCode(dto.getGroupCode());
                msg.setGroupName(dto.getGroupName());
                msg.setAlarmDesc("抓取成功率低于前" + previousDays + "天平均值的" + config.getCrawlSuccessRate() + "%");
                msg.setAlarmType("抓取成功率");
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
                        .append(new BigDecimal(config.getCrawlSuccessRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
                msg.setThresholdDesc(thresholdDesc);

                calcOffsetAndLevel(crawlCompareVal, msg, dto.getCrawlSuccessRate());
                msgList.add(msg);
            }
            //洗数成功率小于前7天平均值
            if (isAlarm(dto.getCrawlSuccessCount(), dto.getProcessSuccessRate(), processCompareVal)) {
                TaskStatAccessAlarmMsgDTO msg = new TaskStatAccessAlarmMsgDTO();
                msg.setGroupCode(dto.getGroupCode());
                msg.setGroupName(dto.getGroupName());
                msg.setAlarmDesc("洗数成功率低于前" + previousDays + "天平均值的" + config.getProcessSuccessRate() + "%");
                msg.setAlarmType("洗数成功率");
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
                        .append(new BigDecimal(config.getProcessSuccessRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
                msg.setThresholdDesc(thresholdDesc);

                calcOffsetAndLevel(processCompareVal, msg, dto.getProcessSuccessRate());
                msgList.add(msg);
            }
            //洗数成功率小于前7天平均值
            if (isAlarm(dto.getCallbackSuccessCount(), dto.getCallbackSuccessRate(), callbackCompareVal)) {
                TaskStatAccessAlarmMsgDTO msg = new TaskStatAccessAlarmMsgDTO();
                msg.setGroupCode(dto.getGroupCode());
                msg.setGroupName(dto.getGroupName());
                msg.setAlarmDesc("回调成功率低于前" + previousDays + "天平均值的" + config.getCallbackSuccessRate() + "%");
                msg.setAlarmType("回调成功率");
                msg.setAlarmSimpleDesc("回调");
                msg.setValue(dto.getCallbackSuccessRate());
                msg.setThreshold(callbackCompareVal);
                String valueDesc = new StringBuilder()
                        .append(dto.getCallbackSuccessRate()).append("%").append(" ").append("(")
                        .append(dto.getCallbackSuccessCount()).append("/")
                        .append(dto.getProcessSuccessCount()).append(")").toString();
                msg.setValueDesc(valueDesc);
                String thresholdDesc = new StringBuilder()
                        .append(callbackCompareVal).append("%").append(" ").append("(")
                        .append(compareDTO.getPreviousCallbackSuccessAvgCount()).append("/")
                        .append(compareDTO.getPreviousProcessSuccessAvgCount()).append("*")
                        .append(new BigDecimal(config.getCallbackSuccessRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
                msg.setThresholdDesc(thresholdDesc);

                calcOffsetAndLevel(processCompareVal, msg, dto.getCallbackSuccessRate());
                msgList.add(msg);
            }

        }
        msgList = msgList.stream().sorted(Comparator.comparing(TaskStatAccessAlarmMsgDTO::getGroupName)).collect(Collectors.toList());
        return msgList;
    }

    private void calcOffsetAndLevel(BigDecimal compareVal, TaskStatAccessAlarmMsgDTO msg, BigDecimal actualVal) {
        if (BigDecimal.ZERO.compareTo(compareVal) == 0) {
            msg.setOffset(BigDecimal.ZERO);
            msg.setAlarmLevel(EAlarmLevel.info);
        } else {
            BigDecimal value = BigDecimal.valueOf(100).subtract(actualVal.multiply(BigDecimal.valueOf(100)).divide(compareVal, 2, BigDecimal.ROUND_HALF_UP));
            msg.setOffset(value);
            determineLevel(msg, value);
        }
    }

    /**
     * 确定预警等级
     *
     * @param msg   预警消息
     * @param value offset 某一属性的偏转值
     */
    private void determineLevel(TaskStatAccessAlarmMsgDTO msg, BigDecimal value) {
        if (value.compareTo(BigDecimal.valueOf(diamondConfig.getErrorLower())) >= 0 && "中国联通".equals(msg.getGroupName())) {
            msg.setAlarmLevel(EAlarmLevel.error);
        } else if ("中国联通".equals(msg.getGroupName())) {
            msg.setAlarmLevel(EAlarmLevel.warning);
        } else {
            msg.setAlarmLevel(EAlarmLevel.info);
        }
    }


    /**
     * 确定预警等级
     *
     * @param msg   预警消息
     * @param value offset 某一属性的偏转值
     */
    private void determineAllLevel(TaskStatAccessAlarmMsgDTO msg, BigDecimal value) {
        if (value.compareTo(BigDecimal.valueOf(diamondConfig.getErrorLower())) >= 0) {
            msg.setAlarmLevel(EAlarmLevel.error);
        } else if (value.compareTo(BigDecimal.valueOf(diamondConfig.getWarningLower())) > 0) {
            msg.setAlarmLevel(EAlarmLevel.warning);
        } else {
            msg.setAlarmLevel(EAlarmLevel.info);
        }
    }

    /**
     * 判断当前环节是否出发预警
     *
     * @param num         上一个环节的数量,分0-5,5-无穷的情况
     * @param rate        当前环节指标值
     * @param compareRate 当前环节阀值
     * @return
     */
    private Boolean isAlarm(Integer num, BigDecimal rate, BigDecimal compareRate) {
        Integer fewNum = diamondConfig.getOperatorAlarmFewNum();
        BigDecimal fewThresholdPercent = BigDecimal.valueOf(diamondConfig.getOperatorAlarmFewThresholdPercent());
        //上一个环节任务数量=0,不预警
        if (num == 0) {
            return false;
        }
        //统计数量较少,且大于设定的特殊阈值,不预警
        if (num > 0 && num < fewNum) {
            if (rate.compareTo(fewThresholdPercent) >= 0) {
                return false;
            }
        }
        //统计数量正常,且大于历史平均阈值,不预警
        if (num >= fewNum) {
            if (rate.compareTo(compareRate) >= 0) {
                return false;
            }
        }
        return true;
    }


}
