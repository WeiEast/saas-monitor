package com.treefinance.saas.monitor.biz.service.newmonitor.task.impl;

import com.alibaba.fastjson.JSON;
import com.treefinance.saas.assistant.model.TaskMonitorMessage;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.helper.TaskMonitorPerMinKeyHelper;
import com.treefinance.saas.monitor.biz.service.EcommerceService;
import com.treefinance.saas.monitor.biz.service.OperatorService;
import com.treefinance.saas.monitor.biz.service.WebsiteService;
import com.treefinance.saas.monitor.biz.service.newmonitor.task.TaskMonitorPerMinRedisProcessor;
import com.treefinance.saas.monitor.biz.service.newmonitor.task.TaskMonitorPerMinService;
import com.treefinance.saas.monitor.common.domain.dto.EcommerceDTO;
import com.treefinance.saas.monitor.common.domain.dto.OperatorDTO;
import com.treefinance.saas.monitor.common.domain.dto.WebsiteDTO;
import com.treefinance.saas.monitor.common.enumeration.EBizType;
import com.treefinance.saas.monitor.common.enumeration.EStatType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by haojiahong on 2017/11/23.
 */
@Service
public class TaskMonitorPerMinServiceImpl implements TaskMonitorPerMinService {
    private static final Logger logger = LoggerFactory.getLogger(TaskMonitorPerMinService.class);

    @Autowired
    private TaskMonitorPerMinRedisProcessor taskMonitorPerMinRedisProcessor;
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private EcommerceService ecommerceService;
    @Autowired
    private WebsiteService websiteService;
    @Autowired
    private OperatorService operatorService;


    @Override
    public void statMerchantAccessWithType(TaskMonitorMessage message) {
        try {
            if (StringUtils.isBlank(message.getAppId()) || message.getStatus() == null || message.getBizType() == null) {
                logger.error("任务监控,消息处理,收到的消息appId,status,bizType为空,message={}", JSON.toJSONString(message));
                return;
            }
            Date taskCreateTime = message.getCompleteTime();
            //给merchant商户系统的监控暂时还是使用10分钟
            Date redisKeyTime = TaskMonitorPerMinKeyHelper.getRedisStatDateTime(taskCreateTime, diamondConfig.getMonitorIntervalMinutes());
            EBizType type = EBizType.getBizType(message.getBizType());
            if (type == null) {
                logger.error("任务预警,任务预警消息处理,任务业务类型有误,任务消息message={}", JSON.toJSONString(message));
                return;
            }
            String website = message.getWebSite();
            switch (type) {
                case ECOMMERCE:
                    EcommerceDTO ecommerceDTO = ecommerceService.getEcommerceByWebsite(website);
                    Short ecommerceId = ecommerceDTO != null ? ecommerceDTO.getId() : null;
                    if (ecommerceId != null) {
                        taskMonitorPerMinRedisProcessor.updateMerchantAccessWithType(redisKeyTime, message, EStatType.ECOMMERCE, ecommerceId + "");
                    }
                    break;
                case EMAIL:
                    WebsiteDTO websiteDTO = websiteService.getWebsiteByName(website);
                    String mailCode = websiteDTO != null ? websiteDTO.getWebsiteName() : null;
                    if (StringUtils.isNotBlank(mailCode)) {
                        taskMonitorPerMinRedisProcessor.updateMerchantAccessWithType(redisKeyTime, message, EStatType.EMAIL, mailCode);
                    }
                    break;
                case OPERATOR:
                    OperatorDTO operatorDTO = operatorService.getOperatorByWebsite(website);
                    String operatorId = operatorDTO != null ? operatorDTO.getId().toString() : null;
                    if (operatorId != null) {
                        taskMonitorPerMinRedisProcessor.updateMerchantAccessWithType(redisKeyTime, message, EStatType.OPERATOR, operatorId + "");
                    }
                    break;
                case FUND:
                    //公积金暂时没有建表
                    break;
                default:
                    logger.error("任务监控,消息处理,收到的消息业务类型有误,message={}", JSON.toJSONString(message));
                    break;

            }
        } catch (Exception e) {
            logger.error("任务监控,消息处理,商户银行访问统计表,商户邮箱访问统计表,商户邮箱访问统计表,商户邮箱访问统计表,以及后续会添加其他业务类型处理异常,message={}",
                    JSON.toJSONString(message), e);
        }


    }

    @Override
    public void statSaasErrorStepDay(TaskMonitorMessage message) {
        if (StringUtils.isBlank(message.getAppId()) || message.getStatus() == null || message.getBizType() == null) {
            logger.error("任务监控,消息处理,收到的消息appId,status,bizType为空,message={}", JSON.toJSONString(message));
            return;
        }
        //任务失败环节统计
        if (StringUtils.isBlank(message.getStepCode())) {
            return;
        }
        Date taskCreateTime = message.getCompleteTime();
        Date redisKeyTime = TaskMonitorPerMinKeyHelper.getRedisStatDateTime(taskCreateTime, 1);
        taskMonitorPerMinRedisProcessor.updateSaasErrorStepDay(redisKeyTime, message, EStatType.TOTAL);
        EBizType type = EBizType.getBizType(message.getBizType());
        if (type == null) {
            logger.error("任务预警,任务预警消息处理,任务业务类型有误,任务消息message={}", JSON.toJSONString(message));
            return;
        }
        switch (type) {
            case ECOMMERCE:
                taskMonitorPerMinRedisProcessor.updateSaasErrorStepDay(redisKeyTime, message, EStatType.ECOMMERCE);
                break;
            case EMAIL:
                taskMonitorPerMinRedisProcessor.updateSaasErrorStepDay(redisKeyTime, message, EStatType.EMAIL);
                break;
            case OPERATOR:
                taskMonitorPerMinRedisProcessor.updateSaasErrorStepDay(redisKeyTime, message, EStatType.OPERATOR);
                break;
            case FUND:
                taskMonitorPerMinRedisProcessor.updateSaasErrorStepDay(redisKeyTime, message, EStatType.FUND);
                break;
            default:
                logger.error("任务监控,消息处理,收到的消息业务类型有误,message={}", JSON.toJSONString(message));
                break;

        }


    }
}
