package com.treefinance.saas.monitor.biz.service.newmonitor.task.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.treefinance.commonservice.uid.UidService;
import com.treefinance.saas.monitor.biz.enums.TaskStepEnum;
import com.treefinance.saas.monitor.biz.helper.TaskMonitorPerMinKeyHelper;
import com.treefinance.saas.monitor.biz.service.EcommerceService;
import com.treefinance.saas.monitor.biz.service.OperatorService;
import com.treefinance.saas.monitor.biz.service.SaasErrorStepDayStatUpdateService;
import com.treefinance.saas.monitor.biz.service.StatAccessUpdateService;
import com.treefinance.saas.monitor.biz.service.WebsiteService;
import com.treefinance.saas.monitor.biz.service.newmonitor.task.TaskPerMinDataFlushService;
import com.treefinance.saas.monitor.common.domain.dto.EcommerceDTO;
import com.treefinance.saas.monitor.common.domain.dto.MerchantStatEcommerceDTO;
import com.treefinance.saas.monitor.common.domain.dto.MerchantStatMailDTO;
import com.treefinance.saas.monitor.common.domain.dto.MerchantStatOperatorDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorDTO;
import com.treefinance.saas.monitor.common.domain.dto.SaasErrorStepDayStatDTO;
import com.treefinance.saas.monitor.common.domain.dto.WebsiteDTO;
import com.treefinance.saas.monitor.common.enumeration.EStatType;
import com.treefinance.toolkit.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by haojiahong on 2017/11/23.
 */
@Service
public class TaskPerMinDataFlushServiceImpl implements TaskPerMinDataFlushService {

    private static final Logger logger = LoggerFactory.getLogger(TaskPerMinDataFlushService.class);
    @Autowired
    private StatAccessUpdateService statAccessUpdateService;
    @Autowired
    private SaasErrorStepDayStatUpdateService saasErrorStepDayStatUpdateService;
    @Autowired
    private EcommerceService ecommerceService;
    @Autowired
    private WebsiteService websiteService;
    @Autowired
    private OperatorService operatorService;
    @Resource
    private UidService uidService;


    @Override
    public void statSaasErrorStepDay(RedisOperations redisOperations, Date jobTime) {
        try {
            List<SaasErrorStepDayStatDTO> list = Lists.newArrayList();
            for (EStatType statType : EStatType.values()) {
                for (TaskStepEnum taskStep : TaskStepEnum.values()) {
                    String hashKey = TaskMonitorPerMinKeyHelper.keyOfSaasErrorStepDay(jobTime, statType, taskStep.getStepCode());
                    if (!redisOperations.hasKey(hashKey)) {
                        continue;
                    }
                    Map<String, Object> dataMap = redisOperations.opsForHash().entries(hashKey);
                    logger.info("任务监控,定时任务执行jobTime={},刷新所有运营商按时间段统计数据到db中,key={},data={}",
                        DateUtils.format(jobTime), hashKey, JSON.toJSONString(dataMap));
                    if (MapUtils.isEmpty(dataMap)) {
                        continue;
                    }
                    String json = JSON.toJSONString(dataMap);
                    SaasErrorStepDayStatDTO dto = JSON.parseObject(json, SaasErrorStepDayStatDTO.class);
                    dto.setId(uidService.getId());
                    if (dto.getFailCount() == null) {
                        dto.setFailCount(0);
                    }
                    dto.setLastUpdateTime(new Date());
                    list.add(dto);
                }

            }

            if (CollectionUtils.isNotEmpty(list)) {
                logger.info("任务监控,定时任务执行jobTime={},刷新数据到db中list={}", DateUtils.format(jobTime), JSON.toJSONString(list));
                saasErrorStepDayStatUpdateService.batchInsertErrorDayStat(list);
            }
        } catch (Exception e) {
            logger.error("任务监控,定时任务执行jobTime={},刷新数据到db中异常", DateUtils.format(jobTime), e);
        }

    }

    @Override
    public void statMerchantAccessWithType(RedisOperations redisOperations, Date jobTime) {
        try {
            for (EStatType statType : EStatType.values()) {
                switch (statType) {
                    case ECOMMERCE:
                        List<EcommerceDTO> ecommerceList = ecommerceService.getAll();
                        List<String> ecommerceAccountList = ecommerceList.stream().map(ecommerceDTO -> ecommerceDTO.getId().toString()).collect(Collectors.toList());
                        List<String> ecommerceResultList = this.statMerchantAccessWithTypeAccount(redisOperations, jobTime, ecommerceAccountList, EStatType.ECOMMERCE);
                        List<MerchantStatEcommerceDTO> ecommerceDataList = Lists.newArrayList();
                        for (String json : ecommerceResultList) {
                            MerchantStatEcommerceDTO dto = JSON.parseObject(json, MerchantStatEcommerceDTO.class);
                            Object account = JSONObject.parseObject(json).get("account");
                            if (account != null) {
                                dto.setEcommerceId(Short.valueOf(account.toString()));
                            }
                            if (dto.getTotalCount() == null) {
                                dto.setTotalCount(0);
                            }
                            if (dto.getSuccessCount() == null) {
                                dto.setSuccessCount(0);
                            }
                            if (dto.getFailCount() == null) {
                                dto.setFailCount(0);
                            }
                            if (dto.getCancelCount() == null) {
                                dto.setCancelCount(0);
                            }
                            dto.setSuccessRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getSuccessCount()));
                            dto.setFailRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getFailCount()));
                            dto.setLastUpdateTime(new Date());
                            ecommerceDataList.add(dto);
                        }
                        if (CollectionUtils.isNotEmpty(ecommerceDataList)) {
                            logger.info("任务监控,定时任务执行jobTime={},刷新数据到db中list={}", DateUtils.format(jobTime), JSON.toJSONString(ecommerceDataList));
                            statAccessUpdateService.batchInsertEcommerce(ecommerceDataList);
                        }
                        break;
                    case EMAIL:
                        List<WebsiteDTO> mails = websiteService.getSupportMails();
                        List<String> mailAccountList = mails.stream().map(WebsiteDTO::getWebsiteName).distinct().collect(Collectors.toList());
                        List<String> mailResultList = this.statMerchantAccessWithTypeAccount(redisOperations, jobTime, mailAccountList, EStatType.EMAIL);
                        List<MerchantStatMailDTO> mailDataList = Lists.newArrayList();
                        for (String json : mailResultList) {
                            MerchantStatMailDTO dto = JSON.parseObject(json, MerchantStatMailDTO.class);
                            Object account = JSONObject.parseObject(json).get("account");
                            if (account != null) {
                                dto.setMailCode(account.toString());
                            }
                            if (dto.getTotalCount() == null) {
                                dto.setTotalCount(0);
                            }
                            if (dto.getSuccessCount() == null) {
                                dto.setSuccessCount(0);
                            }
                            if (dto.getFailCount() == null) {
                                dto.setFailCount(0);
                            }
                            if (dto.getCancelCount() == null) {
                                dto.setCancelCount(0);
                            }
                            dto.setSuccessRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getSuccessCount()));
                            dto.setFailRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getFailCount()));
                            dto.setLastUpdateTime(new Date());
                            mailDataList.add(dto);
                        }
                        if (CollectionUtils.isNotEmpty(mailDataList)) {
                            logger.info("任务监控,定时任务执行jobTime={},刷新数据到db中list={}", DateUtils.format(jobTime), JSON.toJSONString(mailDataList));
                            statAccessUpdateService.batchInsertMail(mailDataList);
                        }
                        break;
                    case OPERATOR:
                        List<OperatorDTO> operators = operatorService.getAll();
                        List<String> operatorAccountList = operators.stream().map(operatorDTO -> operatorDTO.getId().toString()).distinct().collect(Collectors.toList());
                        List<String> operatorResultList = this.statMerchantAccessWithTypeAccount(redisOperations, jobTime, operatorAccountList, EStatType.OPERATOR);
                        List<MerchantStatOperatorDTO> operatorDataList = Lists.newArrayList();
                        for (String json : operatorResultList) {
                            MerchantStatOperatorDTO dto = JSON.parseObject(json, MerchantStatOperatorDTO.class);
                            Object account = JSONObject.parseObject(json).get("account");
                            if (account != null) {
                                dto.setOperaterId(account.toString());
                            }
                            if (dto.getTotalCount() == null) {
                                dto.setTotalCount(0);
                            }
                            if (dto.getSuccessCount() == null) {
                                dto.setSuccessCount(0);
                            }
                            if (dto.getFailCount() == null) {
                                dto.setFailCount(0);
                            }
                            if (dto.getCancelCount() == null) {
                                dto.setCancelCount(0);
                            }
                            dto.setSuccessRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getSuccessCount()));
                            dto.setFailRate(calcRate(dto.getTotalCount(), dto.getCancelCount(), dto.getFailCount()));
                            dto.setLastUpdateTime(new Date());
                            operatorDataList.add(dto);
                        }
                        if (CollectionUtils.isNotEmpty(operatorDataList)) {
                            logger.info("任务监控,定时任务执行jobTime={},刷新数据到db中list={}", DateUtils.format(jobTime), JSON.toJSONString(operatorDataList));
                            statAccessUpdateService.batchInsertOperator(operatorDataList);
                        }
                        break;
                    default://其他类型无相关表,不统计
                        break;
                }
            }
        } catch (NumberFormatException e) {
            logger.error("任务监控,定时任务执行jobTime={},刷新数据到db中异常", DateUtils.format(jobTime), e);
        }
    }

    private List<String> statMerchantAccessWithTypeAccount(RedisOperations redisOperations, Date jobTime, List<String> accountList, EStatType statType) {
        List<String> resultList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(accountList)) {
            return resultList;
        }
        Set<String> appIdSet = redisOperations.opsForSet().members(TaskMonitorPerMinKeyHelper.keyOfAppIds());
        for (String appId : appIdSet) {
            for (String account : accountList) {
                String dayKey = TaskMonitorPerMinKeyHelper.keyOfDayOnMerchantWithTypeIntervalStat(jobTime, appId, account, statType);
                Set<String> redisStatDataTimeStrSets = redisOperations.opsForSet().members(dayKey);
                if (CollectionUtils.isEmpty(redisStatDataTimeStrSets)) {
                    continue;
                }
                logger.info("任务监控,定时任务执行jobTime={},保存统计数据,此次任务需要统计的时间段有dayKey={},dataTimeStrSets={}",
                    DateUtils.format(jobTime), dayKey, JSON.toJSONString(redisStatDataTimeStrSets));
                Set<Date> redisStatDataTimeSets = Sets.newHashSet();
                for (String dateStr : redisStatDataTimeStrSets) {
                    List<String> strList = Splitter.on(";").splitToList(dateStr);
                    Date date = DateUtils.parse(strList.get(0));
                    redisStatDataTimeSets.add(date);
                }
                if (CollectionUtils.isEmpty(redisStatDataTimeSets)) {
                    continue;
                }
                for (Date redisStatDataTime : redisStatDataTimeSets) {
                    String hashKey = TaskMonitorPerMinKeyHelper.keyOfMerchantWithTypeIntervalStat(redisStatDataTime, appId, statType, dayKey);
                    if (!redisOperations.hasKey(hashKey)) {
                        continue;
                    }
                    Map<String, Object> dataMap = redisOperations.opsForHash().entries(hashKey);
                    logger.info("任务监控,定时任务执行jobTime={},刷新数据到db中,key={},data={}",
                        DateUtils.format(jobTime), hashKey, JSON.toJSONString(dataMap));
                    if (MapUtils.isEmpty(dataMap)) {
                        continue;
                    }
                    String json = JSON.toJSONString(dataMap);
                    resultList.add(json);
                }
                if (CollectionUtils.isNotEmpty(redisStatDataTimeStrSets)) {
                    logger.info("任务监控,定时任务执行jobTime={},刷新数据到db后,删除dayKey={}中已统计数据时间dataTimeSet={},dataTimeStrSets={}",
                            DateUtils.format(jobTime), dayKey, JSON.toJSONString(redisStatDataTimeSets), JSON.toJSONString(redisStatDataTimeStrSets));
                    String[] values = redisStatDataTimeStrSets.toArray(new String[0]);
                    redisOperations.opsForSet().remove(dayKey, values);
                }
            }
        }
        return resultList;
    }


    /**
     * 计算比率
     *
     * @param totalCount 总数
     * @param rateCount  比率数
     * @return
     */
    private BigDecimal calcRate(Integer totalCount, Integer cancelCount, Integer rateCount) {
        if (cancelCount != null) {
            totalCount -= cancelCount;
        }
        if (totalCount == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = BigDecimal.valueOf(rateCount, 2)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalCount, 2), 2);
        return rate;
    }
}
