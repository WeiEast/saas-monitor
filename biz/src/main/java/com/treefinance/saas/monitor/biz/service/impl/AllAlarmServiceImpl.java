package com.treefinance.saas.monitor.biz.service.impl;

import com.alibaba.fastjson.JSON;
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

import java.util.Date;
import java.util.List;

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

    @Override
    public void alarm(EStatType type, List<Date> alarmTimes) {
        logger.info("type={} alarmTimes={} trigger all alarm message", type, JSON.toJSONString(alarmTimes));
        SaasStatAccessCriteria criteria = new SaasStatAccessCriteria();
        criteria.setOrderByClause("dataTime desc");
        criteria.createCriteria()
                .andDataTimeIn(alarmTimes)
                .andDataTypeEqualTo(type.getType());
        List<SaasStatAccess> list = saasStatAccessMapper.selectByExample(criteria);
        alarmMessageProducer.sendMail4All(list, type);
        alarmMessageProducer.sendWebChart4All(list, type);
    }
}
