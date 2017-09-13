package com.treefinance.saas.monitor.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.treefinance.saas.monitor.biz.config.DiamondConfig;
import com.treefinance.saas.monitor.biz.mq.producer.AlarmMessageProducer;
import com.treefinance.saas.monitor.biz.service.AllAlarmService;
import com.treefinance.saas.monitor.common.enumeration.EStatType;
import com.treefinance.saas.monitor.dao.entity.MerchantStatAccess;
import com.treefinance.saas.monitor.dao.entity.MerchantStatAccessCriteria;
import com.treefinance.saas.monitor.dao.entity.SaasStatAccess;
import com.treefinance.saas.monitor.dao.entity.SaasStatAccessCriteria;
import com.treefinance.saas.monitor.dao.mapper.MerchantStatAccessMapper;
import com.treefinance.saas.monitor.dao.mapper.SaasStatAccessMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by haojiahong on 2017/8/4.
 */
@Service
public class AllAlarmServiceImpl implements AllAlarmService {

    private static final Logger logger = LoggerFactory.getLogger(AlarmServiceImpl.class);
    @Autowired
    private AlarmMessageProducer alarmMessageProducer;
    @Autowired
    private SaasStatAccessMapper saasStatAccessMapper;
    @Autowired
    private MerchantStatAccessMapper merchantStatAccessMapper;
    @Autowired
    private DiamondConfig diamondConfig;


    @Override
    public void alarm(EStatType type, List<Date> alarmTimes) {
        logger.info("TaskMonitorAlarm:type={} alarmTimes={} trigger all alarm message", type, JSON.toJSONString(alarmTimes));
        if (CollectionUtils.isEmpty(alarmTimes)) {
            return;
        }
        SaasStatAccessCriteria criteria = new SaasStatAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.createCriteria()
                .andDataTimeIn(alarmTimes)
                .andDataTypeEqualTo(type.getType());
        List<SaasStatAccess> list = saasStatAccessMapper.selectByExample(criteria);

        //获取需要排除的商户的统计信息
        List<String> excludeAppIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(diamondConfig.getMonitorAlarmExcludeAppIdsAll())) {
            excludeAppIds = Splitter.on(",").trimResults().splitToList(diamondConfig.getMonitorAlarmExcludeAppIdsAll());
            logger.info("TaskMonitorAlarm: excludeAppIds={}", JSON.toJSONString(excludeAppIds));
        }
        if (!CollectionUtils.isEmpty(excludeAppIds)) {
            MerchantStatAccessCriteria merchantCriteria = new MerchantStatAccessCriteria();
            merchantCriteria.setOrderByClause("dataTime desc");
            merchantCriteria.createCriteria().andAppIdIn(excludeAppIds)
                    .andDataTimeIn(alarmTimes)
                    .andDataTypeEqualTo(type.getType());
            List<MerchantStatAccess> merchantStatAccessList = merchantStatAccessMapper.selectByExample(merchantCriteria);
            logger.info("TaskMonitorAlarm: excludeMerchantStatAccessList={}", JSON.toJSONString(merchantStatAccessList));
            Map<Date, List<MerchantStatAccess>> excludeMerchantMap = merchantStatAccessList.stream().collect(Collectors.groupingBy(MerchantStatAccess::getDataTime));
            List<SaasStatAccess> excludedList = Lists.newArrayList();
            for (SaasStatAccess data : list) {
                SaasStatAccess saasStatAccess = new SaasStatAccess();
                BeanUtils.copyProperties(data, saasStatAccess);
                int excludeTotalCount = 0, excludeSuccessCount = 0, excludeFailCount = 0, excludeCancelCount = 0;
                List<MerchantStatAccess> excludeMerchantList = excludeMerchantMap.get(data.getDataTime());
                if (!CollectionUtils.isEmpty(excludeMerchantList)) {
                    for (MerchantStatAccess merchantStatAccess : excludeMerchantList) {
                        excludeTotalCount = excludeTotalCount + (merchantStatAccess.getTotalCount() == null ? 0 : merchantStatAccess.getTotalCount());
                        excludeSuccessCount = excludeSuccessCount + (merchantStatAccess.getSuccessCount() == null ? 0 : merchantStatAccess.getSuccessCount());
                        excludeFailCount = excludeFailCount + (merchantStatAccess.getFailCount() == null ? 0 : merchantStatAccess.getFailCount());
                        excludeCancelCount = excludeCancelCount + (merchantStatAccess.getCancelCount() == null ? 0 : merchantStatAccess.getCancelCount());
                    }
                }
                int totalCount = saasStatAccess.getTotalCount() == null ? 0 : saasStatAccess.getTotalCount();
                int successCount = saasStatAccess.getSuccessCount() == null ? 0 : saasStatAccess.getSuccessCount();
                int failCount = saasStatAccess.getFailCount() == null ? 0 : saasStatAccess.getFailCount();
                int cancelCount = saasStatAccess.getCancelCount() == null ? 0 : saasStatAccess.getCancelCount();

                saasStatAccess.setTotalCount(totalCount - excludeTotalCount);
                saasStatAccess.setSuccessCount(successCount - excludeSuccessCount);
                saasStatAccess.setFailCount(failCount - excludeFailCount);
                saasStatAccess.setCancelCount(cancelCount - excludeCancelCount);
                excludedList.add(saasStatAccess);
            }
            list = excludedList;
        }
        alarmMessageProducer.sendMail4All(list, type);
        alarmMessageProducer.sendWebChart4All(list, type);
    }
}
