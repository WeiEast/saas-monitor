package com.treefinance.saas.monitor.biz.service.impl;

import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.biz.service.AllAlarmService;
import com.treefinance.saas.monitor.common.enumeration.EStatType;
import com.treefinance.saas.monitor.dao.entity.SaasStatAccess;
import com.treefinance.saas.monitor.dao.entity.SaasStatAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.SaasStatAccessMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by haojiahong on 2017/8/4.
 */
@Service
public class AllAlarmServiceImpl implements AllAlarmService {

    private static final Logger logger = LoggerFactory.getLogger(AlarmServiceImpl.class);
    @Autowired
    private DiamondConfig diamondConfig;
    @Autowired
    private AlarmMessageProducer alarmMessageProducer;
    @Autowired
    private SaasStatAccessMapper saasStatAccessMapper;

    @Override
    public void alarm(EStatType type) {
        Integer thresholdCount = diamondConfig.getMonitorAlarmThresholdCount() == null ? 3 : diamondConfig.getMonitorAlarmThresholdCount();
//        String monitorAlarmExcludeAppIds = diamondConfig.getMonitorAlarmExcludeAppIds();
        logger.info("type={} trigger all alarm message", type);

//        // 验证是否排除的预警消息
//        if (StringUtils.isNotEmpty(monitorAlarmExcludeAppIds)) {
//            List<String> excludeAppIds = Splitter.on(",").trimResults().splitToList(monitorAlarmExcludeAppIds);
//            if (excludeAppIds != null && excludeAppIds.contains(appId)) {
//                return;
//            }
//        }

        BigDecimal alarmThreshold = BigDecimal.valueOf(diamondConfig.getMonitorAlarmThreshold());
        SaasStatAccessCriteria criteria = new SaasStatAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.setOffset(0);
        criteria.setLimit(thresholdCount);
        criteria.createCriteria()
                .andSuccessRateLessThan(alarmThreshold)
                .andDataTimeLessThanOrEqualTo(new Date())
                .andDataTypeEqualTo(type.getType());
        List<SaasStatAccess> list = saasStatAccessMapper.selectPaginationByExample(criteria);
        alarmMessageProducer.sendMail4All(list, type);
        alarmMessageProducer.sendWebChart4All(list, type);
    }
}
