package com.treefinance.saas.monitor.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.commonservice.uid.UidGenerator;
import com.treefinance.saas.monitor.biz.config.EmailAlarmConfig;
import com.treefinance.saas.monitor.biz.helper.EmailMonitorKeyHelper;
import com.treefinance.saas.monitor.biz.service.AbstractAlarmServiceTemplate;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.domain.dto.*;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.BaseAlarmConfigDTO;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.EmailMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.EmailMonitorAlarmTimeConfigDTO;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.MonitorAlarmLevelConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.*;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.common.utils.StatisticCalcUtil;
import com.treefinance.saas.monitor.dao.entity.AlarmRecord;
import com.treefinance.saas.monitor.dao.entity.EmailStatAccess;
import com.treefinance.saas.monitor.dao.entity.EmailStatAccessCriteria;
import com.treefinance.saas.monitor.exception.BizException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.treefinance.saas.monitor.common.constants.AlarmConstants.ALL_EMAIL;
import static com.treefinance.saas.monitor.common.constants.AlarmConstants.GROUP_EMAIL;
import static com.treefinance.saas.monitor.common.constants.AlarmConstants.SWITCH_ON;
import static java.util.stream.Collectors.groupingBy;

/**
 * @author chengtong
 * @date 18/3/13 10:25
 */
@Service("emailAlarmMonitorService")
public class EmailAlarmTemplateImpl extends AbstractAlarmServiceTemplate {

    private static final Logger logger = LoggerFactory.getLogger(EmailAlarmTemplateImpl.class);

    @Autowired
    private EmailAlarmConfig emailAlarmConfig;


    @Override
    public String getKey(ETaskStatDataType type, Date jobTime, BaseAlarmConfigDTO baseAlarmConfigDTO) {
        EmailMonitorAlarmConfigDTO configDTO = (EmailMonitorAlarmConfigDTO) baseAlarmConfigDTO;
        String[] emails = configDTO.getEmails().toArray(new String[configDTO.getEmails().size()]);

        return EmailMonitorKeyHelper.genEmailAllKey(jobTime, "virtual_total_stat_appId", type, ALL_EMAIL.equals(emails[0]) ? ALL_EMAIL : GROUP_EMAIL);
    }

    @Override
    public List<BaseStatAccessDTO> getBaseData(Date baseTime, ETaskStatDataType statDataType, BaseAlarmConfigDTO alarmConfigDTO) {

        EmailMonitorAlarmConfigDTO configDTO = (EmailMonitorAlarmConfigDTO) alarmConfigDTO;
        Date startTime = DateUtils.addMinutes(baseTime, -configDTO.getIntervalMins());

        List<BaseStatAccessDTO> result = doGetBaseData(baseTime, startTime, statDataType, configDTO);

        //是否需要预警？
        if (result.isEmpty()) {
            logger.info("邮箱监控,预警定时任务执行jobTime={},要统计的数据时刻startTime={},endTime={},此段时间内,未查询到所有邮箱的统计数据",
                    MonitorDateUtils.format(new Date()), MonitorDateUtils.format(startTime), MonitorDateUtils.format
                            (baseTime));
            throw new BizException("没有原始数据 无需预警");
        }

        return result;
    }

    private List<BaseStatAccessDTO> doGetBaseData(Date baseTime, Date startTime, ETaskStatDataType statDataType,
                                                  EmailMonitorAlarmConfigDTO configDTO) {
        EmailStatAccessCriteria criteria = new EmailStatAccessCriteria();
        List<String> emails = configDTO.getEmails();
        criteria.createCriteria().andDataTypeEqualTo(statDataType.getCode())
                .andAppIdEqualTo(AlarmConstants.VIRTUAL_TOTAL_STAT_APP_ID).andEmailIn(emails)
                .andDataTimeGreaterThanOrEqualTo(startTime)
                .andDataTimeLessThan(baseTime);

        List<EmailStatAccess> list = emailStatAccessMapper.selectByExample(criteria);
        if (list.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, List<EmailStatAccess>> emailDataMap = list.stream().collect(groupingBy(EmailStatAccess::getEmail));
        List<BaseStatAccessDTO> result = new ArrayList<>();
        for (String key : emailDataMap.keySet()) {

            EmailStatAccess emailStatAccess = emailDataMap.get(key).get(0);
            EmailStatAccessDTO dataDTO = DataConverterUtils.convert(emailStatAccess, EmailStatAccessDTO.class);

            int entryCount = 0, startLoginCount = 0, loginSuccessCount = 0,
                    crawlSuccessCount = 0, processSuccessCount = 0, callbackSuccessCount = 0;
            for (EmailStatAccess item : emailDataMap.get(key)) {
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
            dataDTO.setLoginConversionRate(StatisticCalcUtil.calcRate(startLoginCount, entryCount));
            dataDTO.setLoginSuccessRate(StatisticCalcUtil.calcRate(loginSuccessCount, startLoginCount));
            dataDTO.setCrawlSuccessRate(StatisticCalcUtil.calcRate(crawlSuccessCount, loginSuccessCount));
            dataDTO.setProcessSuccessRate(StatisticCalcUtil.calcRate(processSuccessCount, crawlSuccessCount));
            dataDTO.setCallbackSuccessRate(StatisticCalcUtil.calcRate(callbackSuccessCount, processSuccessCount));
            dataDTO.setWholeConversionRate(StatisticCalcUtil.calcRate(callbackSuccessCount, entryCount));
            result.add(dataDTO);
        }
        return result;
    }

    @Override
    protected Map<String, BaseStatAccessDTO> getPreviousCompareDataMap(Date baseTime, List<BaseStatAccessDTO>
            dtoList, BaseAlarmConfigDTO baseAlarmConfigDTO, ETaskStatDataType statType) {

        EmailMonitorAlarmConfigDTO configDTO = (EmailMonitorAlarmConfigDTO) baseAlarmConfigDTO;
        List<String> emails = configDTO.getEmails();
        Integer previousDays = configDTO.getPreviousDays();
        List<Date> previousOClockList = MonitorDateUtils.getPreviousOClockTime(baseTime, previousDays);
        List<EmailStatAccessDTO> previousDTOList = Lists.newArrayList();

        for (Date previousOClock : previousOClockList) {
            Date startTime = DateUtils.addMinutes(previousOClock, -configDTO.getIntervalMins());
            List<BaseStatAccessDTO> list = doGetBaseData(previousOClock, startTime, statType, configDTO);
            if (list.isEmpty()) {
                continue;
            }
            previousDTOList.addAll(list.stream().map(baseStatAccessDTO -> (EmailStatAccessDTO) baseStatAccessDTO).collect(Collectors.toList()));
        }

        if (CollectionUtils.isEmpty(previousDTOList)) {
            logger.info("邮箱监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},在此时间前{}天内,未查询到区分邮箱的统计数据emailList={}," +
                            "previousOClockList={},list={}",
                    MonitorDateUtils.format(new Date()), MonitorDateUtils.format(baseTime), previousDays, JSON
                            .toJSONString(emails),
                    JSON.toJSONString(previousOClockList), JSON.toJSONString(previousDTOList));
            return Maps.newHashMap();
        }


        Map<String, List<EmailStatAccessDTO>> previousMap = previousDTOList.stream().collect(groupingBy(EmailStatAccessDTO
                ::getEmail));
        Map<String, BaseStatAccessDTO> compareMap = Maps.newHashMap();

        for (Map.Entry<String, List<EmailStatAccessDTO>> entry : previousMap.entrySet()) {
            List<EmailStatAccessDTO> entryList = entry.getValue();
            if (CollectionUtils.isEmpty(entryList)) {
                logger.info("邮箱监控,邮箱类型为{}的没有统计数据",
                        JSON.toJSONString(emails));
                continue;
            }
            //如果列表数量大于1,则去掉相同时段最低的数据,再取平均值,排除数据异常情况.
            if (entryList.size() > 1) {
                entryList = entryList.stream()
                        .sorted((o1, o2) -> o2.getEntryCount().compareTo(o1.getEntryCount()))
                        .collect(Collectors.toList());
                entryList.remove(entryList.size() - 1);
            }
            BigDecimal previousLoginConversionRateCount = BigDecimal.ZERO;
            BigDecimal previousLoginSuccessRateCount = BigDecimal.ZERO;
            BigDecimal previousCrawlSuccessRateCount = BigDecimal.ZERO;
            BigDecimal previousProcessSuccessRateCount = BigDecimal.ZERO;
            BigDecimal previousCallbackSuccessRateCount = BigDecimal.ZERO;
            BigDecimal previousWholeConversionRateCount = BigDecimal.ZERO;


            Integer previousEntryCount = 0, previousStartLoginCount = 0, previousLoginSuccessCount = 0,
                    previousCrawlSuccessCount = 0, previousProcessSuccessCount = 0, previousCallbackSuccessCount = 0;
            for (EmailStatAccessDTO dto : entryList) {
                previousLoginConversionRateCount = previousLoginConversionRateCount.add(dto.getLoginConversionRate());
                previousLoginSuccessRateCount = previousLoginSuccessRateCount.add(dto.getLoginSuccessRate());
                previousCrawlSuccessRateCount = previousCrawlSuccessRateCount.add(dto.getCrawlSuccessRate());
                previousProcessSuccessRateCount = previousProcessSuccessRateCount.add(dto.getProcessSuccessRate());
                previousCallbackSuccessRateCount = previousCallbackSuccessRateCount.add(dto.getCallbackSuccessRate());
                previousWholeConversionRateCount = previousWholeConversionRateCount.add(dto.getWholeConversionRate());

                previousEntryCount = previousEntryCount + dto.getEntryCount();
                previousStartLoginCount = previousStartLoginCount + dto.getStartLoginCount();
                previousLoginSuccessCount = previousLoginSuccessCount + dto.getLoginSuccessCount();
                previousCrawlSuccessCount = previousCrawlSuccessCount + dto.getCrawlSuccessCount();
                previousProcessSuccessCount = previousProcessSuccessCount + dto.getProcessSuccessCount();
                previousCallbackSuccessCount = previousCallbackSuccessCount + dto.getCallbackSuccessCount();
            }
            EmailStatAccessDTO compareDto = new EmailStatAccessDTO();
            compareDto.setEmail(entry.getKey());
            compareDto.setPreviousLoginConversionRate(previousLoginConversionRateCount.divide(BigDecimal.valueOf(entryList.size()), 2, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousLoginSuccessRate(previousLoginSuccessRateCount.divide(BigDecimal.valueOf(entryList.size()), 2, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousCrawlSuccessRate(previousCrawlSuccessRateCount.divide(BigDecimal.valueOf(entryList.size()), 2, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousProcessSuccessRate(previousProcessSuccessRateCount.divide(BigDecimal.valueOf(entryList.size()), 2, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousCallbackSuccessRate(previousCallbackSuccessRateCount.divide(BigDecimal.valueOf(entryList.size()), 2, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousWholeConversionRate(previousWholeConversionRateCount.divide(BigDecimal.valueOf(entryList.size()), 2, BigDecimal.ROUND_HALF_UP));

            compareDto.setPreviousEntryAvgCount(BigDecimal.valueOf(previousEntryCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousStartLoginAvgCount(BigDecimal.valueOf(previousStartLoginCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousLoginSuccessAvgCount(BigDecimal.valueOf(previousLoginSuccessCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousCrawlSuccessAvgCount(BigDecimal.valueOf(previousCrawlSuccessCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousProcessSuccessAvgCount(BigDecimal.valueOf(previousProcessSuccessCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousCallbackSuccessAvgCount(BigDecimal.valueOf(previousCallbackSuccessCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareMap.put(entry.getKey(), compareDto);
        }


        //是否需要预警？
        logger.info("邮箱监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},获取前n天内,相同时刻区分邮箱统计的平均值compareMap={}",
                MonitorDateUtils.format(new Date()), MonitorDateUtils.format(baseTime), JSON.toJSONString(compareMap));
        if (MapUtils.isEmpty(compareMap)) {
            throw new BizException("过去七天没有需要平均值数据");
        }

        return compareMap;
    }


    @Override
    protected List<BaseAlarmMsgDTO> getAlarmMsgList(Date now, List<BaseStatAccessDTO> dtoList, Map<String, BaseStatAccessDTO>
            compareMap, BaseAlarmConfigDTO configDTO) {

        EmailMonitorAlarmConfigDTO emailMonitorAlarmConfigDTO = (EmailMonitorAlarmConfigDTO) configDTO;


        EmailMonitorAlarmTimeConfigDTO timeConfigDTO = emailMonitorAlarmConfigDTO.getList().stream().filter
                (EmailMonitorAlarmTimeConfigDTO::isInTime).collect
                (Collectors.toList()).get(0);

        Integer previousDays = emailMonitorAlarmConfigDTO.getPreviousDays();

        List<BaseAlarmMsgDTO> msgList = Lists.newArrayList();

        for (BaseStatAccessDTO baseStatAccessDTO : dtoList) {
            EmailStatAccessDTO sourceDto = (EmailStatAccessDTO) baseStatAccessDTO;
            if (compareMap.get(sourceDto.getEmail()) == null) {
                logger.info("邮箱监控,预警定时任务执行jobTime={},groupCode={}的邮箱数据前{}天未查询到统计数据",
                        MonitorDateUtils.format(now), sourceDto.getEmail(), previousDays);
                continue;
            }
            //获取当前的某一个邮箱的前七天的比较值
            EmailStatAccessDTO compareDTO = (EmailStatAccessDTO) compareMap.get(sourceDto.getEmail());
            BigDecimal loginConversionCompareVal = compareDTO.getPreviousLoginConversionRate().multiply(new BigDecimal(timeConfigDTO.getLoginConversionRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal loginSuccessCompareVal = compareDTO.getPreviousLoginSuccessRate().multiply(new BigDecimal(timeConfigDTO.getLoginSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal crawlCompareVal = compareDTO.getPreviousCrawlSuccessRate().multiply(new BigDecimal(timeConfigDTO.getCrawlSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal processCompareVal = compareDTO.getPreviousProcessSuccessRate().multiply(new BigDecimal(timeConfigDTO.getProcessSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal callbackCompareVal = compareDTO.getPreviousCallbackSuccessRate().multiply(new BigDecimal(timeConfigDTO.getCallbackSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal wholeConversionCompareVal = compareDTO.getPreviousWholeConversionRate().multiply(new BigDecimal(timeConfigDTO.getWholeConversionRate())).divide(new BigDecimal(100), 2, BigDecimal
                    .ROUND_HALF_UP);


            //大盘不需要计算 这些
            if (!sourceDto.getEmail().equals(AlarmConstants.ALL_EMAIL)) {
                //登录转化率小于前7天平均值
                calcConversionRate(emailMonitorAlarmConfigDTO, timeConfigDTO, previousDays, msgList, sourceDto, compareDTO, loginConversionCompareVal);
                //登录成功率小于前7天平均值
                calcLoginSuccessRate(emailMonitorAlarmConfigDTO, timeConfigDTO, previousDays, msgList, sourceDto, compareDTO, loginSuccessCompareVal);
                //抓取成功率小于前7天平均值
                calcCrawSuccessRate(emailMonitorAlarmConfigDTO, timeConfigDTO, previousDays, msgList, sourceDto, compareDTO, crawlCompareVal);
                //洗数成功率小于前7天平均值
                calcProcessRate(emailMonitorAlarmConfigDTO, timeConfigDTO, previousDays, msgList, sourceDto, compareDTO, processCompareVal);
                //回调成功率
                calcCallbackSuccessRate(emailMonitorAlarmConfigDTO, timeConfigDTO, previousDays, msgList, sourceDto, compareDTO, processCompareVal, callbackCompareVal);
            }

            calcWholeConvert(timeConfigDTO, previousDays, msgList, sourceDto, compareDTO, wholeConversionCompareVal);

        }
        msgList = msgList.stream().map(baseAlarmMsgDTO -> (EmailAlarmMsgDTO) baseAlarmMsgDTO).sorted(Comparator.comparing(EmailAlarmMsgDTO
                ::getEmail)).collect(Collectors.toList());
        return msgList;
    }

    private void calcWholeConvert(EmailMonitorAlarmTimeConfigDTO
                                          timeConfigDTO, Integer previousDays, List<BaseAlarmMsgDTO> msgList, EmailStatAccessDTO sourceDto, EmailStatAccessDTO
                                          compareDTO, BigDecimal wholeConversionCompareVal) {
        if (sourceDto.getWholeConversionRate().compareTo(wholeConversionCompareVal) < 0) {
            EmailAlarmMsgDTO msg = new EmailAlarmMsgDTO();
            String email = AlarmConstants.ALL_EMAIL.equals(sourceDto.getEmail()) ? AlarmConstants.ALL_EMAIL_FLAG : sourceDto
                    .getEmail();
            msg.setEmail(email);
            msg.setAlarmDesc("总转化率低于前" + previousDays + "天平均值的" + timeConfigDTO.getWholeConversionRate() + "%");
            msg.setAlarmType("总转化率");
            msg.setAlarmSimpleDesc("总转化率");
            msg.setValue(sourceDto.getWholeConversionRate());
            msg.setThreshold(wholeConversionCompareVal);
            String valueDesc = String.valueOf(sourceDto.getWholeConversionRate()) + "%" + " " + "(" +
                    sourceDto.getCallbackSuccessCount() + "/" +
                    sourceDto.getEntryCount() + ")";
            msg.setValueDesc(valueDesc);
            String thresholdDesc = String.valueOf(wholeConversionCompareVal) + "%" + " " + "(" +
                    compareDTO.getPreviousCallbackSuccessAvgCount() + "/" +
                    compareDTO.getPreviousEntryAvgCount() + "*" +
                    new BigDecimal(timeConfigDTO.getWholeConversionRate()).divide(new BigDecimal(100), 1, BigDecimal
                            .ROUND_HALF_UP) +
                    ")";
            msg.setThresholdDesc(thresholdDesc);
            calcOffsetAndLevel(wholeConversionCompareVal, msg, sourceDto.getWholeConversionRate());
            msgList.add(msg);

        }
    }

    private void calcCallbackSuccessRate(EmailMonitorAlarmConfigDTO configDTO, EmailMonitorAlarmTimeConfigDTO
            timeConfigDTO, Integer previousDays, List<BaseAlarmMsgDTO> msgList, EmailStatAccessDTO sourceDto, EmailStatAccessDTO
                                                 compareDTO, BigDecimal processCompareVal, BigDecimal callbackCompareVal) {
        if (isAlarm(sourceDto.getCallbackSuccessCount(),
                sourceDto.getCallbackSuccessRate(),
                callbackCompareVal,
                configDTO.getFewNum(), configDTO.getThreshold())) {
            EmailAlarmMsgDTO msg = new EmailAlarmMsgDTO();
            String email = AlarmConstants.ALL_EMAIL.equals(sourceDto.getEmail()) ? "邮箱大盘" : sourceDto.getEmail();
            msg.setEmail(email);
            msg.setAlarmDesc("回调成功率低于前" + previousDays + "天平均值的" + timeConfigDTO.getCallbackSuccessRate() + "%");
            msg.setAlarmType("回调成功率");
            msg.setAlarmSimpleDesc("回调");
            msg.setValue(sourceDto.getCallbackSuccessRate());
            msg.setThreshold(callbackCompareVal);
            String valueDesc = String.valueOf(sourceDto.getCallbackSuccessRate()) + "%" + " " + "(" +
                    sourceDto.getCallbackSuccessCount() + "/" +
                    sourceDto.getProcessSuccessCount() + ")";
            msg.setValueDesc(valueDesc);
            String thresholdDesc = String.valueOf(callbackCompareVal) + "%" + " " + "(" +
                    compareDTO.getPreviousCallbackSuccessAvgCount() + "/" +
                    compareDTO.getPreviousProcessSuccessAvgCount() + "*" +
                    new BigDecimal(timeConfigDTO.getCallbackSuccessRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP) + ")";
            msg.setThresholdDesc(thresholdDesc);

            calcOffsetAndLevel(processCompareVal, msg, sourceDto.getCallbackSuccessRate());
            msgList.add(msg);
        }
    }

    private void calcProcessRate(EmailMonitorAlarmConfigDTO configDTO, EmailMonitorAlarmTimeConfigDTO timeConfigDTO, Integer previousDays, List<BaseAlarmMsgDTO> msgList, EmailStatAccessDTO sourceDto, EmailStatAccessDTO compareDTO, BigDecimal processCompareVal) {
        if (isAlarm(sourceDto.getCrawlSuccessCount(), sourceDto.getProcessSuccessRate(), processCompareVal, configDTO.getFewNum(), configDTO.getThreshold())) {
            EmailAlarmMsgDTO msg = new EmailAlarmMsgDTO();
            String email = AlarmConstants.ALL_EMAIL.equals(sourceDto.getEmail()) ? "邮箱大盘" : sourceDto.getEmail();
            msg.setEmail(email);
            msg.setAlarmDesc("洗数成功率低于前" + previousDays + "天平均值的" + timeConfigDTO.getProcessSuccessRate() + "%");
            msg.setAlarmType("洗数成功率");
            msg.setAlarmSimpleDesc("洗数");
            msg.setValue(sourceDto.getProcessSuccessRate());
            msg.setThreshold(processCompareVal);
            String valueDesc = String.valueOf(sourceDto.getProcessSuccessRate()) + "%" + " " + "(" +
                    sourceDto.getProcessSuccessCount() + "/" +
                    sourceDto.getCrawlSuccessCount() + ")";
            msg.setValueDesc(valueDesc);
            String thresholdDesc = String.valueOf(processCompareVal) + "%" + " " + "(" +
                    compareDTO.getPreviousProcessSuccessAvgCount() + "/" +
                    compareDTO.getPreviousCrawlSuccessAvgCount() + "*" +
                    new BigDecimal(timeConfigDTO.getProcessSuccessRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP) + ")";
            msg.setThresholdDesc(thresholdDesc);

            calcOffsetAndLevel(processCompareVal, msg, sourceDto.getProcessSuccessRate());
            msgList.add(msg);
        }
    }

    private void calcCrawSuccessRate(EmailMonitorAlarmConfigDTO configDTO, EmailMonitorAlarmTimeConfigDTO timeConfigDTO, Integer previousDays, List<BaseAlarmMsgDTO> msgList, EmailStatAccessDTO sourceDto, EmailStatAccessDTO compareDTO, BigDecimal crawlCompareVal) {
        if (isAlarm(sourceDto.getLoginSuccessCount(), sourceDto.getCrawlSuccessRate(), crawlCompareVal, configDTO.getFewNum(), configDTO.getThreshold())) {
            EmailAlarmMsgDTO msg = new EmailAlarmMsgDTO();
            String email = AlarmConstants.ALL_EMAIL.equals(sourceDto.getEmail()) ? "邮箱大盘" : sourceDto.getEmail();
            msg.setEmail(email);
            msg.setAlarmDesc("抓取成功率低于前" + previousDays + "天平均值的" + timeConfigDTO.getCrawlSuccessRate() + "%");
            msg.setAlarmType("抓取成功率");
            msg.setAlarmSimpleDesc("抓取");
            msg.setValue(sourceDto.getCrawlSuccessRate());
            msg.setThreshold(crawlCompareVal);
            String valueDesc = String.valueOf(sourceDto.getCrawlSuccessRate()) + "%" + " " + "(" +
                    sourceDto.getCrawlSuccessCount() + "/" +
                    sourceDto.getLoginSuccessCount() + ")";
            msg.setValueDesc(valueDesc);
            String thresholdDesc = String.valueOf(crawlCompareVal) + "%" + " " + "(" +
                    compareDTO.getPreviousCrawlSuccessAvgCount() + "/" +
                    compareDTO.getPreviousLoginSuccessAvgCount() + "*" +
                    new BigDecimal(timeConfigDTO.getCrawlSuccessRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP) + ")";
            msg.setThresholdDesc(thresholdDesc);

            calcOffsetAndLevel(crawlCompareVal, msg, sourceDto.getCrawlSuccessRate());
            msgList.add(msg);
        }
    }

    private void calcLoginSuccessRate(EmailMonitorAlarmConfigDTO configDTO, EmailMonitorAlarmTimeConfigDTO timeConfigDTO, Integer previousDays, List<BaseAlarmMsgDTO> msgList, EmailStatAccessDTO sourceDto, EmailStatAccessDTO compareDTO, BigDecimal loginSuccessCompareVal) {
        if (isAlarm(sourceDto.getStartLoginCount(), sourceDto.getLoginSuccessRate(), loginSuccessCompareVal, configDTO.getFewNum(), configDTO.getThreshold())) {
            EmailAlarmMsgDTO msg = new EmailAlarmMsgDTO();
            String email = AlarmConstants.ALL_EMAIL.equals(sourceDto.getEmail()) ? "邮箱大盘" : sourceDto.getEmail();
            msg.setEmail(email);
            msg.setAlarmDesc("登陆成功率低于前" + previousDays + "天平均值的" + timeConfigDTO.getLoginSuccessRate() + "%");
            msg.setAlarmType("登陆成功率");
            msg.setAlarmSimpleDesc("登陆");
            msg.setValue(sourceDto.getLoginSuccessRate());
            msg.setThreshold(loginSuccessCompareVal);
            String valueDesc = String.valueOf(sourceDto.getLoginSuccessRate()) + "%" + " " + "(" +
                    sourceDto.getLoginSuccessCount() + "/" +
                    sourceDto.getStartLoginCount() + ")";
            msg.setValueDesc(valueDesc);
            String thresholdDesc = String.valueOf(loginSuccessCompareVal) + "%" + " " + "(" +
                    compareDTO.getPreviousLoginSuccessAvgCount() + "/" +
                    compareDTO.getPreviousStartLoginAvgCount() + "*" +
                    new BigDecimal(timeConfigDTO.getLoginSuccessRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP) + ")";
            msg.setThresholdDesc(thresholdDesc);
            calcOffsetAndLevel(loginSuccessCompareVal, msg, sourceDto.getLoginSuccessRate());
            msgList.add(msg);
        }
    }

    private void calcConversionRate(EmailMonitorAlarmConfigDTO configDTO, EmailMonitorAlarmTimeConfigDTO timeConfigDTO, Integer previousDays, List<BaseAlarmMsgDTO> msgList, EmailStatAccessDTO sourceDto, EmailStatAccessDTO compareDTO, BigDecimal loginConversionCompareVal) {
        if (isAlarm(sourceDto.getEntryCount(), sourceDto.getLoginConversionRate(), loginConversionCompareVal,
                configDTO.getFewNum(), configDTO.getThreshold())) {
            EmailAlarmMsgDTO msg = new EmailAlarmMsgDTO();
            String email = AlarmConstants.ALL_EMAIL.equals(sourceDto.getEmail()) ? "邮箱大盘" : sourceDto.getEmail();
            msg.setEmail(email);
            msg.setAlarmDesc("登陆转化率低于前" + previousDays + "天平均值的" + timeConfigDTO.getLoginConversionRate() + "%");
            msg.setAlarmType("登陆转化率");
            msg.setAlarmSimpleDesc("开始登陆");
            msg.setValue(sourceDto.getLoginConversionRate());
            msg.setThreshold(loginConversionCompareVal);
            String valueDesc = String.valueOf(sourceDto.getLoginConversionRate()) + "%" + " " + "(" +
                    sourceDto.getStartLoginCount() + "/" +
                    sourceDto.getEntryCount() + ")";
            msg.setValueDesc(valueDesc);
            String thresholdDesc = String.valueOf(loginConversionCompareVal) + "%" + " " + "(" +
                    compareDTO.getPreviousStartLoginAvgCount() + "/" +
                    compareDTO.getPreviousEntryAvgCount() + "*" +
                    new BigDecimal(timeConfigDTO.getLoginConversionRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP) + ")";
            msg.setThresholdDesc(thresholdDesc);
            calcOffsetAndLevel(loginConversionCompareVal, msg, sourceDto.getLoginConversionRate());
            msgList.add(msg);
        }
    }

    private void calcOffsetAndLevel(BigDecimal compareVal, EmailAlarmMsgDTO msg, BigDecimal actualVal) {
        if (BigDecimal.ZERO.compareTo(compareVal) == 0) {
            msg.setOffset(BigDecimal.ZERO);
            msg.setAlarmLevel(EAlarmLevel.info);
        } else {
            BigDecimal value = BigDecimal.ONE.subtract(actualVal.divide(compareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
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
    private void determineLevel(EmailAlarmMsgDTO msg, BigDecimal value) {

        boolean isAll = "邮箱大盘".equals(msg.getEmail());

        BigDecimal warningUpper = isAll ? BigDecimal.valueOf(emailAlarmConfig.getAllWarnning()) : BigDecimal.valueOf
                (emailAlarmConfig.getGroupWarning());
        BigDecimal infoUpper = isAll ? BigDecimal.valueOf(emailAlarmConfig.getAllInfo()) : BigDecimal.valueOf
                (emailAlarmConfig.getGroupInfo());

        if (value.compareTo(warningUpper) >= 0) {
            msg.setAlarmLevel(EAlarmLevel.error);
        } else if (value.compareTo(infoUpper) >= 0) {
            msg.setAlarmLevel(EAlarmLevel.warning);
        } else {
            msg.setAlarmLevel(EAlarmLevel.info);
        }

    }


    @Override
    protected String sendAlarmMsg(EAlarmLevel alarmLevel, List<BaseAlarmMsgDTO> dtoList, BaseAlarmConfigDTO
            configDTO, Date endTime, ETaskStatDataType statDataType) {

        String returnBody = "";

        EmailMonitorAlarmConfigDTO emailConfig = (EmailMonitorAlarmConfigDTO) configDTO;
        Date startTime = DateUtils.addMinutes(endTime, -configDTO.getIntervalMins());
        MonitorAlarmLevelConfigDTO levelConfig = emailConfig.getLevelConfig().stream().filter
                (emailMonitorAlarmLevelConfigDTO ->
                        alarmLevel.name().equals(emailMonitorAlarmLevelConfigDTO.getLevel())).findFirst().orElse(null);
        if (levelConfig == null) {
            logger.info("邮箱预警配置 预警等级配置 为空");
            return null;
        }


        HashMap<String, String> switches = emailConfig.getSwitches();
        if (switches == null || switches.isEmpty() || switches.values().stream().noneMatch(SWITCH_ON::equals)) {
            logger.info("邮箱预警配置 为空 或者预警信息发送渠道全部关闭。。");
            return null;
        }


        List<String> channels = levelConfig.getChannels();
        String alarmBiz;
        if (AlarmConstants.ALL_EMAIL_FLAG.equals(((EmailAlarmMsgDTO) dtoList.get(0)).getEmail())) {
            alarmBiz = "邮箱大盘";
        } else {
            alarmBiz = "邮箱分组";
        }
        alarmBiz += "-" + statDataType.getText();

        for (String channel : channels) {

            EAlarmChannel alarmChannel = EAlarmChannel.getByValue(channel);
            if (alarmChannel == null) {
                logger.info("配置错误 无法找到对应的预警渠道");
                return null;
            }
            switch (alarmChannel) {
                case IVR:
                    if (!SWITCH_ON.equals(switches.get(alarmChannel.getValue()))) {
                        logger.info("邮箱预警配置 " + alarmChannel.getValue() + "已关闭");
                        continue;
                    }
                    sendIvr(dtoList, alarmLevel, alarmBiz);
                    break;
                case SMS:
                    if (!SWITCH_ON.equals(switches.get(alarmChannel.getValue()))) {
                        logger.info("邮箱预警配置 " + alarmChannel.getValue() + "已关闭");
                        continue;
                    }
                    sendSms(dtoList, startTime, endTime, alarmLevel, alarmBiz);
                    break;
                case WECHAT:
                    if (!SWITCH_ON.equals(switches.get(alarmChannel.getValue()))) {
                        logger.info("邮箱预警配置 " + alarmChannel.getValue() + "已关闭");
                        continue;
                    }
                    returnBody = sendWeChat(dtoList, startTime, endTime, alarmLevel, alarmBiz);
                    break;
                case EMAIL:
                    if (!SWITCH_ON.equals(switches.get(alarmChannel.getValue()))) {
                        logger.info("邮箱预警配置 " + alarmChannel.getValue() + "已关闭");
                        continue;
                    }
                    sendMail(dtoList, new Date(), startTime, endTime, alarmLevel, alarmBiz);
                    break;
                default:
                    logger.info("不支持的邮箱预警渠道" + alarmChannel.getValue());
                    break;
            }
        }


        return returnBody;
    }

    private String generateMailDataBody(List<BaseAlarmMsgDTO> msgList, Date startTime, Date endTime, Map<String,
            Object> placeHolder, EAlarmLevel alarmLevel, String alarmBiz) {
        StringBuilder pageHtml = new StringBuilder();

        StringBuilder tableTrs = new StringBuilder();
        //title里面的具体内容
        StringBuilder detail = new StringBuilder();

        detail.append("【");
        for (BaseAlarmMsgDTO msg : msgList) {
            tableTrs.append("<tr>").append("<td>").append(((EmailAlarmMsgDTO) msg).getEmail()).append("</td>")
                    .append("<td>").append(msg.getAlarmDesc()).append(" ").append("</td>")
                    .append("<td>").append(msg.getValueDesc()).append(" ").append("</td>")
                    .append("<td>").append(msg.getThresholdDesc()).append(" ").append("</td>")
                    .append("<td>").append(msg.getOffset()).append("%").append(" ").append("</td>").append("</tr>");
            detail.append(((EmailAlarmMsgDTO) msg).getEmail()).append("-").append(msg.getAlarmType()).append("(").append(msg.getOffset
                    ()).append("%").append(")").append("，");
        }
        String detailsStr = detail.substring(0, detail.length() - 1);
        detailsStr += "】";

        String module = "saas-" + diamondConfig.getMonitorEnvironment();
        pageHtml.append("<br>").append("【").append(alarmLevel.name()).append("】").append
                ("您好，").append
                (module)
                .append(alarmBiz)
                .append("预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段数据存在问题").append("，此时监控数据如下，请及时处理：").append("</br>");
        pageHtml.append("<table border=\"1\" cellspacing=\"0\" bordercolor=\"#BDBDBD\" >");
        pageHtml.append("<tr bgcolor=\"#C9C9C9\">")
                .append("<th>").append("邮箱").append("</th>")
                .append("<th>").append("预警描述").append("</th>")
                .append("<th>").append("当前指标值").append("</th>")
                .append("<th>").append("指标阀值").append("</th>")
                .append("<th>").append("偏离阀值程度").append("</th>")
                .append("</tr>");

        pageHtml.append(tableTrs);
        pageHtml.append("</table>");


        placeHolder.put("module", module);
        placeHolder.put("detail", detailsStr);

        return pageHtml.toString();
    }

    private String generateWeChatBody(List<BaseAlarmMsgDTO> msgList, Date startTime, Date endTime, EAlarmLevel
            alarmLevel, String alarmBiz) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("【").append(alarmLevel.name()).append("】")
                .append("您好，").append("saas-").append(diamondConfig.getMonitorEnvironment())
                .append(alarmBiz)
                .append("预警,在")
                .append(MonitorDateUtils.format(startTime))
                .append("--")
                .append(MonitorDateUtils.format(endTime))
                .append("时段数据存在问题").append("，此时监控数据如下，请及时处理：").append("\n");
        for (BaseAlarmMsgDTO msg : msgList) {
            buffer.append("【").append(((EmailAlarmMsgDTO) msg).getEmail()).append("】").append("【").append(msg
                    .getAlarmSimpleDesc())
                    .append("】")
                    .append("当前指标值:").append("【").append(msg.getValueDesc()).append("】")
                    .append("指标阀值:").append("【").append(msg.getThresholdDesc()).append("】")
                    .append("偏离阀值程度:").append("【").append(msg.getOffset()).append("】")
                    .append("\n");
        }
        return buffer.toString();
    }


    @Override
    public void ifAlarmed(Date now, Date baseTime, String alarmTimeKey, BaseAlarmConfigDTO baseAlarmConfigDTO) {
        BoundSetOperations<String, Object> setOperations = redisTemplate.boundSetOps(alarmTimeKey);
        if (setOperations.isMember(MonitorDateUtils.format(baseTime))) {

            logger.info("邮箱监控,预警定时任务执行,已预警,不再预警,jobTime={},baseTime={},config={}",
                    MonitorDateUtils.format(now), MonitorDateUtils.format(baseTime), JSON.toJSONString
                            (baseAlarmConfigDTO));
            throw new BizException("该时段已经预警过");
        }

        setOperations.add(MonitorDateUtils.format(baseTime));
        if (setOperations.getExpire() == -1) {
            setOperations.expire(2, TimeUnit.DAYS);
        }
    }

    @Override
    protected EAlarmLevel determineLevel(List<BaseAlarmMsgDTO> msgList) {
        return msgList.stream().anyMatch(baseAlarmMsgDTO -> EAlarmLevel.error.equals(baseAlarmMsgDTO
                .getAlarmLevel())) ? EAlarmLevel.error : msgList
                .stream().anyMatch(baseAlarmMsgDTO -> EAlarmLevel.warning.equals(baseAlarmMsgDTO.getAlarmLevel()))
                ? EAlarmLevel
                .warning : EAlarmLevel.info;
    }


    private String sendWeChat(List<BaseAlarmMsgDTO> msgList, Date startTime, Date endTime,
                              EAlarmLevel alarmLevel, String alarmBiz) {
        String weChatBody = generateWeChatBody(msgList, startTime, endTime, alarmLevel, alarmBiz);
        alarmMessageProducer.sendWebChart4OperatorMonitor(weChatBody, new Date());
        return weChatBody;
    }

    private void sendSms(List<BaseAlarmMsgDTO> msgList, Date startTime, Date endTime, EAlarmLevel alarmLevel, String alarmBiz) {

        String template = "${level} ${type} 时间段:${startTime}至${endTime},${alarmBiz} " +
                "预警类型:${alarmDesc},偏离阀值程度${offset}%";
        Map<String, Object> placeHolder = Maps.newHashMap();

        List<BaseAlarmMsgDTO> warningMsg = msgList.stream().filter(baseAlarmMsgDTO ->
                EAlarmLevel.warning.equals(baseAlarmMsgDTO.getAlarmLevel())).collect(Collectors.toList());

        String type = "SAAS-" + diamondConfig.getMonitorEnvironment() + "-" + alarmBiz;

        String format = "yyyy-MM-dd HH:mm:SS";
        String startTimeStr = new SimpleDateFormat(format).format(startTime);
        String endTimeStr = new SimpleDateFormat(format).format(endTime);

        BaseAlarmMsgDTO dto = warningMsg.get(0);

        placeHolder.put("level", alarmLevel.name());
        placeHolder.put("type", type);
        placeHolder.put("startTime", startTimeStr);
        placeHolder.put("endTime", endTimeStr);
        placeHolder.put("alarmBiz", alarmBiz);
        placeHolder.put("alarmDesc", dto.getAlarmDesc());
        placeHolder.put("offset", dto.getOffset());

        smsNotifyService.send(StrSubstitutor.replace(template, placeHolder));
    }

    private void sendIvr(List<BaseAlarmMsgDTO> msgList, EAlarmLevel level, String alarmBiz) {

        List<BaseAlarmMsgDTO> errorMsgs = msgList.stream().filter(baseAlarmMsgDTO ->
                level.equals(baseAlarmMsgDTO.getAlarmLevel())).collect(Collectors.toList());

        logger.info(alarmBiz + "发送ivr请求 {}", errorMsgs.get(0).getAlarmDesc());

        ivrNotifyService.notifyIvr(level, EAlarmType.operator_alarm, alarmBiz + errorMsgs.get(0).getAlarmDesc());
    }

    private void sendMail(List<BaseAlarmMsgDTO> msgList, Date jobTime, Date startTime, Date endTime,
                          EAlarmLevel alarmLevel, String alarmBiz) {

        String mailBaseTitle = "【${level}】【${module}】【${type}】发生 ${detail} 预警";

        Map<String, Object> map = new HashMap<>(4);
        map.put("type", alarmBiz);
        map.put("level", alarmLevel.name());

        String mailDataBody = generateMailDataBody(msgList, startTime, endTime, map, alarmLevel, alarmBiz);

        alarmMessageProducer.sendMail4OperatorMonitor(StrSubstitutor.replace(mailBaseTitle, map), mailDataBody, jobTime);
    }

    @Override
    protected String generateSummary(EAlarmLevel alarmLevel, ESaasEnv env, List<BaseAlarmMsgDTO> msgDTOList) {

        return Joiner.on(":").join(EAlarmType.email_alarm.getCode(), alarmLevel.name(), EStatType.EMAIL.getType(), env.getValue(),
                getBizSourceAspect
                        (genBizSourceAspectList(msgDTOList)));
    }



    private List<BizSourceAspect> genBizSourceAspectList(List<BaseAlarmMsgDTO> msgList) {
        List<BizSourceAspect> list = Lists.newArrayList();

        for (BaseAlarmMsgDTO msg : msgList) {
            EmailAlarmMsgDTO opMsg = (EmailAlarmMsgDTO) msg;
            BizSourceAspect sourceAspect = new BizSourceAspect(opMsg.getEmail(), msg.getAlarmAspectType().getValue());
            list.add(sourceAspect);
        }

        return list;
    }

    @Override
    protected String genDutyManAlarmInfo(Long id, Long orderId, List<BaseAlarmMsgDTO> dtoList, EAlarmLevel alarmLevel, Date baseTime, ESaasEnv env, String dutyManName) {
        return null;
    }
}
