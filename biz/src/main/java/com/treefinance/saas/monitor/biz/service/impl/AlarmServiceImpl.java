package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.biz.service.AlarmService;
import com.treefinance.saas.monitor.common.enumeration.EStatType;
import com.treefinance.saas.monitor.dao.entity.MerchantStatAccess;
import com.treefinance.saas.monitor.dao.entity.MerchantStatAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.MerchantStatAccessMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by yh-treefinance on 2017/6/30.
 */
@Service
public class AlarmServiceImpl implements AlarmService {
    private static final Logger logger = LoggerFactory.getLogger(AlarmServiceImpl.class);
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private MerchantStatAccessMapper merchantStatAccessMapper;
    @Autowired
    private AlarmMessageProducer alarmMessageProducer;

    @Override
    public void alarm(String appId, EStatType type) {
        logger.info("alarm appId={},type={} ", appId, type);
        Integer thresholdCount = diamondConfig.getMonitorAlarmThresholdCount() == null ? 3 : diamondConfig.getMonitorAlarmThresholdCount();

        BigDecimal alarmThreshold = BigDecimal.valueOf(diamondConfig.getMonitorAlarmThreshold());
        MerchantStatAccessCriteria criteria = new MerchantStatAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.setOffset(0);
        criteria.setLimit(thresholdCount);
        criteria.createCriteria()
                .andSuccessRateLessThan(alarmThreshold)
                .andDataTimeLessThanOrEqualTo(new Date())
                .andDataTypeEqualTo(type.getType())
                .andAppIdEqualTo(appId);
        List<MerchantStatAccess> list = merchantStatAccessMapper.selectByExample(criteria);
        alarmMessageProducer.sendMail(list, type);
        alarmMessageProducer.sendWebChart(list, type);
    }
}
