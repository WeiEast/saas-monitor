package com.treefinance.saas.monitor.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.config.EmailAlarmConfig;
import com.treefinance.saas.monitor.biz.helper.EmailMonitorKeyHelper;
import com.treefinance.saas.monitor.biz.service.AbstractEmailAlarmServiceTemplate;
import com.treefinance.saas.monitor.common.constants.AlarmConstants;
import com.treefinance.saas.monitor.common.domain.dto.*;
import com.treefinance.saas.monitor.common.enumeration.EAlarmChannel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.dao.entity.EmailStatAccess;
import com.treefinance.saas.monitor.dao.entity.EmailStatAccessCriteria;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.treefinance.saas.monitor.common.constants.AlarmConstants.SWITCH_ON;
import static java.util.stream.Collectors.groupingBy;

/**
 * @author chengtong
 * @date 18/3/13 10:25
 */
@Service("emailAlarmMonitorService")
public class EmailAlarmTemplateImpl extends AbstractEmailAlarmServiceTemplate {

    private static final Logger logger = LoggerFactory.getLogger(EmailAlarmTemplateImpl.class);

    @Autowired
    private EmailAlarmConfig emailAlarmConfig;


    @Override
    public String getKey(ETaskStatDataType type, Date baseTime) {
        return EmailMonitorKeyHelper.genEmailAllKey(baseTime, "virtual_total_stat_appId", type);
    }

    @Override
    public List<BaseStatAccessDTO> getBaseData(Date startTime, Date endTime, ETaskStatDataType statDataType, String
            appId, String... email) {

        EmailStatAccessCriteria criteria = new EmailStatAccessCriteria();

        List<String> emails = Arrays.asList(email);

        criteria.createCriteria().andDataTypeEqualTo(statDataType.getCode())
                .andAppIdEqualTo(appId).andEmailIn(emails)
                .andDataTimeGreaterThanOrEqualTo(startTime)
                .andDataTimeLessThan(endTime);

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
            dataDTO.setLoginConversionRate(calcRate(startLoginCount, entryCount));
            dataDTO.setLoginSuccessRate(calcRate(loginSuccessCount, startLoginCount));
            dataDTO.setCrawlSuccessRate(calcRate(crawlSuccessCount, loginSuccessCount));
            dataDTO.setProcessSuccessRate(calcRate(processSuccessCount, crawlSuccessCount));
            dataDTO.setCallbackSuccessRate(calcRate(callbackSuccessCount, processSuccessCount));
            dataDTO.setWholeConversionRate(calcRate(callbackSuccessCount,entryCount));
            result.add(dataDTO);
        }

        return result;
    }

    @Override
    protected Map<String, BaseStatAccessDTO> getPreviousCompareDataMap(Date jobTime, Date baseTime, List<BaseStatAccessDTO> dtoList, EmailMonitorAlarmConfigDTO configDTO, ETaskStatDataType statType, String... emails) {
        Integer previousDays = configDTO.getPreviousDays();
        List<Date> previousOClockList = MonitorDateUtils.getPreviousOClockTime(baseTime, previousDays);
        List<EmailStatAccessDTO> previousDTOList = Lists.newArrayList();

        for (Date previousOClock : previousOClockList) {
            Date startTime = DateUtils.addMinutes(previousOClock, -configDTO.getIntervalMins());
            Date endTime = previousOClock;
            List<BaseStatAccessDTO> list = this.getBaseData(startTime, endTime, statType, AlarmConstants
                    .VIRTUAL_TOTAL_STAT_APP_ID, emails);
            if (list.isEmpty()) {
                continue;
            }
            previousDTOList.addAll(list.stream().map(baseStatAccessDTO -> (EmailStatAccessDTO) baseStatAccessDTO).collect(Collectors.toList()));
        }

        if (CollectionUtils.isEmpty(previousDTOList)) {
            logger.info("邮箱监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},在此时间前{}天内,未查询到区分邮箱的统计数据emailList={}," +
                            "previousOClockList={},list={}",
                    MonitorDateUtils.format(jobTime), MonitorDateUtils.format(baseTime), previousDays, JSON
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
            compareDto.setPreviousWholeConversionRate(previousWholeConversionRateCount.divide(BigDecimal.valueOf(previousDTOList.size()), 2, BigDecimal.ROUND_HALF_UP));

            compareDto.setPreviousEntryAvgCount(BigDecimal.valueOf(previousEntryCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousStartLoginAvgCount(BigDecimal.valueOf(previousStartLoginCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousLoginSuccessAvgCount(BigDecimal.valueOf(previousLoginSuccessCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousCrawlSuccessAvgCount(BigDecimal.valueOf(previousCrawlSuccessCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousProcessSuccessAvgCount(BigDecimal.valueOf(previousProcessSuccessCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareDto.setPreviousCallbackSuccessAvgCount(BigDecimal.valueOf(previousCallbackSuccessCount).divide(BigDecimal.valueOf(entryList.size()), 1, BigDecimal.ROUND_HALF_UP));
            compareMap.put(entry.getKey(), compareDto);
        }
        return compareMap;
    }


    @Override
    protected List<BaseAlarmMsgDTO> getAlarmMsgList(Date now, List<BaseStatAccessDTO> dtoList, Map<String, BaseStatAccessDTO>
            compareMap, EmailMonitorAlarmConfigDTO configDTO) {

        EmailMonitorAlarmTimeConfigDTO timeConfigDTO = configDTO.getList().stream().filter
                (EmailMonitorAlarmTimeConfigDTO::isInTime).collect
                (Collectors.toList()).get(0);

        Integer previousDays = configDTO.getPreviousDays();

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
                calcConversionRate(configDTO, timeConfigDTO, previousDays, msgList, sourceDto, compareDTO, loginConversionCompareVal);
                //登录成功率小于前7天平均值
                calcLoginSuccessRate(configDTO, timeConfigDTO, previousDays, msgList, sourceDto, compareDTO, loginSuccessCompareVal);
                //抓取成功率小于前7天平均值
                calcCrawSuccessRate(configDTO, timeConfigDTO, previousDays, msgList, sourceDto, compareDTO, crawlCompareVal);
                //洗数成功率小于前7天平均值
                calcProcessRate(configDTO, timeConfigDTO, previousDays, msgList, sourceDto, compareDTO, processCompareVal);
                //回调成功率
                calcCallbackSuccessRate(configDTO, timeConfigDTO, previousDays, msgList, sourceDto, compareDTO, processCompareVal, callbackCompareVal);
            }

            calcWholeConvert(timeConfigDTO, previousDays, msgList, sourceDto, compareDTO, wholeConversionCompareVal);

        }
        msgList = msgList.stream().sorted(Comparator.comparing(BaseAlarmMsgDTO::getDataTime)).collect(Collectors
                .toList());
        return msgList;
    }

    private void calcWholeConvert(EmailMonitorAlarmTimeConfigDTO
                                          timeConfigDTO, Integer previousDays, List<BaseAlarmMsgDTO> msgList, EmailStatAccessDTO sourceDto, EmailStatAccessDTO
                                          compareDTO, BigDecimal wholeConversionCompareVal) {
        if (sourceDto.getWholeConversionRate().compareTo(wholeConversionCompareVal) < 0) {
            EmailAlarmMsgDTO msg = new EmailAlarmMsgDTO();
            String email = AlarmConstants.ALL_EMAIL.equals(sourceDto.getEmail()) ? "邮箱大盘" : sourceDto.getEmail();
            msg.setEmail(email);
            msg.setAlarmDesc("总转化率低于前" + previousDays + "天平均值的" + timeConfigDTO.getWholeConversionRate() + "%");
            msg.setAlarmType("总转化率");
            msg.setAlarmSimpleDesc("总转化率");
            msg.setValue(sourceDto.getWholeConversionRate());
            msg.setThreshold(wholeConversionCompareVal);
            String valueDesc = new StringBuilder()
                    .append(sourceDto.getWholeConversionRate()).append("%").append(" ").append("(")
                    .append(sourceDto.getCallbackSuccessCount()).append("/")
                    .append(sourceDto.getEntryCount()).append(")").toString();
            msg.setValueDesc(valueDesc);
            String thresholdDesc = new StringBuilder()
                    .append(wholeConversionCompareVal).append("%").append(" ").append("(")
                    .append(compareDTO.getPreviousCallbackSuccessAvgCount()).append("/")
                    .append(compareDTO.getPreviousEntryAvgCount()).append("*")
                    .append(new BigDecimal(timeConfigDTO.getWholeConversionRate()).divide(new BigDecimal(100), 1, BigDecimal
                            .ROUND_HALF_UP)).append(")").toString();
            msg.setThresholdDesc(thresholdDesc);
            if (BigDecimal.ZERO.compareTo(wholeConversionCompareVal) == 0) {
                msg.setOffset(BigDecimal.ZERO);
            } else {
                BigDecimal value = BigDecimal.ONE.subtract(sourceDto.getWholeConversionRate().divide
                        (wholeConversionCompareVal, 2, BigDecimal.ROUND_HALF_UP)).multiply(BigDecimal.valueOf(100));
                msg.setOffset(value);
                determineLevel(msg, value);
            }
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
            String valueDesc = new StringBuilder()
                    .append(sourceDto.getCallbackSuccessRate()).append("%").append(" ").append("(")
                    .append(sourceDto.getCallbackSuccessCount()).append("/")
                    .append(sourceDto.getProcessSuccessCount()).append(")").toString();
            msg.setValueDesc(valueDesc);
            String thresholdDesc = new StringBuilder()
                    .append(callbackCompareVal).append("%").append(" ").append("(")
                    .append(compareDTO.getPreviousCallbackSuccessAvgCount()).append("/")
                    .append(compareDTO.getPreviousProcessSuccessAvgCount()).append("*")
                    .append(new BigDecimal(timeConfigDTO.getCallbackSuccessRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
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
            String valueDesc = new StringBuilder()
                    .append(sourceDto.getProcessSuccessRate()).append("%").append(" ").append("(")
                    .append(sourceDto.getProcessSuccessCount()).append("/")
                    .append(sourceDto.getCrawlSuccessCount()).append(")").toString();
            msg.setValueDesc(valueDesc);
            String thresholdDesc = new StringBuilder()
                    .append(processCompareVal).append("%").append(" ").append("(")
                    .append(compareDTO.getPreviousProcessSuccessAvgCount()).append("/")
                    .append(compareDTO.getPreviousCrawlSuccessAvgCount()).append("*")
                    .append(new BigDecimal(timeConfigDTO.getProcessSuccessRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
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
            String valueDesc = new StringBuilder()
                    .append(sourceDto.getCrawlSuccessRate()).append("%").append(" ").append("(")
                    .append(sourceDto.getCrawlSuccessCount()).append("/")
                    .append(sourceDto.getLoginSuccessCount()).append(")").toString();
            msg.setValueDesc(valueDesc);
            String thresholdDesc = new StringBuilder()
                    .append(crawlCompareVal).append("%").append(" ").append("(")
                    .append(compareDTO.getPreviousCrawlSuccessAvgCount()).append("/")
                    .append(compareDTO.getPreviousLoginSuccessAvgCount()).append("*")
                    .append(new BigDecimal(timeConfigDTO.getCrawlSuccessRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
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
            String valueDesc = new StringBuilder()
                    .append(sourceDto.getLoginSuccessRate()).append("%").append(" ").append("(")
                    .append(sourceDto.getLoginSuccessCount()).append("/")
                    .append(sourceDto.getStartLoginCount()).append(")").toString();
            msg.setValueDesc(valueDesc);
            String thresholdDesc = new StringBuilder()
                    .append(loginSuccessCompareVal).append("%").append(" ").append("(")
                    .append(compareDTO.getPreviousLoginSuccessAvgCount()).append("/")
                    .append(compareDTO.getPreviousStartLoginAvgCount()).append("*")
                    .append(new BigDecimal(timeConfigDTO.getLoginSuccessRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
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
            String valueDesc = new StringBuilder()
                    .append(sourceDto.getLoginConversionRate()).append("%").append(" ").append("(")
                    .append(sourceDto.getStartLoginCount()).append("/")
                    .append(sourceDto.getEntryCount()).append(")").toString();
            msg.setValueDesc(valueDesc);
            String thresholdDesc = new StringBuilder()
                    .append(loginConversionCompareVal).append("%").append(" ").append("(")
                    .append(compareDTO.getPreviousStartLoginAvgCount()).append("/")
                    .append(compareDTO.getPreviousConfirmMobileAvgCount()).append("*")
                    .append(new BigDecimal(timeConfigDTO.getLoginConversionRate()).divide(new BigDecimal(100), 1, BigDecimal.ROUND_HALF_UP)).append(")").toString();
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

        boolean isAll = AlarmConstants.ALL_EMAIL.equals(msg.getEmail());

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
    protected void sendAlarmMsg(EAlarmLevel alarmLevel, List<BaseAlarmMsgDTO> dtoList, EmailMonitorAlarmConfigDTO
            configDTO, Date startTime, Date endTime, ETaskStatDataType statDataType) {

        EmailMonitorAlarmLevelConfigDTO levelConfig = configDTO.getLevelConfig().stream().filter
                (emailMonitorAlarmLevelConfigDTO ->
                        alarmLevel.name().equals(emailMonitorAlarmLevelConfigDTO.getLevel())).findFirst().get();

        HashMap<String, String> switches = configDTO.getSwitches();
        if (switches == null || switches.isEmpty() || switches.values().stream().noneMatch(SWITCH_ON::equals)) {
            logger.info("邮箱预警配置 为空 或者预警信息发送渠道全部关闭。。");
            return;
        }


        List<String> channels = levelConfig.getChannels();
        String alarmBiz;
        if ("邮箱大盘".equals(((EmailAlarmMsgDTO) dtoList.get(0)).getEmail())) {
            alarmBiz = "邮箱大盘";
        } else {
            alarmBiz = "邮箱分组";
        }
        alarmBiz += "-" + statDataType.getText();

        for (String channel : channels) {

            EAlarmChannel alarmChannel = EAlarmChannel.getByValue(channel);
            if (alarmChannel == null) {
                logger.info("配置错误 无法找到对应的预警渠道");
                return;
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
                    sendWeChat(dtoList, startTime, endTime, alarmLevel, alarmBiz);
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
    }

    @Override
    protected String generateMailDataBody(List<BaseAlarmMsgDTO> msgList, Date startTime, Date endTime, Map<String,
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

    @Override
    protected String generateWeChatBody(List<BaseAlarmMsgDTO> msgList, Date startTime, Date endTime, EAlarmLevel
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
            buffer.append("【").append(msg.getAlarmSimpleDesc()).append("】")
                    .append("当前指标值:").append("【").append(msg.getValueDesc()).append("】")
                    .append("指标阀值:").append("【").append(msg.getThresholdDesc()).append("】")
                    .append("偏离阀值程度:").append("【").append(msg.getOffset()).append("】")
                    .append("\n");
        }
        return buffer.toString();
    }
}
