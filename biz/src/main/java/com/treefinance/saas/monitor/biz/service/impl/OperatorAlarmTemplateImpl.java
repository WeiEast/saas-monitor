package com.treefinance.saas.monitor.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.treefinance.saas.monitor.biz.config.OperatorMonitorConfig;
import com.treefinance.saas.monitor.biz.helper.TaskOperatorMonitorKeyHelper;
import com.treefinance.saas.monitor.biz.service.AbstractAlarmServiceTemplate;
import com.treefinance.saas.monitor.common.constants.MonitorConstants;
import com.treefinance.saas.monitor.common.domain.dto.*;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.*;
import com.treefinance.saas.monitor.common.domain.dto.alarmconfig.OperatorMonitorAlarmConfigDTO;
import com.treefinance.saas.monitor.common.enumeration.EAlarmChannel;
import com.treefinance.saas.monitor.common.enumeration.EAlarmLevel;
import com.treefinance.saas.monitor.common.enumeration.ETaskStatDataType;
import com.treefinance.saas.monitor.common.utils.DataConverterUtils;
import com.treefinance.saas.monitor.common.utils.MonitorDateUtils;
import com.treefinance.saas.monitor.common.utils.StatisticCalcUtil;
import com.treefinance.saas.monitor.dao.entity.OperatorStatAccess;
import com.treefinance.saas.monitor.dao.entity.OperatorStatAccessCriteria;
import com.treefinance.saas.monitor.exception.BizException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.treefinance.saas.monitor.common.constants.AlarmConstants.*;
import static java.util.stream.Collectors.groupingBy;

/**
 * @author chengtong
 * @date 18/3/13 10:25
 */
@Service("operatorAlarmMonitorService")
public class OperatorAlarmTemplateImpl extends AbstractAlarmServiceTemplate {

    private static final Logger logger = LoggerFactory.getLogger(OperatorAlarmTemplateImpl.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OperatorMonitorConfig operatorMonitorConfig;

    @Override
    public String getKey(ETaskStatDataType type,Date jobTime, BaseAlarmConfigDTO baseAlarmConfigDTO) {
        OperatorMonitorAlarmConfigDTO configDTO = (OperatorMonitorAlarmConfigDTO) baseAlarmConfigDTO;
        return TaskOperatorMonitorKeyHelper.strKeyOfAlarmTimeLog(jobTime, configDTO);
    }

    @Override
    public List<BaseStatAccessDTO> getBaseData(Date baseTime, ETaskStatDataType statDataType, BaseAlarmConfigDTO alarmConfigDTO) {
        OperatorMonitorAlarmConfigDTO config = (OperatorMonitorAlarmConfigDTO) alarmConfigDTO;
        List<String> operatorNameList = Splitter.on(",").splitToList(diamondConfig.getOperatorAlarmOperatorNameList());
        if (CollectionUtils.isEmpty(operatorNameList)) {
            logger.error("运营商监控,预警定时任务执行jobTime={},未配置需要预警的运营商,operator.alarm.operator.name.list未配置",
                    MonitorDateUtils.format(new Date()));
            return Lists.newArrayList();
        }

        Date startTime = DateUtils.addMinutes(baseTime, -config.getIntervalMins());
        List<BaseStatAccessDTO> dataDTOList = doGetBaseData(baseTime,startTime,config);

        //是否需要预警？
        if (CollectionUtils.isEmpty(dataDTOList)) {
            logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻startTime={},endTime={},此段时间内,未查询到运营商的统计数据",
                    MonitorDateUtils.format(new Date()), MonitorDateUtils.format(startTime), MonitorDateUtils.format
                            (baseTime));
            throw new BizException("没有原始数据 无需预警");
        }


        return dataDTOList;
    }

    private List<BaseStatAccessDTO> doGetBaseData(Date baseTime,Date startTime,
                                                  OperatorMonitorAlarmConfigDTO config){

        OperatorStatAccessCriteria criteria = new OperatorStatAccessCriteria();
        OperatorStatAccessCriteria.Criteria innerCriteria = criteria.createCriteria();
        innerCriteria.andAppIdEqualTo(config.getAppId())
                .andDataTypeEqualTo(config.getDataType())
                .andSaasEnvEqualTo(config.getSaasEnv())
                .andDataTimeGreaterThanOrEqualTo(startTime)
                .andDataTimeLessThan(baseTime);

        List<String> operatorNameList = Splitter.on(",").splitToList(diamondConfig.getOperatorAlarmOperatorNameList());

        //总运营商
        if (config.getAlarmType() == 1) {
            innerCriteria.andGroupCodeEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_OPERATOR);
        } else {
            innerCriteria.andGroupNameIn(operatorNameList);
            innerCriteria.andGroupCodeNotEqualTo(MonitorConstants.VIRTUAL_TOTAL_STAT_OPERATOR);
        }
        List<OperatorStatAccess> list = operatorStatAccessMapper.selectByExample(criteria);
        if (CollectionUtils.isEmpty(list)) {
            logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻startTime={},endTime={},此段时间内,未查询到运营商的统计数据list={}",
                    MonitorDateUtils.format(new Date()), MonitorDateUtils.format(startTime), MonitorDateUtils.format
                            (baseTime), JSON.toJSONString(list));
            return Lists.newArrayList();
        }
        List<BaseStatAccessDTO> dataDTOList = Lists.newArrayList();
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

    @Override
    protected Map<String, BaseStatAccessDTO> getPreviousCompareDataMap(Date baseTime, List<BaseStatAccessDTO>
            dtoList, BaseAlarmConfigDTO baseAlarmConfigDTO, ETaskStatDataType statType) {
        OperatorMonitorAlarmConfigDTO config = (OperatorMonitorAlarmConfigDTO) baseAlarmConfigDTO;

        List<String> groupCodeList = dtoList.stream().map(baseStatAccess -> ((OperatorStatAccessDTO)baseStatAccess).getGroupCode())
                .distinct().collect(Collectors.toList());
        Integer previousDays = config.getPreviousDays();
        List<Date> previousOClockList = MonitorDateUtils.getPreviousOClockTime(baseTime, previousDays);
        List<BaseStatAccessDTO> previousDTOList = Lists.newArrayList();
        for (Date previousOClock : previousOClockList) {
            Date startTime = DateUtils.addMinutes(previousOClock, -config.getIntervalMins());
            Date endTime = previousOClock;
            List<BaseStatAccessDTO> list = doGetBaseData(endTime, startTime, config);
            previousDTOList.addAll(list);
        }


        if (CollectionUtils.isEmpty(previousDTOList)) {
            logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},在此时间前{}天内,未查询到运营商统计数据groupCodeList={},previousOClockList={},list={}",
                    MonitorDateUtils.format(new Date()), MonitorDateUtils.format(baseTime), previousDays, JSON.toJSONString
                            (groupCodeList),
                    JSON.toJSONString(previousOClockList), JSON.toJSONString(previousDTOList));
            return Maps.newHashMap();
        }
        //<groupCode,List<OperatorStatAccessDTO>>
        Map<String, List<BaseStatAccessDTO>> previousMap = previousDTOList.stream().collect(groupingBy
                (baseStatAccessDTO -> ((OperatorStatAccessDTO)baseStatAccessDTO).getGroupCode()));
        Map<String, BaseStatAccessDTO> compareMap = Maps.newHashMap();
        for (Map.Entry<String, List<BaseStatAccessDTO>> entry : previousMap.entrySet()) {
            List<BaseStatAccessDTO> entryList = entry.getValue();
            if (CollectionUtils.isEmpty(entryList)) {
                continue;
            }
            //如果列表数量大于1,则去掉相同时段最低的数据,再取平均值,排除数据异常情况.
            if (entryList.size() > 1) {
                entryList = entryList.stream()
                        .sorted((o1, o2) -> ((OperatorStatAccessDTO)o2).getConfirmMobileCount().compareTo
                                (((OperatorStatAccessDTO)o1).getConfirmMobileCount()))
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
            for (BaseStatAccessDTO baseDTO : entryList) {
                OperatorStatAccessDTO dto = (OperatorStatAccessDTO)baseDTO;
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
            compareDto.setPreviousCallbackSuccessRate(previousCallbackSuccessRateCount.divide(BigDecimal.valueOf(entryList.size()), 2, BigDecimal.ROUND_HALF_UP));
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
        logger.info("运营商监控,预警定时任务执行jobTime={},要统计的数据时刻dataTime={},获取前n天内,相同时刻运营商统计的平均值compareMap={}",
                MonitorDateUtils.format(new Date()), MonitorDateUtils.format(baseTime), JSON.toJSONString(compareMap));
        if (MapUtils.isEmpty(compareMap)) {
            throw new BizException();
        }


        return compareMap;
    }


    @Override
    protected List<BaseAlarmMsgDTO> getAlarmMsgList(Date now, List<BaseStatAccessDTO> dtoList, Map<String, BaseStatAccessDTO>
            compareMap, BaseAlarmConfigDTO configDTO) {

        OperatorMonitorAlarmConfigDTO config = (OperatorMonitorAlarmConfigDTO) configDTO;

        List<BaseAlarmMsgDTO> msgList = Lists.newArrayList();
        Integer previousDays = config.getPreviousDays();
        for (BaseStatAccessDTO baseDTO : dtoList) {
            OperatorStatAccessDTO dto = (OperatorStatAccessDTO)baseDTO;

            if (compareMap.get(dto.getGroupCode()) == null) {
                logger.info("运营商监控,预警定时任务执行jobTime={},groupCode={}的运营商前{}天未查询到统计数据",
                        MonitorDateUtils.format(now), dto.getGroupCode(), previousDays);
                continue;
            }

            OperatorStatAccessDTO compareDTO = (OperatorStatAccessDTO)compareMap.get(dto.getGroupCode());
            BigDecimal loginConversionCompareVal = compareDTO.getPreviousLoginConversionRate().multiply(new BigDecimal(config.getLoginConversionRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal loginSuccessCompareVal = compareDTO.getPreviousLoginSuccessRate().multiply(new BigDecimal(config.getLoginSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal crawlCompareVal = compareDTO.getPreviousCrawlSuccessRate().multiply(new BigDecimal(config.getCrawlSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal processCompareVal = compareDTO.getPreviousProcessSuccessRate().multiply(new BigDecimal(config.getProcessSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal callbackCompareVal = compareDTO.getPreviousCallbackSuccessRate().multiply(new BigDecimal(config.getCallbackSuccessRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);

            if (config.getAlarmType() == 1) {
                BigDecimal confirmMobileCompareVal = compareDTO.getPreviousConfirmMobileConversionRate().multiply(new BigDecimal(config.getConfirmMobileConversionRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                BigDecimal wholeConversionCompareVal = compareDTO.getPreviousWholeConversionRate().multiply(new BigDecimal(config.getWholeConversionRate())).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);

                //确认手机转化率小于前7天平均值
                if (dto.getConfirmMobileConversionRate().compareTo(confirmMobileCompareVal) < 0) {
                    OperatorAccessAlarmMsgDTO msg = new OperatorAccessAlarmMsgDTO();
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
                    calcOffsetAndLevel(confirmMobileCompareVal, msg, dto.getConfirmMobileConversionRate());
                    msgList.add(msg);
                }

                //总转化率小于前7天平均值
                if (dto.getWholeConversionRate().compareTo(wholeConversionCompareVal) < 0) {
                    OperatorAccessAlarmMsgDTO msg = new OperatorAccessAlarmMsgDTO();
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
                    calcOffsetAndLevel(wholeConversionCompareVal, msg, dto.getWholeConversionRate());
                    msgList.add(msg);

                }
            }

            //登录转化率小于前7天平均值
            if (isAlarm(dto.getConfirmMobileCount(), dto.getLoginConversionRate(), loginConversionCompareVal)) {
                OperatorAccessAlarmMsgDTO msg = new OperatorAccessAlarmMsgDTO();
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
                OperatorAccessAlarmMsgDTO msg = new OperatorAccessAlarmMsgDTO();
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
                OperatorAccessAlarmMsgDTO msg = new OperatorAccessAlarmMsgDTO();
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
                OperatorAccessAlarmMsgDTO msg = new OperatorAccessAlarmMsgDTO();
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
                OperatorAccessAlarmMsgDTO msg = new OperatorAccessAlarmMsgDTO();
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

                calcOffsetAndLevel(callbackCompareVal, msg, dto.getCallbackSuccessRate());
                msgList.add(msg);
            }

        }
        msgList = msgList.stream().sorted(Comparator.comparing(baseAlarmMsgDTO
                -> ((OperatorAccessAlarmMsgDTO)baseAlarmMsgDTO).getGroupName())).collect(Collectors.toList());


        return msgList;
    }







    private void calcOffsetAndLevel(BigDecimal compareVal, BaseAlarmMsgDTO msg, BigDecimal actualVal) {
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
    private void determineLevel(BaseAlarmMsgDTO msg, BigDecimal value) {
        //所有运营商预警级别定义
        OperatorAccessAlarmMsgDTO msgDTO = (OperatorAccessAlarmMsgDTO)msg;

        if (MonitorConstants.VIRTUAL_TOTAL_STAT_OPERATOR.equals(msgDTO.getGroupCode())) {
            if (value.compareTo(BigDecimal.valueOf(diamondConfig.getErrorLower())) >= 0) {
                msg.setAlarmLevel(EAlarmLevel.error);
            } else if (value.compareTo(BigDecimal.valueOf(diamondConfig.getWarningLower())) > 0) {
                msg.setAlarmLevel(EAlarmLevel.warning);
            } else {
                msg.setAlarmLevel(EAlarmLevel.info);
            }
        } else { //分运营商预警级别定义
            if (value.compareTo(BigDecimal.valueOf(diamondConfig.getErrorLower())) >= 0
                    && "中国联通".equals(msgDTO.getGroupName())) {
                msg.setAlarmLevel(EAlarmLevel.error);
            } else if ("中国联通".equals(msgDTO.getGroupName())) {
                msg.setAlarmLevel(EAlarmLevel.warning);
            } else {
                msg.setAlarmLevel(EAlarmLevel.info);
            }
        }


    }


    @Override
    protected void sendAlarmMsg(EAlarmLevel alarmLevel, List<BaseAlarmMsgDTO> msgList, BaseAlarmConfigDTO
            configDTO, Date endTime, ETaskStatDataType statDataType) {

        OperatorMonitorAlarmConfigDTO config = (OperatorMonitorAlarmConfigDTO)configDTO;
        Date startTime = DateUtils.addMinutes(endTime, -configDTO.getIntervalMins());

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
        String saasEnvDesc = config.getSaasEnvDesc();


//        if (isError) {
//            sendMail(msgList, jobTime, startTime, endTime, statType, baseTitle, mailSwitch, EAlarmLevel.error, saasEnvDesc);
//            sendIvr(msgList, jobTime, ivrSwitch, saasEnvDesc);
//            sendWeChat(msgList, jobTime, startTime, endTime, baseTitle, weChatSwitch, EAlarmLevel.error, saasEnvDesc);
//        } else if (isWarning) {
//            sendMail(msgList, jobTime, startTime, endTime, statType, baseTitle, mailSwitch, EAlarmLevel.warning, saasEnvDesc);
//            sendSms(msgList, jobTime, startTime, endTime, statType, smsSwitch, EAlarmLevel.warning, saasEnvDesc);
//            sendWeChat(msgList, jobTime, startTime, endTime, baseTitle, weChatSwitch, EAlarmLevel.warning, saasEnvDesc);
//        } else {
//            sendMail(msgList, jobTime, startTime, endTime, statType, baseTitle, mailSwitch, EAlarmLevel.info, saasEnvDesc);
//            sendWeChat(msgList, jobTime, startTime, endTime, baseTitle, weChatSwitch, EAlarmLevel.info, saasEnvDesc);
//        }
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
            buffer.append("【").append(((EmailAlarmMsgDTO)msg).getEmail()).append("】").append("【").append(msg
                    .getAlarmSimpleDesc())
                    .append("】")
                    .append("当前指标值:").append("【").append(msg.getValueDesc()).append("】")
                    .append("指标阀值:").append("【").append(msg.getThresholdDesc()).append("】")
                    .append("偏离阀值程度:").append("【").append(msg.getOffset()).append("】")
                    .append("\n");
        }
        return buffer.toString();
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

    @Override
    public void ifAlarmed(Date now,Date baseTime, String alarmTimeKey,BaseAlarmConfigDTO baseAlarmConfigDTO) {
        if (stringRedisTemplate.hasKey(alarmTimeKey)) {
            logger.info("运营商监控,预警定时任务执行,已预警,不再预警,jobTime={},baseTime={},config={}",
                    MonitorDateUtils.format(now), MonitorDateUtils.format(baseTime), JSON.toJSONString
                            (baseAlarmConfigDTO));
            throw new BizException("改时间段已经预警");
        }
        stringRedisTemplate.opsForValue().set(alarmTimeKey, "1");
        stringRedisTemplate.expire(alarmTimeKey, 2, TimeUnit.HOURS);
    }


    @Override
    public EAlarmLevel determineLevel(List<BaseAlarmMsgDTO> msgList){
        Map<String, List<BaseAlarmMsgDTO>> operatorNameGroup = msgList.stream().collect(Collectors
                .groupingBy(operatorAccessAlarmMsgDTO -> ((OperatorAccessAlarmMsgDTO)operatorAccessAlarmMsgDTO).getGroupName()));

        boolean isError = msgList.stream().anyMatch(BaseAlarmMsgDTO -> BaseAlarmMsgDTO
                .getAlarmLevel().equals(EAlarmLevel.error));
        if(isError){
            return EAlarmLevel.error;
        }
        boolean isWarning = msgList.stream().anyMatch(BaseAlarmMsgDTO -> BaseAlarmMsgDTO
                .getAlarmLevel().equals(EAlarmLevel.warning)) || operatorNameGroup.keySet().size() >= 3;

        if(isWarning){
            return EAlarmLevel.warning;
        }

        return EAlarmLevel.info;

    }


}
